package com.example.gym_app.api

import org.gymapp.library.request.CreateAccountRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/security/register")
    suspend fun register(@Body request: CreateAccountRequest): Response<Void>
}