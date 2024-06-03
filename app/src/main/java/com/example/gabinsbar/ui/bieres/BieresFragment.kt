package com.example.gabinsbar.ui.bieres

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


class BieresFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentBieresBinding? = null
    private val binding get() = _binding!!

    private lateinit var text_bieres: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var pressions: RecyclerView
    private lateinit var adapter: ArticleAdapter<Article>
    private lateinit var adapterPressions: ArticleAdapter<Article>

    val typeBiere = arrayListOf("Bouteilles", "Pressions")
    var typeBierePosition = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBieresBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorSecondary)
        FirebaseApp.initializeApp(requireActivity())

        try {
            recyclerView = root.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter = ArticleAdapter(emptyList()) { article -> onArticleSelected(article) }
            recyclerView.adapter = adapter
            chargerBieres()

            pressions = root.findViewById(R.id.pressions)
            pressions.layoutManager = LinearLayoutManager(requireContext())
            adapterPressions = ArticleAdapter(emptyList()) { article -> onArticleSelected(article) }
            pressions.adapter = adapterPressions
            chargerPressions()

        }
        catch (e: Exception) {
            text_bieres.text = "Erreur lors de la récupération des données Firestore" + e.message
        }

        return root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        typeBierePosition = typeBiere[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(requireContext(), "Veuillez sélectionner un type de bière", Toast.LENGTH_SHORT).show()
    }

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
                Toast.makeText(requireContext(), "Aucune boisson trouvée pour le type Bières Bouteilles", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Erreur lors du chargement des boissons : $e", Toast.LENGTH_SHORT).show()
        }
    }

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
                Toast.makeText(requireContext(), "Aucune boisson trouvée pour le type Extravagants", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Erreur lors du chargement des boissons : $e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onArticleSelected(article: Article) {
        PanierManager.monPanier.add(article.nom)
        Toast.makeText(requireContext(), "${article.nom} ajouté au panier", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
