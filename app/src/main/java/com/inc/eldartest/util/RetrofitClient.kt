package com.inc.eldartest.util

import com.inc.eldartest.data.QRCodeApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://zingzy.p.rapidapi.com"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-rapidapi-key", "Mf7QqhqQrDmshCbnLp1DJ1FFJTeBp1hdW1gjsntPEIE0cHsJBc")
                .addHeader("x-rapidapi-host", "zingzy.p.rapidapi.com")
                .build()
            chain.proceed(request)
        }
        .build()

    val qrCodeApi: QRCodeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QRCodeApiService::class.java)
    }
}

