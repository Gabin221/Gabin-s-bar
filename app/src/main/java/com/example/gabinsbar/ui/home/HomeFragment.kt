package com.example.gabinsbar.ui.home

import PanierManager.monPanier
import SessionManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorSecondary)
        FirebaseApp.initializeApp(requireActivity())

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

        chargerBiereAleatoire()
        chargerSiropAleatoire()
        chargerClassiqueAleatoire()

        return root
    }

    private fun onArticleSelected(article: Article) {
        monPanier.add(article.nom)
        Toast.makeText(requireContext(), "${article.nom} ajouté au panier", Toast.LENGTH_SHORT).show()
    }

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
                    val randomBiere = mutableListOf<Article>(bieres.random())
                    adapterBiere.updateData(randomBiere)
                } else {
                    Toast.makeText(requireContext(), "Aucune bière trouvée", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Aucune bière trouvée", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Erreur lors du chargement des bières : $e", Toast.LENGTH_SHORT).show()
        }
    }

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
                    Toast.makeText(requireContext(), "Aucun sirop trouvé", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Aucun sirop trouvé", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Erreur lors du chargement des sirops : $e", Toast.LENGTH_SHORT).show()
        }
    }

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
                    Toast.makeText(requireContext(), "Aucun classique trouvé", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Aucun classique trouvé", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Erreur lors du chargement des classique : $e", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}