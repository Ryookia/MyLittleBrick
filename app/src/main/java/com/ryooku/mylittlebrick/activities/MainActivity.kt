package com.ryooku.mylittlebrick.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.ryooku.mylittlebrick.R
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request


class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        fetchButton.setOnClickListener { makeRequest() }
    }

    private fun makeRequest() {
        Request.Builder().url(urlInput.text.toString()).build().also {
            val response = client.newCall(it).execute()
            if (!response.isSuccessful) {
                Toast.makeText(this, "We fucked up", Toast.LENGTH_LONG).show()
                Log.e("MainActivity", response.body().toString())
                return
            }
            val body = response.body()!!
            val stream = body.byteStream()
            val bitmap = BitmapFactory.decodeStream(stream)
            stream.close()
            imageHolder.setImageBitmap(bitmap)
        }

    }
}
