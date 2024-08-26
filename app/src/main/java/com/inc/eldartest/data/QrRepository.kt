package com.inc.eldartest.data

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class QrRepository {

    private val API_URL = "https://qrcode68.p.rapidapi.com/classic"
    private val API_KEY = "Mf7QqhqQrDmshCbnLp1DJ1FFJTeBp1hdW1gjsntPEIE0cHsJBc"

    fun generateQRCode(text: String): ByteArray? {
        val client = OkHttpClient()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("text", text)
            .build()

        val request = Request.Builder()
            .url(API_URL)
            .post(requestBody)
            .addHeader("x-rapidapi-key", API_KEY)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body?.bytes()
        }
    }

}