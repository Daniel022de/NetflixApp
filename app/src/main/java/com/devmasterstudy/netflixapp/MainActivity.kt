package com.devmasterstudy.netflixapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devmasterstudy.netflixapp.model.Category
import com.devmasterstudy.netflixapp.util.CategoryTask


class MainActivity : AppCompatActivity(), CategoryTask.CallBack {

    private lateinit var adapter: CategoryAdapter
    private val categories = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        adapter = CategoryAdapter(categories) { id ->
            startActivity(Intent(this, MovieActivity::class.java).putExtra("id",id))
        }

        val rv: RecyclerView = findViewById(R.id.rv_main)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)

        CategoryTask(this).execute("https://api.tiagoaguiar.co/netflixapp/home?apiKey=a0d78df8-56c6-4d3c-815a-05d89b024ad7")


    }

    override fun onPreExecute() {
        this.findViewById<ProgressBar>(R.id.progress_main).visibility = View.VISIBLE
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResult(categories: List<Category>) {
        this.findViewById<ProgressBar>(R.id.progress_main).visibility = View.GONE
        this.categories.clear()
        this.categories.addAll(categories)
        adapter.notifyDataSetChanged()
    }

    override fun onFailure(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        this.findViewById<ProgressBar>(R.id.progress_main).visibility = View.GONE
    }
}