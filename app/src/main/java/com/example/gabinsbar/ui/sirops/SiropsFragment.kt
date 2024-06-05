package com.example.gabinsbar.ui.sirops

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
import com.example.gabinsbar.R
import com.example.gabinsbar.databinding.FragmentSiropsBinding
import com.example.gabinsbar.ui.ArticleAdapter
import com.google.firebase.firestore.FirebaseFirestore

class SiropsFragment : Fragment() {

    // Variable pour stocker la liaison avec la vue
    private var _binding: FragmentSiropsBinding? = null

    // Accesseur pour obtenir la liaison en garantissant qu'elle n'est pas nulle
    private val binding get() = _binding!!

    // Déclaration du TextView pour afficher les sirops
    lateinit var text_sirops: TextView

    // Déclaration du RecyclerView et de son adaptateur
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArticleAdapter<Article>
    private lateinit var erreurChargement: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Initialisation de la liaison avec la vue
        _binding = FragmentSiropsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Modification de la couleur de la barre de statut
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorSecondary)

        // Initialisation du TextView pour afficher les erreurs de chargement
        erreurChargement = root.findViewById(R.id.erreurChargement)

        try {
            // Initialisation du RecyclerView et de son adaptateur
            recyclerView = root.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter = ArticleAdapter(emptyList()) { article -> onArticleSelected(article) }
            recyclerView.adapter = adapter

            // Chargement des sirops depuis Firestore
            chargerSirops()
        } catch (e: Exception) {
            // Affichage d'un message d'erreur en cas d'échec
            erreurChargement.visibility = View.VISIBLE
            erreurChargement.text = "Erreur lors de la récupération des données Firestore: $e"
        }

        return root
    }

    // Fonction pour charger les sirops depuis Firestore
    private fun chargerSirops() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Sirops").collection("Sirops")
        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val boissons = mutableListOf<Article>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    boissons.add(Article(document.id, nom))
                }
                // Mise à jour des données de l'adaptateur avec les sirops récupérés
                adapter.updateData(boissons)
            } else {
                // Affichage d'un message si aucun sirop n'est trouvé
                erreurChargement.visibility = View.VISIBLE
                erreurChargement.text = "Aucun sirop trouvé"
            }
        }.addOnFailureListener { e ->
            // Affichage d'un message d'erreur en cas d'échec
            erreurChargement.visibility = View.VISIBLE
            erreurChargement.text = "Erreur lors de la récupération des données Firestore: $e"
        }
    }

    // Fonction appelée lorsqu'un article est sélectionné
    private fun onArticleSelected(article: Article) {
        PanierManager.monPanier.add(article.nom)
        Toast.makeText(requireContext(), "${article.nom} ajouté au panier", Toast.LENGTH_SHORT).show()
    }

    // Fonction appelée lors de la destruction de la vue
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Libération de la liaison avec la vue pour éviter les fuites de mémoire
    }
}
