package com.example.gabinsbar.ui.connexion

import SessionManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gabinsbar.R
import com.example.gabinsbar.databinding.FragmentConnexionBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigInteger
import java.security.MessageDigest

class ConnexionFragment : Fragment() {

    private var _binding: FragmentConnexionBinding? = null
    private val binding get() = _binding!!

    lateinit var editTextPersonName: EditText
    lateinit var editTextPersonName2: EditText
    lateinit var buttonLogin: Button
    lateinit var editTextPseudo: EditText
    lateinit var editTextPassword: EditText
    lateinit var editTextPasswordConfirm: EditText
    lateinit var buttonCreateAccount: Button
    lateinit var buttonOpenCreateAccount: Button
    lateinit var createAccountCard: CardView

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentConnexionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorSecondary)
        FirebaseApp.initializeApp(requireActivity())

        editTextPersonName = binding.root.findViewById(R.id.editTextPersonName)
        editTextPersonName2 = binding.root.findViewById(R.id.editTextPersonName2)
        buttonLogin = binding.root.findViewById(R.id.buttonLogin)
        editTextPseudo = binding.root.findViewById(R.id.editTextPseudo)
        editTextPassword = binding.root.findViewById(R.id.editTextPassword)
        editTextPasswordConfirm = binding.root.findViewById(R.id.editTextPasswordConfirm)
        buttonCreateAccount = binding.root.findViewById(R.id.buttonCreateAccount)
        buttonOpenCreateAccount = binding.root.findViewById(R.id.buttonOpenCreateAccount)
        createAccountCard = binding.root.findViewById(R.id.createAccountCard)

        firestore = FirebaseFirestore.getInstance()

        buttonOpenCreateAccount.setOnClickListener {
            if(createAccountCard.visibility == View.VISIBLE) {
                createAccountCard.visibility = View.INVISIBLE
            } else {
                createAccountCard.visibility = View.VISIBLE
            }
        }

        buttonLogin.setOnClickListener {
            val username = editTextPersonName.text.toString().trim()
            val password = editTextPersonName2.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password)
            } else {
                Toast.makeText(requireContext(), "Veuillez saisir un nom d'utilisateur et un mot de passe.", Toast.LENGTH_SHORT).show()
            }
        }

        buttonCreateAccount.setOnClickListener {
            val username = editTextPseudo.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                ajouterUtilisateur(username, password)
            } else {
                Toast.makeText(requireContext(), "Veuillez saisir un nom d'utilisateur et un mot de passe.", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    private fun ajouterUtilisateur(pseudo: String, motDePasse: String) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("user_gabinsbar")

        usersCollection.get().addOnSuccessListener { documents ->
            var nextId = documents.size() + 1

            val user = hashMapOf(
                "id" to pseudo,
                "droits" to "invite",
                "motdepasse" to encryptPassword(motDePasse)
            )

            usersCollection.document("user$nextId").set(user)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Utilisateur ajouté avec succès", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erreur lors de l'ajout de l'utilisateur : $e", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun encryptPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        val hashedPassword = BigInteger(1, hashedBytes).toString(16)

        return hashedPassword.padStart(64, '0')
    }

    private fun loginUser(username: String, password: String) {
        val usersCollection = firestore.collection("user_gabinsbar")
        usersCollection.get().addOnSuccessListener { documents ->
            var userFound = false
            for (document in documents) {
                if (document.getString("id") == username && document.getString("motdepasse") == encryptPassword(password)) {
                    userFound = true
                    Toast.makeText(requireContext(), "Connexion réussie", Toast.LENGTH_SHORT).show()
                    SessionManager.isLoggedIn = true
                    SessionManager.pseudo = username
                    SessionManager.droits = document.getString("droits")!!
                    break
                }
            }
            if (!userFound) {
                Toast.makeText(requireContext(), "Nom d'utilisateur ou mot de passe incorrect.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Erreur lors de la récupération des données: $exception", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        editTextPersonName.text.clear()
        editTextPersonName2.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
