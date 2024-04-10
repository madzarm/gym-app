package com.example.gym_app.api

import org.gymapp.library.request.CreateGymRequest
import org.gymapp.library.request.CreateUserRequest
import org.gymapp.library.request.UpdateClassRequest
import org.gymapp.library.response.AccessCodeDto
import org.gymapp.library.response.GymClassDto
import org.gymapp.library.response.GymMemberDto
import org.gymapp.library.response.GymTrainerDto
import org.gymapp.library.response.GymUserDto
import org.gymapp.library.response.GymVisitDto
import org.gymapp.library.response.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @GET("/trainers/gyms/{gymId}")
    suspend fun getTrainer(@Header("Authorization") authHeader: String, @Path("gymId") gymId: String): GymTrainerDto

    @GET("/trainers/gyms/{gymId}/upcoming-classes")
    suspend fun getTrainerWithUpcomingClasses(@Header("Authorization") authHeader: String, @Path("gymId") gymId: String): GymTrainerDto

    @PUT("/trainers/gyms/classes/{classId}")
    suspend fun updateGymClass(@Header("Authorization") authHeader: String, @Path("classId") classId: String, @Body request: UpdateClassRequest): GymTrainerDto

    @DELETE("/trainers/gyms/classes/{classId}")
    suspend fun deleteGymClass(@Header("Authorization") authHeader: String, @Path("classId") classId: String): GymTrainerDto

    @POST("/trainers/gyms/{gymId}/classes")
    suspend fun createGymClass(@Header("Authorization") authHeader: String, @Path("gymId") gymId: String, @Body request: UpdateClassRequest): GymTrainerDto

    @GET("/gyms/{gymId}/classes")
    suspend fun getGymClasses(@Header("Authorization") authHeader: String, @Path("gymId") gymId: String): List<GymClassDto>

    @POST("/members/classes/{classId}")
    suspend fun registerToClass(@Header("Authorization") authHeader: String, @Path("classId") classId: String): GymMemberDto

    @GET("/gyms/{gymId}/live")
    suspend fun getLiveStatus(@Header("Authorization") authHeader: String, @Path("gymId") gymId: String): Int

    @GET("/gyms/{gymId}/gymVisits")
    suspend fun getGymVisits(@Header("Authorization") authHeader: String, @Path("gymId") gymId: String): List<GymVisitDto>
}