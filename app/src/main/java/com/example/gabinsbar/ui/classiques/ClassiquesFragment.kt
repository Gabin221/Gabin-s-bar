package com.example.gabinsbar.ui.classiques

// Importation des classes nécessaires
import PanierManager
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
import com.example.gabinsbar.databinding.FragmentClassiquesBinding
import com.google.firebase.firestore.FirebaseFirestore

// Déclaration du fragment ClassiquesFragment
class ClassiquesFragment : Fragment() {

    // Variable pour le binding de la vue, initialisée à null
    private var _binding: FragmentClassiquesBinding? = null

    // Propriété pour accéder à _binding en toute sécurité
    private val binding get() = _binding!!

    // Déclaration des variables pour RecyclerView, l'adaptateur et TextView pour les erreurs
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArticleAdapter<Article>
    private lateinit var erreurChargement: TextView

    // Méthode appelée lors de la création de la vue du fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Initialisation du binding et récupération de la racine de la vue
        _binding = FragmentClassiquesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Changement de la couleur de la barre de statut
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorSecondary)

        // Initialisation du TextView pour afficher les erreurs de chargement
        erreurChargement = root.findViewById(R.id.erreurChargement)

        try {
            // Configuration du RecyclerView avec un layout manager et un adaptateur vide
            recyclerView = root.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter = ArticleAdapter(emptyList()) { article -> onArticleSelected(article) }
            recyclerView.adapter = adapter

            // Chargement des articles classiques depuis Firestore
            chargerClassiques()
        } catch (e: Exception) {
            // Gestion des exceptions en affichant un message d'erreur
            erreurChargement.visibility = View.VISIBLE
            erreurChargement.text = "Erreur lors de la récupération des données Firestore: " + e.message
        }

        return root
    }

    // Méthode pour charger les articles classiques depuis Firestore
    private fun chargerClassiques() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Classiques").collection("Classiques")

        // Récupération des documents de la collection
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
                erreurChargement.text = "Aucun classique trouvé"
            }
        }.addOnFailureListener { e ->
            // Gestion des erreurs de récupération de données
            erreurChargement.visibility = View.VISIBLE
            erreurChargement.text = "Erreur lors de la récupération des données Firestore: $e"
        }
    }

    // Méthode appelée lorsque l'utilisateur sélectionne un article
    private fun onArticleSelected(article: Article) {
        PanierManager.monPanier.add(article.nom)
        Toast.makeText(requireContext(), "${article.nom} ajouté au panier", Toast.LENGTH_SHORT).show()
    }

    // Méthode appelée lors de la destruction de la vue du fragment
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
