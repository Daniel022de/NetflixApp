package com.devmasterstudy.netflixapp

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.devmasterstudy.netflixapp.model.Movie
import com.devmasterstudy.netflixapp.util.DownloadImageTask


class MovieAdapter(private val movies: List<Movie>,
    @LayoutRes private val layoutId: Int,
    private val onItemClickLister: ((Int) -> Unit)? = null
    ) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId,parent,false)
        return MovieViewHolder(itemView) }

    override fun getItemCount() = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val itemCurrent = movies[position]
        holder.bind(itemCurrent)
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(itemCurrent: Movie){
            val imageViewCover: ImageView = itemView.findViewById(R.id.iv_cover)

            imageViewCover.setOnClickListener {
                onItemClickLister?.invoke(itemCurrent.id)
            }

            DownloadImageTask(object : DownloadImageTask.CallBack {
                override fun onResult(bitmap: Bitmap) {
                    imageViewCover.setImageBitmap(bitmap)
                }
            }).execute(itemCurrent.coverUrl)
        }
    }

}