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

    private var _binding: FragmentSiropsBinding? = null

    private val binding get() = _binding!!

    lateinit var text_sirops: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArticleAdapter<Article>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSiropsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorSecondary)

        try {
            recyclerView = root.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter = ArticleAdapter(emptyList()) { article -> onArticleSelected(article) }
            recyclerView.adapter = adapter

            chargerSirops()
        }
        catch (e: Exception) {
            text_sirops.text = "Erreur lors de la récupération des données Firestore" + e.message
        }

        return root
    }

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
                adapter.updateData(boissons)
            } else {
                Toast.makeText(requireContext(), "Aucune boisson trouvée pour le type Sirops", Toast.LENGTH_SHORT).show()
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