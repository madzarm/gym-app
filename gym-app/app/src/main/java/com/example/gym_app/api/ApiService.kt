package com.example.gym_app.api

import org.gymapp.library.request.CreateUserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/user")
    suspend fun register(@Body request: CreateUserRequest): Response<Void>
}