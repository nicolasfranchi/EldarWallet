package com.inc.eldartest.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface QRCodeApiService {
    companion object {
        const val API_KEY = "Mf7QqhqQrDmshCbnLp1DJ1FFJTeBp1hdW1gjsntPEIE0cHsJBc"
        const val API_HOST = "qrcode68.p.rapidapi.com"
    }

    @Headers(
        "x-rapidapi-key: $API_KEY",
        "x-rapidapi-host: $API_HOST"
    )
    @POST("/gradient")
    suspend fun generateQRCode(@Query("data") text: String): Response<ResponseBody>
}
