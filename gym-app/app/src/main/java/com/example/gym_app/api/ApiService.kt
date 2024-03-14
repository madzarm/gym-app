package com.example.gym_app.api

import org.gymapp.library.request.CreateUserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("/user")
    suspend fun createUser(@Body request: CreateUserRequest, @Header("Authorization") authHeader: String): Response<Void>
}