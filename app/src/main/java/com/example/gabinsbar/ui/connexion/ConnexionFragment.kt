package com.example.gabinsbar.ui.connexion

import SessionManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

    // Liaison avec le layout via le binding
    private var _binding: FragmentConnexionBinding? = null
    private val binding get() = _binding!!

    // Déclaration des variables des composants de l'interface utilisateur
    lateinit var editTextPersonName: EditText
    lateinit var editTextPersonName2: EditText
    lateinit var buttonLogin: Button
    lateinit var editTextPseudo: EditText
    lateinit var editTextPassword: EditText
    lateinit var editTextPasswordConfirm: EditText
    lateinit var buttonCreateAccount: Button
    lateinit var createAccountCard: CardView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var erreurChargement: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Initialisation du binding
        _binding = FragmentConnexionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Modification de la couleur de la barre de statut
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorSecondary)

        // Initialisation de Firebase
        FirebaseApp.initializeApp(requireActivity())

        // Liaison des composants de l'interface utilisateur avec les éléments du layout
        editTextPersonName = binding.root.findViewById(R.id.editTextPersonName)
        editTextPersonName2 = binding.root.findViewById(R.id.editTextPersonName2)
        buttonLogin = binding.root.findViewById(R.id.buttonLogin)
        editTextPseudo = binding.root.findViewById(R.id.editTextPseudo)
        editTextPassword = binding.root.findViewById(R.id.editTextPassword)
        editTextPasswordConfirm = binding.root.findViewById(R.id.editTextPasswordConfirm)
        buttonCreateAccount = binding.root.findViewById(R.id.buttonCreateAccount)
        createAccountCard = binding.root.findViewById(R.id.createAccountCard)
        erreurChargement = root.findViewById(R.id.erreurChargement)

        // Initialisation de Firestore
        firestore = FirebaseFirestore.getInstance()

        // Gestionnaire d'événements pour le bouton de connexion
        buttonLogin.setOnClickListener {
            val username = editTextPersonName.text.toString().trim()
            val password = editTextPersonName2.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password)
            } else {
                erreurChargement.visibility = View.VISIBLE
                erreurChargement.text = "Veuillez saisir un nom d'utilisateur et un mot de passe."
            }
        }

        // Gestionnaire d'événements pour le bouton de création de compte
        buttonCreateAccount.setOnClickListener {
            val username = editTextPseudo.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextPasswordConfirm.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                ajouterUtilisateur(username, password)
            } else {
                erreurChargement.visibility = View.VISIBLE
                erreurChargement.text = "Veuillez saisir un nom d'utilisateur, un mot de passe et la confirmation du mot de passe."
            }
        }

        return root
    }

    // Fonction pour ajouter un nouvel utilisateur à Firestore
    private fun ajouterUtilisateur(pseudo: String, motDePasse: String) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("user_gabinsbar")

        usersCollection.get().addOnSuccessListener { documents ->
            var nextId = documents.size() + 1

            // Création de l'objet utilisateur
            val user = hashMapOf(
                "id" to pseudo,
                "droits" to "invite",
                "motdepasse" to encryptPassword(motDePasse)
            )

            // Ajout de l'utilisateur à la collection Firestore
            usersCollection.document("user$nextId").set(user)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Utilisateur ajouté avec succès", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    erreurChargement.visibility = View.VISIBLE
                    erreurChargement.text = "Erreur lors de l'ajout de l'utilisateur : $e"
                }
        }
    }

    // Fonction pour chiffrer le mot de passe
    private fun encryptPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        val hashedPassword = BigInteger(1, hashedBytes).toString(16)

        return hashedPassword.padStart(64, '0')
    }

    // Fonction pour authentifier l'utilisateur
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
                erreurChargement.visibility = View.VISIBLE
                erreurChargement.text = "Nom d'utilisateur ou mot de passe incorrect."
            }
        }.addOnFailureListener { exception ->
            erreurChargement.visibility = View.VISIBLE
            erreurChargement.text = "Erreur lors de la récupération des données: $exception"
        }
    }

    // Nettoyage des champs de texte lors de la pause du fragment
    override fun onPause() {
        super.onPause()
        editTextPersonName.text.clear()
        editTextPersonName2.text.clear()
    }

    // Nettoyage du binding lors de la destruction de la vue
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
