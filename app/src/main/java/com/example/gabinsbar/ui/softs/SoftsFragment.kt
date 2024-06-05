package com.example.gabinsbar.ui.softs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gabinsbar.model.Article
import com.example.gabinsbar.ui.ArticleAdapter
import com.example.gabinsbar.R
import com.example.gabinsbar.databinding.FragmentSoftsBinding
import com.google.firebase.firestore.FirebaseFirestore

class SoftsFragment : Fragment() {

    // Variable pour stocker le binding du fragment
    private var _binding: FragmentSoftsBinding? = null

    // Propriété pour accéder au binding de manière sécurisée
    private val binding get() = _binding!!

    // Déclaration des variables de vue
    lateinit var text_softs: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArticleAdapter<Article>
    private lateinit var erreurChargement: TextView

    // Méthode appelée pour créer et initialiser la vue du fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialisation du binding
        _binding = FragmentSoftsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Modification de la couleur de la barre de statut
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorSecondary)

        // Initialisation des vues
        erreurChargement = root.findViewById(R.id.erreurChargement)

        try {
            // Initialisation du RecyclerView et de son adaptateur
            recyclerView = root.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter = ArticleAdapter(emptyList()) { article -> onArticleSelected(article) }
            recyclerView.adapter = adapter

            // Chargement des articles
            chargerSofts()
        } catch (e: Exception) {
            // Affichage d'un message d'erreur en cas d'échec de chargement des articles
            erreurChargement.visibility = View.VISIBLE
            erreurChargement.text = "Erreur lors de la récupération des données Firestore: $e"
        }

        return root
    }

    // Méthode pour charger les articles depuis Firestore
    private fun chargerSofts() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Softs").collection("Softs")
        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val boissons = mutableListOf<Article>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    boissons.add(Article(document.id, nom))
                }
                // Mise à jour des données de l'adaptateur
                adapter.updateData(boissons)
            } else {
                // Affichage d'un message si aucun article n'est trouvé
                erreurChargement.visibility = View.VISIBLE
                erreurChargement.text = "Aucun soft trouvé"
            }
        }.addOnFailureListener { e ->
            // Affichage d'un message d'erreur en cas d'échec de récupération des données
            erreurChargement.visibility = View.VISIBLE
            erreurChargement.text = "Erreur lors de la récupération des données Firestore: $e"
        }
    }

    // Méthode appelée lorsqu'un article est sélectionné
    private fun onArticleSelected(article: Article) {
        PanierManager.monPanier.add(article.nom)
        Toast.makeText(requireContext(), "${article.nom} ajouté au panier", Toast.LENGTH_SHORT).show()
    }

    // Méthode appelée lorsque la vue du fragment est détruite
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Libération du binding pour éviter les fuites de mémoire
    }
}
