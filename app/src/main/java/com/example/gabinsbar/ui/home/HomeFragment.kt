// Déclaration du package
package com.example.gabinsbar.ui.home

// Importation des classes nécessaires
import PanierManager.monPanier
import SessionManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
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
    lateinit var boutonSuggestions: Button
    private lateinit var adapterBiere: ArticleAdapter<Article>
    private lateinit var adapterSirop: ArticleAdapter<Article>
    private lateinit var adapterClassique: ArticleAdapter<Article>
    private lateinit var erreurChargement: TextView
    private lateinit var boutonSiropsSuggestion: Button
    private lateinit var textSiropsSuggestion: TextView
    private lateinit var boutonSoftsSuggestion: Button
    private lateinit var textSoftsSuggestion: TextView
    private lateinit var boutonBieresSuggestion: Button
    private lateinit var textBieresSuggestion: TextView
    private lateinit var boutonVinsSuggestion: Button
    private lateinit var textVinsSuggestion: TextView
    private lateinit var boutonClassiquesSuggestion: Button
    private lateinit var textClassiquesSuggestion: TextView
    private lateinit var boutonExtravagantsSuggestion: Button
    private lateinit var textExtravagantsSuggestion: TextView
    private lateinit var suggestionsMenu: HorizontalScrollView

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
        erreurChargement = root.findViewById(R.id.erreurChargement)
        boutonSiropsSuggestion = root.findViewById(R.id.boutonSiropsSuggestion)
        textSiropsSuggestion = root.findViewById(R.id.textSiropsSuggestion)
        boutonSoftsSuggestion = root.findViewById(R.id.boutonSoftsSuggestion)
        textSoftsSuggestion = root.findViewById(R.id.textSoftsSuggestion)
        boutonBieresSuggestion = root.findViewById(R.id.boutonBieresSuggestion)
        textBieresSuggestion = root.findViewById(R.id.textBieresSuggestion)
        boutonVinsSuggestion = root.findViewById(R.id.boutonVinsSuggestion)
        textVinsSuggestion = root.findViewById(R.id.textVinsSuggestion)
        boutonClassiquesSuggestion = root.findViewById(R.id.boutonClassiquesSuggestion)
        textClassiquesSuggestion = root.findViewById(R.id.textClassiquesSuggestion)
        boutonExtravagantsSuggestion = root.findViewById(R.id.boutonExtravagantsSuggestion)
        textExtravagantsSuggestion = root.findViewById(R.id.textExtravagantsSuggestion)
        suggestionsMenu = root.findViewById(R.id.suggestionsMenu)

        // Gestion du clic sur le bouton des suggestions
        boutonSuggestions.setOnClickListener{
            if(suggestionsMenu.visibility.equals(GONE)) {
                suggestionsMenu.visibility = VISIBLE
                if (SessionManager.nbrOuvertures == 0) {
                    SessionManager.nbrOuvertures += 1
                }
            } else {
                suggestionsMenu.visibility = View.GONE
            }
        }

        // Chargement des suggestions aléatoires
        chargerSiropAleatoire()
        chargerSoftAleatoire()
        chargerBiereAleatoire()
        chargerVinAleatoire()
        chargerClassiqueAleatoire()
        chargerExtravagantAleatoire()

        boutonSiropsSuggestion.setOnClickListener {
            if (textSiropsSuggestion.visibility.equals(VISIBLE)){
                textSiropsSuggestion.visibility = GONE
            } else {
                textSiropsSuggestion.visibility = VISIBLE
            }
            textSoftsSuggestion.visibility = GONE
            textBieresSuggestion.visibility = GONE
            textVinsSuggestion.visibility = GONE
            textClassiquesSuggestion.visibility = GONE
            textExtravagantsSuggestion.visibility = GONE
        }
        boutonSoftsSuggestion.setOnClickListener {
            textSiropsSuggestion.visibility = GONE
            if (textSoftsSuggestion.visibility.equals(VISIBLE)){
                textSoftsSuggestion.visibility = GONE
            } else {
                textSoftsSuggestion.visibility = VISIBLE
            }
            textBieresSuggestion.visibility = GONE
            textVinsSuggestion.visibility = GONE
            textClassiquesSuggestion.visibility = GONE
            textExtravagantsSuggestion.visibility = GONE
        }
        boutonBieresSuggestion.setOnClickListener {
            textSiropsSuggestion.visibility = GONE
            textSoftsSuggestion.visibility = GONE
            if (textBieresSuggestion.visibility.equals(VISIBLE)){
                textBieresSuggestion.visibility = GONE
            } else {
                textBieresSuggestion.visibility = VISIBLE
            }
            textVinsSuggestion.visibility = GONE
            textClassiquesSuggestion.visibility = GONE
            textExtravagantsSuggestion.visibility = GONE
        }
        boutonVinsSuggestion.setOnClickListener {
            textSiropsSuggestion.visibility = GONE
            textSoftsSuggestion.visibility = GONE
            textBieresSuggestion.visibility = GONE
            if (textVinsSuggestion.visibility.equals(VISIBLE)){
                textVinsSuggestion.visibility = GONE
            } else {
                textVinsSuggestion.visibility = VISIBLE
            }
            textClassiquesSuggestion.visibility = GONE
            textExtravagantsSuggestion.visibility = GONE
        }
        boutonClassiquesSuggestion.setOnClickListener {
            textSiropsSuggestion.visibility = GONE
            textSoftsSuggestion.visibility = GONE
            textBieresSuggestion.visibility = GONE
            textVinsSuggestion.visibility = GONE
            if (textClassiquesSuggestion.visibility.equals(VISIBLE)){
                textClassiquesSuggestion.visibility = GONE
            } else {
                textClassiquesSuggestion.visibility = VISIBLE
            }
            textExtravagantsSuggestion.visibility = GONE
        }
        boutonExtravagantsSuggestion.setOnClickListener {
            textSiropsSuggestion.visibility = GONE
            textSoftsSuggestion.visibility = GONE
            textBieresSuggestion.visibility = GONE
            textVinsSuggestion.visibility = GONE
            textClassiquesSuggestion.visibility = GONE
            if (textExtravagantsSuggestion.visibility.equals(VISIBLE)){
                textExtravagantsSuggestion.visibility = GONE
            } else {
                textExtravagantsSuggestion.visibility = VISIBLE
            }
        }

        return root
    }

    // Méthode appelée lors de la sélection d'un article
    private fun onArticleSelected(article: Article) {
        monPanier.add(article.nom)
        Toast.makeText(requireContext(), "${article.nom} ajouté au panier", Toast.LENGTH_SHORT).show()
    }

    // Méthode pour charger un sirop aléatoire depuis Firestore
    private fun chargerSiropAleatoire() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Sirops").collection("Sirops")

        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val nomSirop = mutableListOf<String>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    nomSirop.add(nom)
                }
                if (nomSirop.isNotEmpty()) {
                    val randomSirop = mutableListOf(nomSirop.random())
                    textSiropsSuggestion.text = randomSirop[0]
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

    // Méthode pour charger un soft aléatoire depuis Firestore
    private fun chargerSoftAleatoire() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Softs").collection("Softs")

        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val nomSoft = mutableListOf<String>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    nomSoft.add(nom)
                }
                if (nomSoft.isNotEmpty()) {
                    val randomSoft = mutableListOf(nomSoft.random())
                    textSoftsSuggestion.text = randomSoft[0]
                } else {
                    erreurChargement.visibility = VISIBLE
                    erreurChargement.text = "Aucun soft trouvé"
                }
            } else {
                erreurChargement.visibility = VISIBLE
                erreurChargement.text = "Aucun soft trouvé"
            }
        }.addOnFailureListener { e ->
            erreurChargement.visibility = VISIBLE
            erreurChargement.text = "Erreur lors du chargement des softs : $e"
        }
    }

    // Méthode pour charger une bière aléatoire depuis Firestore
    private fun chargerBiereAleatoire() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Bières").collection("Bouteilles")

        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val nomBiere = mutableListOf<String>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    val alcool = document.getString("Alcool") ?: ""
                    val chaine = "${nom} (${alcool}°)"
                    nomBiere.add(chaine)
                }
                if (nomBiere.isNotEmpty()) {
                    val randomBiere = mutableListOf(nomBiere.random())
                    textBieresSuggestion.text = randomBiere[0]
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

    // Méthode pour charger un vin aléatoire depuis Firestore
    private fun chargerVinAleatoire() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Vins").collection("Vins")

        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val nomVin = mutableListOf<String>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    val alcool = document.getString("Alcool") ?: ""
                    val chaine = "${nom} (${alcool}°)"
                    nomVin.add(chaine)
                }
                if (nomVin.isNotEmpty()) {
                    val randomVin = mutableListOf(nomVin.random())
                    textVinsSuggestion.text = randomVin[0]
                } else {
                    erreurChargement.visibility = VISIBLE
                    erreurChargement.text = "Aucun vin trouvée"
                }
            } else {
                erreurChargement.visibility = VISIBLE
                erreurChargement.text = "Aucun vin trouvée"
            }
        }.addOnFailureListener { e ->
            erreurChargement.visibility = VISIBLE
            erreurChargement.text = "Erreur lors du chargement des vins : $e"
        }
    }

    // Méthode pour charger un classique aléatoire depuis Firestore
    private fun chargerClassiqueAleatoire() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Classiques").collection("Classiques")

        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val nomClassique = mutableListOf<String>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    val alcool = document.getString("Alcool") ?: ""
                    val chaine = "${nom} (${alcool}°)"
                    nomClassique.add(chaine)
                }
                if (nomClassique.isNotEmpty()) {
                    val randomClassique = mutableListOf(nomClassique.random())
                    textClassiquesSuggestion.text = randomClassique[0]
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
            erreurChargement.text = "Erreur lors du chargement des classiques : $e"
        }
    }

    // Méthode pour charger un extravagant aléatoire depuis Firestore
    private fun chargerExtravagantAleatoire() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Extravagants").collection("Extravagants")

        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val nomExtravagant = mutableListOf<String>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    val alcool = document.getString("Alcool") ?: ""
                    val chaine = "${nom} (${alcool}°)"
                    nomExtravagant.add(chaine)
                }
                if (nomExtravagant.isNotEmpty()) {
                    val randomExtravagant = mutableListOf(nomExtravagant.random())
                    textExtravagantsSuggestion.text = randomExtravagant[0]
                } else {
                    erreurChargement.visibility = VISIBLE
                    erreurChargement.text = "Aucun extravagant trouvé"
                }
            } else {
                erreurChargement.visibility = VISIBLE
                erreurChargement.text = "Aucun extravagant trouvé"
            }
        }.addOnFailureListener { e ->
            erreurChargement.visibility = VISIBLE
            erreurChargement.text = "Erreur lors du chargement des extravagants : $e"
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
