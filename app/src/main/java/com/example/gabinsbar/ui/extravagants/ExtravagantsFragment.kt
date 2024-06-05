package com.example.gabinsbar.ui.extravagants

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
import com.example.gabinsbar.databinding.FragmentExtravagantsBinding
import com.google.firebase.firestore.FirebaseFirestore

// Déclaration de la classe ExtravagantsFragment héritant de Fragment
class ExtravagantsFragment : Fragment() {

    // Variable pour la liaison de vue. Le '?' indique qu'elle peut être nulle.
    private var _binding: FragmentExtravagantsBinding? = null

    // Getter pour accéder à _binding de manière sécurisée. !! force la valeur non nulle.
    private val binding get() = _binding!!

    // Déclaration des variables RecyclerView, ArticleAdapter et TextView pour la gestion de l'affichage.
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArticleAdapter<Article>
    private lateinit var erreurChargement: TextView

    // Méthode appelée pour créer et initialiser la vue du fragment.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Initialisation de la liaison de vue avec FragmentExtravagantsBinding
        _binding = FragmentExtravagantsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Changement de la couleur de la barre de statut
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorSecondary)

        // Initialisation du TextView pour afficher les erreurs
        erreurChargement = root.findViewById(R.id.erreurChargement)

        try {
            // Initialisation du RecyclerView et de son adaptateur
            recyclerView = root.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter = ArticleAdapter(emptyList()) { article -> onArticleSelected(article) }
            recyclerView.adapter = adapter

            // Chargement des articles extravagants depuis Firestore
            chargerExtravagants()
        }
        catch (e: Exception) {
            // Gestion des erreurs en cas de problème avec le RecyclerView ou l'adaptateur
            erreurChargement.visibility = View.VISIBLE
            erreurChargement.text = "Erreur lors de la récupération des données Firestore: $e"
        }

        // Retourne la vue racine pour affichage
        return root
    }

    // Méthode pour charger les articles extravagants depuis Firestore
    private fun chargerExtravagants() {
        // Accès à l'instance Firestore
        val db = FirebaseFirestore.getInstance()
        // Référence à la collection des extravagants
        val collection = db.collection("boissons").document("Extravagants").collection("Extravagants")

        // Récupération des documents de la collection
        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                // Si des documents sont trouvés, les ajouter à la liste des boissons
                val boissons = mutableListOf<Article>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    val alcool = document.getString("Alcool") ?: ""
                    val chaine = "${nom} (${alcool}°)"
                    boissons.add(Article(document.id, chaine))
                }
                // Mise à jour de l'adaptateur avec les nouvelles données
                adapter.updateData(boissons)
            } else {
                // Si aucun document trouvé, afficher un message d'erreur
                erreurChargement.visibility = View.VISIBLE
                erreurChargement.text = "Aucun extravagant trouvé"
            }
        }.addOnFailureListener { e ->
            // Gestion des erreurs lors de la récupération des données
            erreurChargement.visibility = View.VISIBLE
            erreurChargement.text = "Erreur lors de la récupération des données Firestore: $e"
        }
    }

    // Méthode appelée lorsqu'un article est sélectionné
    private fun onArticleSelected(article: Article) {
        // Ajout de l'article sélectionné au panier
        PanierManager.monPanier.add(article.nom)
        // Affichage d'un message de confirmation
        Toast.makeText(requireContext(), "${article.nom} ajouté au panier", Toast.LENGTH_SHORT).show()
    }

    // Méthode appelée lors de la destruction de la vue
    override fun onDestroyView() {
        super.onDestroyView()
        // Libération de la liaison de vue pour éviter les fuites de mémoire
        _binding = null
    }
}
