// Déclaration du package
package com.example.gabinsbar.ui.home

// Importation des classes nécessaires
import PanierManager.monPanier
import SessionManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gabinsbar.R
import com.example.gabinsbar.databinding.FragmentHomeBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gabinsbar.model.Article
import com.example.gabinsbar.ui.ArticleAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

// Déclaration de la classe HomeFragment héritant de Fragment
class HomeFragment : Fragment() {

    // Binding pour accéder aux vues du layout
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Déclaration des variables pour les boutons et autres vues
    lateinit var buttonHome2Bieres: ImageButton
    lateinit var buttonHome2Vins: ImageButton
    lateinit var buttonHome2Classiques: ImageButton
    lateinit var buttonHome2Extravagants: ImageButton
    lateinit var buttonHome2Sirops: ImageButton
    lateinit var buttonHome2Softs: ImageButton
    lateinit var textSuggestions: CardView
    lateinit var boutonSuggestions: Button
    lateinit var suggestionBiere: RecyclerView
    lateinit var suggestionSirop: RecyclerView
    lateinit var suggestionClassique: RecyclerView
    private lateinit var adapterBiere: ArticleAdapter<Article>
    private lateinit var adapterSirop: ArticleAdapter<Article>
    private lateinit var adapterClassique: ArticleAdapter<Article>
    private lateinit var erreurChargement: TextView

