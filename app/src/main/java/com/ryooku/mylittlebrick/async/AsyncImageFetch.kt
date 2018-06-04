package com.ryooku.mylittlebrick.async

import android.os.AsyncTask
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayOutputStream

class AsyncImageFetch(
        private val onPreExe: () -> Unit,
        private val onPostExe: (Int, ByteArray?) -> Unit,
        private val inventoryItemID: Int
) : AsyncTask<String, String, ByteArray?>() {

    private val client = OkHttpClient()

    override fun onPreExecute() {
        super.onPreExecute()
        onPreExe()
    }

    override fun doInBackground(vararg params: String?): ByteArray? {
        if (params.isEmpty()) return null
        val count = params[0]!!.toInt()
        for (i in 1..count) {
            if (params[i] == null) continue
            val result = makeRequest(params[i]!!)
            if (result != null) return result
        }
        return null
    }

    override fun onPostExecute(result: ByteArray?) {
        super.onPostExecute(result)
        onPostExe(inventoryItemID, result)
    }

    private fun makeRequest(url: String): ByteArray? {
        val bufferSize = 2048
        Request.Builder().url(url).build().also {
            val response = client.newCall(it).execute()
            if (!response.isSuccessful) {
                return null
            }
            val body = response.body()!!
            val stream = body.byteStream()

            val buffer = ByteArrayOutputStream()

            var nRead: Int
            val data = ByteArray(bufferSize)

            nRead = stream.read(data, 0, bufferSize)
            while (nRead != -1) {
                buffer.write(data, 0, nRead)
                nRead = stream.read(data, 0, bufferSize)
            }

            buffer.flush()

            stream.close()

            return buffer.toByteArray()
        }
        return null
    }
}