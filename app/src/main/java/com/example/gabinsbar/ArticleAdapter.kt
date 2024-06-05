package com.example.gabinsbar.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gabinsbar.R
import com.example.gabinsbar.model.Article

// Adapter personnalisé pour lier les données d'Article à une RecyclerView
class ArticleAdapter<T>(private var articles: List<T>, private val onItemClick: (T) -> Unit) :
    RecyclerView.Adapter<ArticleAdapter<T>.ArticleViewHolder>() {

    // Vue interne pour représenter chaque élément dans la RecyclerView
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Élément visuel pour afficher le nom de l'article
        val nomTextView: TextView = itemView.findViewById(R.id.nomTextView)

        init {
            // Gestionnaire de clics sur les éléments de la RecyclerView
            itemView.setOnClickListener {
                onItemClick(articles[adapterPosition])
            }
        }
    }

    // Crée un nouvel ArticleViewHolder en fonction de son parent et de son type de vue
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        // Inflate la vue de l'article à partir de son XML
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    // Lie les données de l'article à la vue lorsqu'elle est affichée
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        // Obtient l'article à la position donnée et le caste en Article
        val article = articles[position] as Article
        // Met à jour la vue avec le nom de l'article
        holder.nomTextView.text = article.nom
    }

    // Retourne le nombre total d'éléments dans la liste d'articles
    override fun getItemCount(): Int = articles.size

    // Met à jour les données de l'adaptateur avec une nouvelle liste d'articles et notifie la RecyclerView du changement
    fun updateData(newData: List<T>) {
        articles = newData
        notifyDataSetChanged()
    }
}