    // Méthode appelée lors de la création de la vue du fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialisation du binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Modification de la couleur de la barre de statut
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorSecondary)

        // Initialisation de Firebase
        FirebaseApp.initializeApp(requireActivity())

        // Liaison des boutons et des vues à leurs identifiants
        buttonHome2Bieres = root.findViewById(R.id.buttonHome2Bieres)
        buttonHome2Vins = root.findViewById(R.id.buttonHome2Vins)
        buttonHome2Classiques = root.findViewById(R.id.buttonHome2Classiques)
        buttonHome2Extravagants = root.findViewById(R.id.buttonHome2Extravagants)
        buttonHome2Sirops = root.findViewById(R.id.buttonHome2Sirops)
        buttonHome2Softs = root.findViewById(R.id.buttonHome2Softs)
        boutonSuggestions = root.findViewById(R.id.boutonSuggestions)
        textSuggestions = root.findViewById(R.id.textSuggestions)
        suggestionBiere = root.findViewById(R.id.suggestionBiere)
        suggestionSirop = root.findViewById(R.id.suggestionSirop)
        suggestionClassique = root.findViewById(R.id.suggestionClassique)
        erreurChargement = root.findViewById(R.id.erreurChargement)

        // Configuration des RecyclerViews et des adaptateurs
        suggestionBiere.layoutManager = LinearLayoutManager(requireContext())
        adapterBiere = ArticleAdapter(emptyList()) { article -> onArticleSelected(article) }
        suggestionBiere.adapter = adapterBiere
        suggestionBiere.background = getResources().getDrawable(R.drawable.input)

        suggestionSirop.layoutManager = LinearLayoutManager(requireContext())
        adapterSirop = ArticleAdapter(emptyList()) { article -> onArticleSelected(article) }
        suggestionSirop.adapter = adapterSirop
        suggestionSirop.background = getResources().getDrawable(R.drawable.input)

        suggestionClassique.layoutManager = LinearLayoutManager(requireContext())
        adapterClassique = ArticleAdapter(emptyList()) { article -> onArticleSelected(article) }
        suggestionClassique.adapter = adapterClassique
        suggestionClassique.background = getResources().getDrawable(R.drawable.input)

        // Gestion du clic sur le bouton des suggestions
        boutonSuggestions.setOnClickListener{
            if(textSuggestions.visibility.equals(View.GONE)) {
                textSuggestions.visibility = View.VISIBLE
                if (SessionManager.nbrOuvertures == 0) {
                    SessionManager.nbrOuvertures += 1
                }
            } else {
                textSuggestions.visibility = View.GONE
            }
        }

        // Chargement des suggestions aléatoires
        chargerBiereAleatoire()
        chargerSiropAleatoire()
        chargerClassiqueAleatoire()

        return root
    }

    // Méthode appelée lors de la sélection d'un article
    private fun onArticleSelected(article: Article) {
        monPanier.add(article.nom)
        Toast.makeText(requireContext(), "${article.nom} ajouté au panier", Toast.LENGTH_SHORT).show()
    }

    // Méthode pour charger une bière aléatoire depuis Firestore
    private fun chargerBiereAleatoire() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Bières").collection("Bouteilles")

        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val bieres = mutableListOf<Article>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    bieres.add(Article(document.id, nom))
                }
                if (bieres.isNotEmpty()) {
                    val randomBiere = mutableListOf(bieres.random())
                    adapterBiere.updateData(randomBiere)
                } else {
                    erreurChargement.visibility = VISIBLE
                    erreurChargement.text = "Aucune bière trouvée"
                }
            } else {
                erreurChargement.visibility = VISIBLE
                erreurChargement.text = "Aucune bière trouvée"
            }
        }.addOnFailureListener { e ->
            erreurChargement.visibility = VISIBLE
            erreurChargement.text = "Erreur lors du chargement des bières : $e"
        }
    }

    // Méthode pour charger un sirop aléatoire depuis Firestore
    private fun chargerSiropAleatoire() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Sirops").collection("Sirops")

        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val sirops = mutableListOf<Article>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    sirops.add(Article(document.id, nom))
                }
                if (sirops.isNotEmpty()) {
                    val randomSirop = mutableListOf<Article>(sirops.random())
                    adapterSirop.updateData(randomSirop)
                } else {
                    erreurChargement.visibility = VISIBLE
                    erreurChargement.text = "Aucun sirop trouvé"
                }
            } else {
                erreurChargement.visibility = VISIBLE
                erreurChargement.text = "Aucun sirop trouvé"
            }
        }.addOnFailureListener { e ->
            erreurChargement.visibility = VISIBLE
            erreurChargement.text = "Erreur lors du chargement des sirops : $e"
        }
    }

    // Méthode pour charger une boisson classique aléatoire depuis Firestore
    private fun chargerClassiqueAleatoire() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Classiques").collection("Classiques")

        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val classiques = mutableListOf<Article>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    classiques.add(Article(document.id, nom))
                }
                if (classiques.isNotEmpty()) {
                    val randomClassique = mutableListOf<Article>(classiques.random())
                    adapterClassique.updateData(randomClassique)
                } else {
                    erreurChargement.visibility = VISIBLE
                    erreurChargement.text = "Aucun classique trouvé"
                }
            } else {
                erreurChargement.visibility = VISIBLE
                erreurChargement.text = "Aucun classique trouvé"
            }
        }.addOnFailureListener { e ->
            erreurChargement.visibility = VISIBLE
            erreurChargement.text = "Erreur lors du chargement des classique : $e"
        }
    }

    // Méthode appelée après la création de la vue
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gestion des clics sur les boutons pour naviguer vers les différentes catégories de boissons
        buttonHome2Bieres.setOnClickListener {
            findNavController().popBackStack(R.id.nav_accueil, false)
            findNavController().navigate(R.id.nav_bieres)
        }

        buttonHome2Vins.setOnClickListener {
            findNavController().popBackStack(R.id.nav_accueil, false)
            findNavController().navigate(R.id.nav_vins)
        }

        buttonHome2Classiques.setOnClickListener {
            findNavController().popBackStack(R.id.nav_accueil, false)
            findNavController().navigate(R.id.nav_classiques)
        }

        buttonHome2Extravagants.setOnClickListener {
            findNavController().popBackStack(R.id.nav_accueil, false)
            findNavController().navigate(R.id.nav_extravagants)
        }

        buttonHome2Sirops.setOnClickListener {
            findNavController().popBackStack(R.id.nav_accueil, false)
            findNavController().navigate(R.id.nav_sirops)
        }

        buttonHome2Softs.setOnClickListener {
            findNavController().popBackStack(R.id.nav_accueil, false)
            findNavController().navigate(R.id.nav_softs)
        }
    }

    // Méthode appelée lors de la destruction de la vue pour nettoyer le binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
