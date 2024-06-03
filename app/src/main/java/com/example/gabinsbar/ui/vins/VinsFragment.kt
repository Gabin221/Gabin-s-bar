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

class VinsFragment : Fragment() {

    private var _binding: FragmentVinsBinding? = null

    private val binding get() = _binding!!

    lateinit var text_vins: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArticleAdapter<Article>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentVinsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorSecondary)

        try {
            recyclerView = root.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter = ArticleAdapter(emptyList()) { article -> onArticleSelected(article) }
            recyclerView.adapter = adapter

            chargerVins()
        }
        catch (e: Exception) {
            text_vins.text = "Erreur lors de la récupération des données Firestore" + e.message
        }

        return root
    }

    private fun chargerVins() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("boissons").document("Vins").collection("Vins")
        collection.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val boissons = mutableListOf<Article>()
                for (document in documents) {
                    val nom = document.getString("Nom") ?: ""
                    boissons.add(Article(document.id, nom))
                }
                adapter.updateData(boissons)
            } else {
                Toast.makeText(requireContext(), "Aucune boisson trouvée pour le type Vins", Toast.LENGTH_SHORT).show()
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