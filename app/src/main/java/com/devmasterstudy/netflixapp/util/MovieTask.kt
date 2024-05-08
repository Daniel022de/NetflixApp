package com.devmasterstudy.netflixapp.util


import android.os.Handler
import android.os.Looper
import com.devmasterstudy.netflixapp.model.Movie
import com.devmasterstudy.netflixapp.model.MovieDetail
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class MovieTask(private val callback: CallBack) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    private var urlConnection: HttpsURLConnection? = null
    private var stream: InputStream? = null

    interface CallBack {
        fun onPreExecute()
        fun onResult(movieDetail: MovieDetail)
        fun onFailure(message: String)
    }

    fun execute(url: String) {
        handler.post { callback.onPreExecute() }

        executor.execute {
            try {
                val requestURL = URL(url)
                urlConnection = requestURL.openConnection() as HttpsURLConnection

                urlConnection?.apply {
                    readTimeout = 2000
                    connectTimeout = 2000
                }

                val statusCode = urlConnection?.responseCode

                if (statusCode == 400) {

                    stream = urlConnection?.errorStream

                    val jsonAsString = stream?.bufferedReader().use { it?.readText() }

                    val json = JSONObject(jsonAsString)
                    val message = json.getString("message")
                    throw  IOException(message)

                } else if (statusCode != null) {
                    if (statusCode > 400) {
                        throw IOException("Erro na comunicaçao com o servidor!")
                    }
                }

                stream = urlConnection?.inputStream

                val jsonAsString = stream?.bufferedReader().use { it?.readText() }

                val movieDetail = jsonAsString?.let { toMovieDetail(it) }
                handler.post {
                    if (movieDetail != null) {
                        callback.onResult(movieDetail)
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

    private fun toMovieDetail(jsonAsString: String): MovieDetail {
        val similars = mutableListOf<Movie>()
        val jsonRoot = JSONObject(jsonAsString)

        val id = jsonRoot.getInt("id")
        val title = jsonRoot.getString("title")
        val desc = jsonRoot.getString("desc")
        val cast = jsonRoot.getString("cast")
        val coverUrl = jsonRoot.getString("cover_url")

        val jsonMovies = jsonRoot.getJSONArray("movie")

        for (i in 0 until jsonMovies.length()) {
            val jsonMovie = jsonMovies.getJSONObject(i)

            val similarId = jsonMovie.getInt("id")
            val similarCoverUrl = jsonMovie.getString("cover_url")

            val m = Movie(similarId,similarCoverUrl)
            similars.add(m)
        }
        val movie = Movie(id,coverUrl,title,desc,cast)

        return MovieDetail(movie, similars)
    }
}