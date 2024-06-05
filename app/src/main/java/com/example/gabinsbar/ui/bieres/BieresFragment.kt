// Déclaration du package de l'application
package com.example.gabinsbar.ui.bieres

// Importation des bibliothèques nécessaires
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gabinsbar.R
import com.example.gabinsbar.databinding.FragmentBieresBinding
import com.example.gabinsbar.model.Article
import com.example.gabinsbar.ui.ArticleAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

// Déclaration de la classe BieresFragment qui hérite de Fragment et implémente AdapterView.OnItemSelectedListener
class BieresFragment : Fragment(), AdapterView.OnItemSelectedListener {

    // Déclaration de la variable pour le binding
    private var _binding: FragmentBieresBinding? = null
    private val binding get() = _binding!!

    // Déclaration des variables pour les RecyclerView et les adaptateurs
    private lateinit var recyclerView: RecyclerView
    private lateinit var pressions: RecyclerView
    private lateinit var adapter: ArticleAdapter<Article>
    private lateinit var adapterPressions: ArticleAdapter<Article>
    private lateinit var erreurChargement: TextView

    // Liste des types de bière et la position sélectionnée
    val typeBiere = arrayListOf("Bouteilles", "Pressions")
    var typeBierePosition = ""

    // Méthode appelée pour créer la vue du fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Liaison du layout avec le binding
        _binding = FragmentBieresBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Changement de la couleur de la barre de statut
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorSecondary)
        FirebaseApp.initializeApp(requireActivity())

        // Initialisation de l'élément TextView pour afficher les erreurs
        erreurChargement = root.findViewById(R.id.erreurChargement)

        try {
            // Initialisation et configuration de la RecyclerView pour les bouteilles
            recyclerView = root.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter = ArticleAdapter(emptyList()) { article -> onArticleSelected(article) }
            recyclerView.adapter = adapter
            // Chargement des données pour les bouteilles
            chargerBieres()

            // Initialisation et configuration de la RecyclerView pour les pressions
            pressions = root.findViewById(R.id.pressions)
            pressions.layoutManager = LinearLayoutManager(requireContext())
            adapterPressions = ArticleAdapter(emptyList()) { article -> onArticleSelected(article) }
            pressions.adapter = adapterPressions
            // Chargement des données pour les pressions
            chargerPressions()
        } catch (e: Exception) {
            // Gestion des exceptions et affichage des erreurs
            erreurChargement.visibility = View.VISIBLE
            erreurChargement.text = "Erreur lors de la récupération des données Firestore" + e.message
        }

        return root
    }

    // Méthode appelée lorsqu'un item est sélectionné dans le spinner
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        typeBierePosition = typeBiere[position]
    }

    // Méthode appelée lorsqu'aucun item n'est sélectionné dans le spinner
    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(requireContext(), "Veuillez sélectionner un type de bière", Toast.LENGTH_SHORT).show()
    }

    // Méthode pour charger les données des bières en bouteilles depuis Firestore
    private fun chargerBieres() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Bières").collection("Bouteilles")
        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val boissons = mutableListOf<Article>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    boissons.add(Article(document.id, nom))
                }
                adapter.updateData(boissons)
            } else {
                erreurChargement.visibility = View.VISIBLE
                erreurChargement.text = "Aucune bière bouteille trouvé"
            }
        }.addOnFailureListener { e ->
            erreurChargement.visibility = View.VISIBLE
            erreurChargement.text = "Erreur lors de la récupération des boissons: $e"
        }
    }

    // Méthode pour charger les données des bières en pression depuis Firestore
    private fun chargerPressions() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Bières").collection("Pressions")
        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val boissons = mutableListOf<Article>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    boissons.add(Article(document.id, nom))
                }
                adapterPressions.updateData(boissons)
            } else {
                erreurChargement.visibility = View.VISIBLE
                erreurChargement.text = "Aucune bière pression trouvée"
            }
        }.addOnFailureListener { e ->
            erreurChargement.visibility = View.VISIBLE
            erreurChargement.text = "Erreur lors du chargement des boissons: $e"
        }
    }

    // Méthode appelée lorsqu'un article est sélectionné dans la liste
    private fun onArticleSelected(article: Article) {
        PanierManager.monPanier.add(article.nom)
        Toast.makeText(requireContext(), "${article.nom} ajouté au panier", Toast.LENGTH_SHORT).show()
    }

    // Méthode appelée lors de la destruction de la vue
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
