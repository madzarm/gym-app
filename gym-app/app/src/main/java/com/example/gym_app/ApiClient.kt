package com.example.gym_app

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.110.195:8080/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    }
}

object ApiClient {
    val apiService: ApiService by lazy { RetrofitClient.retrofit.create(ApiService::class.java) }
}