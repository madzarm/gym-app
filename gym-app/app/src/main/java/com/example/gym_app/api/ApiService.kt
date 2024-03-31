package com.example.gym_app.api

import org.gymapp.library.request.CreateGymRequest
import org.gymapp.library.request.CreateUserRequest
import org.gymapp.library.response.AccessCodeDto
import org.gymapp.library.response.GymUserDto
import org.gymapp.library.response.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/users")
    suspend fun createUser(@Body request: CreateUserRequest, @Header("Authorization") authHeader: String): Response<Void>

    @GET("/users/current")
    suspend fun getCurrentUser(@Header("Authorization") authHeader: String): Response<UserDto>

   @GET("/users/gyms")
    suspend fun getUserGyms(@Header("Authorization") authHeader: String): Response<List<GymUserDto>>

    @POST("/users/join-as-member")
    suspend fun joinGymAsMember(@Header("Authorization") authHeader: String, @Query("code") code: String): GymUserDto

    @POST("/users/join-as-trainer")
    suspend fun joinGymAsTrainer(@Header("Authorization") authHeader: String, @Query("code") code: String): GymUserDto

    @GET("owners/gyms/{id}/accessCode")
    suspend fun generateAccessCode(@Header("Authorization") authHeader: String, @Path("id") gymId: String): AccessCodeDto

    @POST("/gyms")
    suspend fun createGym(@Header("Authorization") authHeader: String, @Body request: CreateGymRequest): GymUserDto
}