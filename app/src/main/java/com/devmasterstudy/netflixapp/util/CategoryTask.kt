package com.devmasterstudy.netflixapp.util


import android.os.Handler
import android.os.Looper
import android.util.Log
import com.devmasterstudy.netflixapp.model.Category
import com.devmasterstudy.netflixapp.model.Movie
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask (private val callback: CallBack) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    private var urlConnection: HttpsURLConnection? = null
    private var stream : InputStream? = null

    interface CallBack {
        fun onPreExecute()
        fun onResult(categories: List<Category>)
        fun onFailure(message: String)
    }

    fun execute(url: String){
        handler.post{callback.onPreExecute()}

        executor.execute{
            try {
            val requestURL = URL(url)
            urlConnection = requestURL.openConnection() as HttpsURLConnection

            urlConnection?.apply {
                readTimeout = 2000
                connectTimeout = 2000 }

            val statusCode = urlConnection?.responseCode
                if (statusCode != null) {
                    if (statusCode > 400) {
                        throw IOException("Erro na comunicaçao com o servidor!")
                    }
                }

                stream = urlConnection?.inputStream


                val jsonAsString = stream?.bufferedReader().use {it?.readText()}


                val categories = jsonAsString?.let { toCategories(it) }

                handler.post{
                    if (categories != null) {
                        callback.onResult(categories)
                    }
                }

            } catch (e: IOException) {
                handler.post { callback.onFailure(e.message ?: "erro desconhecido") }
            } finally {
                urlConnection?.disconnect()
                stream?.close()
            }
        }

    }

    private fun toCategories(jsonAsString: String) : List<Category>{
        val categories = mutableListOf<Category>()
        val movies = mutableListOf<Movie>()
        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategories = jsonRoot.getJSONArray("category")

        for (i in 0 until jsonCategories.length()){

            val jsonCategory = jsonCategories.getJSONObject(i)

            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")


            for (j in 0 until jsonMovies.length()){
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(id,coverUrl))
            }

            categories.add(Category(title,movies))

        }

        return categories
    }
}