package com.example.gym_app.api

import org.gymapp.library.request.CreateFrequencyBasedChallengeRequest
import org.gymapp.library.request.CreateGymRequest
import org.gymapp.library.request.CreateInviteFriendChallengeRequest
import org.gymapp.library.request.CreateRecurringClassRequest
import org.gymapp.library.request.CreateTimedVisitBasedChallengeRequest
import org.gymapp.library.request.CreateUserRequest
import org.gymapp.library.request.ReviewGymClassRequest
import org.gymapp.library.request.ReviewTrainerRequest
import org.gymapp.library.request.UpdateChallengeRequest
import org.gymapp.library.request.UpdateClassRequest
import org.gymapp.library.request.UpdateGymClassInstanceRequest
import org.gymapp.library.response.AccessCodeDto
import org.gymapp.library.response.AccountCompletedDto
import org.gymapp.library.response.AccountLinkDto
import org.gymapp.library.response.ChallengeDto
import org.gymapp.library.response.GymClassDto
import org.gymapp.library.response.GymClassInstanceDto
import org.gymapp.library.response.GymClassReviewDto
import org.gymapp.library.response.GymClassWithReviewsDto
import org.gymapp.library.response.GymMemberDto
import org.gymapp.library.response.GymMemberDtoFull
import org.gymapp.library.response.GymTrainerDto
import org.gymapp.library.response.GymTrainerReviewDto
import org.gymapp.library.response.GymTrainerWithReviewsDto
import org.gymapp.library.response.GymUserDto
import org.gymapp.library.response.GymVisitDto
import org.gymapp.library.response.PaymentSheetResponse
import org.gymapp.library.response.SubscriptionStatusDto
import org.gymapp.library.response.UserDto
import org.gymapp.library.response.VisitCountByDay
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
  suspend fun createUser(
    @Body request: CreateUserRequest,
    @Header("Authorization") authHeader: String,
  ): Response<Void>

  @PUT("/users")
  suspend fun updateUser(
    @Header("Authorization") authHeader: String,
    @Body request: CreateUserRequest,
  ): Response<Void>

  @GET("/users/current")
  suspend fun getCurrentUser(@Header("Authorization") authHeader: String): Response<UserDto>

  @GET("/users/gyms")
  suspend fun getUserGyms(@Header("Authorization") authHeader: String): Response<List<GymUserDto>>

  @POST("/users/join-as-member")
  suspend fun joinGymAsMember(
    @Header("Authorization") authHeader: String,
    @Query("code") code: String,
  ): GymUserDto

  @POST("/users/join-as-member/invite/")
  suspend fun joinGymWithFriendCode(
    @Header("Authorization") authHeader: String,
    @Query("code") code: String,
  ): GymUserDto

  @POST("/users/join-as-trainer")
  suspend fun joinGymAsTrainer(
    @Header("Authorization") authHeader: String,
    @Query("code") code: String,
  ): GymUserDto

  @GET("owners/gyms/{id}/accessCode")
  suspend fun generateAccessCode(
    @Header("Authorization") authHeader: String,
    @Path("id") gymId: String,
  ): AccessCodeDto

  @POST("/gyms")
  suspend fun createGym(
    @Header("Authorization") authHeader: String,
    @Body request: CreateGymRequest,
  ): GymUserDto

  @GET("/trainers/gyms/{gymId}")
  suspend fun getTrainer(
    @Header("Authorization") authHeader: String,
    @Path("gymId") gymId: String,
  ): GymTrainerDto

  @POST("/gyms/members")
  suspend fun getGymMembers(
    @Header("Authorization") authHeader: String,
    @Body memberIds: List<String>,
  ): List<GymMemberDtoFull>

  @GET("/trainers/gyms/{gymId}/upcoming-classes")
  suspend fun getTrainerWithUpcomingClasses(
    @Header("Authorization") authHeader: String,
    @Path("gymId") gymId: String,
  ): GymTrainerDto

  @PUT("/trainers/gyms/classes/{classId}")
  suspend fun updateGymClass(
    @Header("Authorization") authHeader: String,
    @Path("classId") classId: String,
    @Body request: UpdateClassRequest,
  ): GymTrainerDto

  @PUT("/trainers/gyms/classes/{classId}/instances")
  suspend fun updateRecurringGymClass(
    @Header("Authorization") authHeader: String,
    @Path("classId") classId: String,
    @Body request: UpdateGymClassInstanceRequest,
  ): GymTrainerDto

  @DELETE("/trainers/gyms/classes/{classId}")
  suspend fun deleteGymClass(
    @Header("Authorization") authHeader: String,
    @Path("classId") classId: String,
  ): GymTrainerDto

  @DELETE("/trainers/gyms/classes/{classId}/cancel")
  suspend fun cancelGymClass(
    @Header("Authorization") authHeader: String,
    @Path("classId") classId: String,
    @Query("dateTime") dateTime: String,
  ): GymTrainerDto

  @POST("/trainers/gyms/{gymId}/classes")
  suspend fun createGymClass(
    @Header("Authorization") authHeader: String,
    @Path("gymId") gymId: String,
    @Body request: UpdateClassRequest,
  ): GymTrainerDto

  @POST("/trainers/gyms/{gymId}/classes/recurring")
  suspend fun createRecurringGymClass(
    @Header("Authorization") authHeader: String,
    @Path("gymId") gymId: String,
    @Body request: CreateRecurringClassRequest,
  ): GymTrainerDto

  @GET("/gyms/{gymId}/classes")
  suspend fun getUpcomingGymClasses(
    @Header("Authorization") authHeader: String,
    @Path("gymId") gymId: String,
  ): List<GymClassDto>

  @POST("/members/classes/{classId}")
  suspend fun registerToClass(
    @Header("Authorization") authHeader: String,
    @Path("classId") classId: String,
    @Query("dateTime") dateTime: String,
  ): GymMemberDto

  @GET("/gyms/{gymId}/live")
  suspend fun getLiveStatus(
    @Header("Authorization") authHeader: String,
    @Path("gymId") gymId: String,
  ): Int

  @GET("/gyms/{gymId}/gymVisits")
  suspend fun getGymVisits(
    @Header("Authorization") authHeader: String,
    @Path("gymId") gymId: String,
  ): List<GymVisitDto>

  @POST("/members/gyms/review-class")
  suspend fun reviewGymClass(
    @Header("Authorization") authHeader: String,
    @Body request: ReviewGymClassRequest,
  ): GymClassReviewDto

  @POST("/members/gyms/review-trainer")
  suspend fun reviewTrainer(
    @Header("Authorization") authHeader: String,
    @Body request: ReviewTrainerRequest,
  ): GymTrainerReviewDto

  @GET("/members/gyms/{gymId}/classes-for-review")
  suspend fun getGymClassesForReview(
    @Header("Authorization") authHeader: String,
    @Path("gymId") gymId: String,
  ): List<GymClassInstanceDto>

  @GET("/owners/gyms/{gymId}/trainers")
  suspend fun getTrainersWithReviews(
    @Header("Authorization") authHeader: String,
    @Path("gymId") gymId: String,
  ): List<GymTrainerWithReviewsDto>

  @GET("/owners/gyms/{gymId}/classes")
  suspend fun getGymClassesWithReviews(
    @Header("Authorization") authHeader: String,
    @Path("gymId") gymId: String,
  ): List<GymClassWithReviewsDto>

  @GET("/challenges")
  suspend fun fetchActiveChallenges(
    @Header("Authorization") authHeader: String,
    @Query("gymId") gymId: String,
  ): List<ChallengeDto>

  @POST("/challenges/timed-visit-based")
  suspend fun createTimedBasedChallenge(
    @Header("Authorization") authHeader: String,
    @Query("gymId") gymId: String,
    @Body request: CreateTimedVisitBasedChallengeRequest,
  ): Response<Unit>

  @POST("/challenges/frequency-based")
  suspend fun createFrequencyBasedChallenge(
    @Header("Authorization") authHeader: String,
    @Query("gymId") gymId: String,
    @Body request: CreateFrequencyBasedChallengeRequest,
  ): Response<Unit>

  @POST("/challenges/invite-based")
  suspend fun createInviteFriendChallenge(
    @Header("Authorization") authHeader: String,
    @Query("gymId") gymId: String,
    @Body request: CreateInviteFriendChallengeRequest,
  ): Response<Unit>

  @DELETE("/challenges/{challengeId}")
  suspend fun deleteChallenge(
    @Header("Authorization") authHeader: String,
    @Path("challengeId") challengeId: String,
  ): Response<Unit>

  @PUT("/challenges/{challengeId}")
  suspend fun updateChallenge(
    @Header("Authorization") authHeader: String,
    @Path("challengeId") challengeId: String,
    @Body request: UpdateChallengeRequest,
  ): Response<Unit>

  @GET("/challenges/unclaimed")
  suspend fun fetchUnclaimedChallenges(
    @Header("Authorization") authHeader: String,
    @Query("gymId") gymId: String,
  ): List<ChallengeDto>

  @GET("/challenges/points")
  suspend fun fetchPoints(
    @Header("Authorization") authHeader: String,
    @Query("gymId") gymId: String,
  ): Int

  @POST("/challenges/{challengeId}/claim")
  suspend fun claimChallenge(
    @Header("Authorization") authHeader: String,
    @Path("challengeId") challengeId: String,
  ): Response<Unit>

  @GET("/gyms/{gymId}/gymVisits/heatMap")
  suspend fun fetchHeatmapData(
    @Header("Authorization") authHeader: String,
    @Path("gymId") gymId: String,
  ): List<VisitCountByDay>

  @GET("/gyms/payment-sheet")
  suspend fun getPaymentSheet(
    @Header("Authorization") authHeader: String,
    @Query("gymId") gymId: String,
  ): PaymentSheetResponse

  @GET("/gyms/setup-intent")
  suspend fun createSetupIntent(
    @Header("Authorization") authHeader: String,
    @Query("gymId") gymId: String,
  ): PaymentSheetResponse

  @GET("/gyms/confirm-setup-intent")
  suspend fun confirmSetupIntent(
    @Header("Authorization") authHeader: String,
    @Query("gymId") gymId: String,
    @Query("setupIntentId") setupIntentId: String,
  ): Response<Unit>

  @GET("/gyms/subscription-status")
  suspend fun getSubscriptionStatus(
    @Header("Authorization") authHeader: String,
    @Query("gymId") gymId: String,
  ): SubscriptionStatusDto


  @GET("/gyms/create-account-link")
  suspend fun createAccountLink(
    @Header("Authorization") authHeader: String,
    @Query("gymId") gymId: String,
    @Query("returnUrl") returnUrl: String,
    @Query("refreshUrl") refreshUrl: String
  ): AccountLinkDto

  @GET("/gyms/stripe-connect-account-completed")
  suspend fun isStripeConnectAccountCompleted(
    @Header("Authorization") authHeader: String,
    @Query("gymId") gymId: String,
  ): AccountCompletedDto
}
