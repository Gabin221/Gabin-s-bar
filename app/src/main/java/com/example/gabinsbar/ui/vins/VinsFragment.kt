package com.example.gabinsbar.ui.vins

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
import com.example.gabinsbar.R
import com.example.gabinsbar.model.Article
import com.example.gabinsbar.databinding.FragmentVinsBinding
import com.example.gabinsbar.ui.ArticleAdapter
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Fragment pour afficher la liste des vins disponibles.
 */
class VinsFragment : Fragment() {

    // Binding pour le fragment
    private var _binding: FragmentVinsBinding? = null
    private val binding get() = _binding!!

    // RecyclerView pour afficher la liste des vins
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArticleAdapter<Article>
    private lateinit var erreurChargement: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflater pour le layout du fragment
        _binding = FragmentVinsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Changer la couleur de la barre de statut
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorSecondary)

        // Initialiser les vues
        erreurChargement = root.findViewById(R.id.erreurChargement)

        try {
            // Initialiser le RecyclerView et son adaptateur
            recyclerView = root.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter = ArticleAdapter(emptyList()) { article -> onArticleSelected(article) }
            recyclerView.adapter = adapter

            // Charger la liste des vins depuis Firestore
            chargerVins()
        }
        catch (e: Exception) {
            // Afficher une erreur en cas d'échec de chargement
            erreurChargement.visibility = View.VISIBLE
            erreurChargement.text = "Erreur lors de la récupération des données Firestore: $e"
        }

        return root
    }

    /**
     * Charger la liste des vins depuis Firestore.
     */
    private fun chargerVins() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Vins").collection("Vins")
        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                // Si des vins sont trouvés, les ajouter à la liste
                val boissons = mutableListOf<Article>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    val alcool = document.getString("Alcool") ?: ""
                    val chaine = "${nom} (${alcool}°)"
                    boissons.add(Article(document.id, chaine))
                }
                adapter.updateData(boissons)
            } else {
                // Afficher un message si aucun vin n'est trouvé
                erreurChargement.visibility = View.VISIBLE
                erreurChargement.text = "Aucun vin trouvé"
            }
        }.addOnFailureListener { e ->
            // Afficher une erreur en cas d'échec de récupération des données Firestore
            erreurChargement.visibility = View.VISIBLE
            erreurChargement.text = "Erreur lors de la récupération des données Firestore: $e"
        }
    }

    /**
     * Appelé lorsqu'un article est sélectionné.
     * Ajoute l'article au panier et affiche un message.
     */
    private fun onArticleSelected(article: Article) {
        PanierManager.monPanier.add(article.nom)
        Toast.makeText(requireContext(), "${article.nom} ajouté au panier", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
