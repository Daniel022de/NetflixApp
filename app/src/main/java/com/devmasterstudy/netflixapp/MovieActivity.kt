package com.devmasterstudy.netflixapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devmasterstudy.netflixapp.model.Movie
import com.devmasterstudy.netflixapp.model.MovieDetail
import com.devmasterstudy.netflixapp.util.DownloadImageTask
import com.devmasterstudy.netflixapp.util.MovieTask

class MovieActivity : AppCompatActivity(), MovieTask.CallBack {

    private val movies = mutableListOf<Movie>()
    private lateinit var adapter: MovieAdapter
    private lateinit var rvMovieSimilar: RecyclerView
    lateinit var coverImg: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val toolbar: Toolbar = findViewById(R.id.toolbar_movie)

        val id = intent?.getIntExtra("id",0) ?: throw IllegalStateException("ID não foi encontrado!")

        coverImg = findViewById(R.id.img_movie)

        rvMovieSimilar = findViewById(R.id.rv_movie_similar)
        adapter = MovieAdapter(movies,R.layout.movie_item_similar){ id ->
            startActivity(Intent(this, MovieActivity::class.java).putExtra("id",id))
        }

        rvMovieSimilar.layoutManager = GridLayoutManager(this,3)
        rvMovieSimilar.adapter = adapter

        MovieTask(this).execute("https://api.tiagoaguiar.co/netflixapp/movie/$id?apiKey=a0d78df8-56c6-4d3c-815a-05d89b024ad7")


        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPreExecute() {
        findViewById<ProgressBar>(R.id.progress_movie).visibility = View.VISIBLE
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResult(movieDetail: MovieDetail) {
        findViewById<ProgressBar>(R.id.progress_movie).visibility = View.GONE

        findViewById<TextView>(R.id.tv_movie_title).text = movieDetail.movie.title
        findViewById<TextView>(R.id.tv_movie_desc).text = movieDetail.movie.desc
        findViewById<TextView>(R.id.tv_movie_cast).text = movieDetail.movie.cast

        movies.clear()
        movies.addAll(movieDetail.similars)
        adapter.notifyDataSetChanged()

        DownloadImageTask(object : DownloadImageTask.CallBack {
            override fun onResult(bitmap: Bitmap) {
                val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this@MovieActivity,R.drawable.shadows) as LayerDrawable
                val movieCover = BitmapDrawable(resources, bitmap)

                layerDrawable.setDrawableByLayerId(R.id.cover_drawable,movieCover)
                coverImg.setImageDrawable(layerDrawable)
            }
        }).execute(movieDetail.movie.coverUrl)


    }

    override fun onFailure(message: String) {
        findViewById<ProgressBar>(R.id.progress_movie).visibility = View.GONE
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }
}