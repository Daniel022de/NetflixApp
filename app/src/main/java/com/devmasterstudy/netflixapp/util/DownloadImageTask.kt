package com.devmasterstudy.netflixapp.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class DownloadImageTask (private val callback: CallBack) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    private var urlConnection: HttpsURLConnection? = null
    private var stream : InputStream? = null

    interface CallBack {
        fun onResult(bitmap: Bitmap)
    }


    fun execute(url:String) {
        executor.execute{
            try {
                val requestURL = URL(url)
                urlConnection = requestURL.openConnection() as HttpsURLConnection

                urlConnection?.apply {
                    readTimeout = 2000
                    connectTimeout = 2000
                }

                val statusCode = urlConnection?.responseCode
                if (statusCode != null) {
                    if (statusCode > 400) {
                        throw IOException("Erro na comunicaçao com o servidor!")
                    }
                }

                stream = urlConnection?.inputStream
                val bitmap = BitmapFactory.decodeStream(stream)

                handler.post {
                    callback.onResult(bitmap)
                }

            } catch (e:IOException){ Log.e("Error",e.message ?: "erro desconhecido",e)}
            finally {
                urlConnection?.disconnect()
                stream?.close()
            }

        }

    }

}