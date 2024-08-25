package com.inc.eldartest.data

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class QRCodeRepository(private val apiService: QRCodeApiService) {
    //suspend fun generateQRCode(text: String) = apiService.generateQRCode(text)

    fun generateQRCode(text: String): ByteArray? {
        val client = OkHttpClient()

        val mediaType =
            "multipart/form-data; boundary=---011000010111000001101001".toMediaTypeOrNull()
        val body = RequestBody.create(
            mediaType,
            "-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"text\"\r\n\r\n$text\r\n-----011000010111000001101001--\r\n\r\n"
        )
        val request = Request.Builder()
            .url("https://qrcode68.p.rapidapi.com/classic")
            .post(body)
            .addHeader("x-rapidapi-key", "Mf7QqhqQrDmshCbnLp1DJ1FFJTeBp1hdW1gjsntPEIE0cHsJBc")
            .addHeader("x-rapidapi-host", "qrcode68.p.rapidapi.com")
            .addHeader("Content-Type", "multipart/form-data; boundary=---011000010111000001101001")
            .build()

        client.newCall(request).execute().use { response ->
            return response.body?.bytes()
        }
    }
}