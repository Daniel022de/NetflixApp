package com.devmasterstudy.netflixapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devmasterstudy.netflixapp.model.Category


class CategoryAdapter(
    private val categories: List<Category>,
    private val onItemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun getItemCount() = categories.size


    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val itemCurrent = categories[position]
        holder.bind(itemCurrent)
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(itemCurrent: Category) {
            val txtTitle: TextView = itemView.findViewById(R.id.tv_title)
            val rvMovie: RecyclerView = itemView.findViewById(R.id.rv_movie)

            txtTitle.text = itemCurrent.name
            rvMovie.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            rvMovie.adapter = MovieAdapter(itemCurrent.movies, R.layout.movie_item, onItemClickListener)

        }
    }

}