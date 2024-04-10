package com.example.gym_app.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_app.api.ApiClient
import com.example.gym_app.common.TokenManager
import com.example.gym_app.common.readErrorMessage
import kotlinx.coroutines.launch
import org.gymapp.library.request.ReviewGymClassRequest
import org.gymapp.library.request.ReviewTrainerRequest
import org.gymapp.library.request.UpdateClassRequest
import org.gymapp.library.response.GymClassDto
import org.gymapp.library.response.GymMemberDto
import retrofit2.HttpException

data class GymClassReview(val gymClassDto: GymClassDto, val rating: Int, val review: String)

data class TrainerReview(val trainerId: String, val rating: Int, val review: String)

class GymClassViewModel : ViewModel() {
  private val _selectedGymClass = MutableLiveData<GymClassDto>()
  val selectedGymClass: MutableLiveData<GymClassDto> = _selectedGymClass

  private val _updatedGymClass = MutableLiveData<GymClassDto>()
  val updatedGymClass: MutableLiveData<GymClassDto> = _updatedGymClass

  private val _createGymClassRequest = MutableLiveData<UpdateClassRequest>()
  val createGymClassRequest: MutableLiveData<UpdateClassRequest> = _createGymClassRequest

  private val _gymClassReview = MutableLiveData<GymClassReview>()
  val gymClassReview: MutableLiveData<GymClassReview> = _gymClassReview

  private val _trainerReview = MutableLiveData<TrainerReview>()
  val trainerReview: MutableLiveData<TrainerReview> = _trainerReview

  fun setSelectedGymClass(gymClass: GymClassDto) {
    _selectedGymClass.value = gymClass
    _updatedGymClass.value = gymClass
  }

  fun updateGymClass(update: GymClassDto.() -> GymClassDto) {
    _updatedGymClass.value = update(_updatedGymClass.value!!)
  }

  fun updateGymClassReview(update: GymClassReview.() -> GymClassReview) {
    _gymClassReview.value =
      update(
        _gymClassReview.value
          ?: GymClassReview(gymClassDto = _selectedGymClass.value!!, rating = 1, review = "")
      )
  }

  fun updateTrainerReview(update: TrainerReview.() -> TrainerReview) {
    _trainerReview.value =
      update(_trainerReview.value ?: TrainerReview(trainerId = "", rating = 1, review = ""))
  }

  fun updateRequest(update: UpdateClassRequest.() -> UpdateClassRequest) {
    _createGymClassRequest.value = update(_createGymClassRequest.value ?: UpdateClassRequest())
  }

  fun submitReview(
    context: Context,
    memberId: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit,
  ) =
    viewModelScope.launch {
      val classReview =
        ReviewGymClassRequest(
          review = _gymClassReview.value?.review ?: "",
          rating = _gymClassReview.value?.rating ?: 1,
          classId = _selectedGymClass.value?.id ?: "",
          memberId = memberId,
        )
      val trainerReview =
        ReviewTrainerRequest (
            review = _trainerReview.value?.review ?: "",
            rating = _trainerReview.value?.rating ?: 1,
            trainerId = _selectedGymClass.value?.trainerId ?: "",
            memberId = memberId,
        )
      try {
        ApiClient.apiService.reviewGymClass("Bearer ${TokenManager.getAccessToken(context)}", classReview)
        ApiClient.apiService.reviewTrainer("Bearer ${TokenManager.getAccessToken(context)}", trainerReview)
        onSuccess()
      } catch (e: HttpException) {
        onFailure(readErrorMessage(e))
      }
    }

  fun updateGymClass(context: Context) =
    viewModelScope.launch {
      _selectedGymClass.value = _updatedGymClass.value
      val request =
        UpdateClassRequest(
          name = _updatedGymClass.value!!.name,
          description = _updatedGymClass.value!!.description,
          duration = _updatedGymClass.value!!.duration,
          maxParticipants = _updatedGymClass.value!!.maxParticipants?.toInt(),
          dateTime = _updatedGymClass.value!!.dateTime,
        )
      ApiClient.apiService.updateGymClass(
        "Bearer ${TokenManager.getAccessToken(context)}",
        _selectedGymClass.value?.id!!,
        request,
      )
    }

  fun joinGymClass(
    context: Context,
    gymClassId: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) =
    viewModelScope.launch {
      try {
        val gymMemberDto: GymMemberDto =
          ApiClient.apiService.registerToClass(
            "Bearer ${TokenManager.getAccessToken(context)}",
            gymClassId,
          )
        onSuccess()
      } catch (e: HttpException) {
        val errorMessage = readErrorMessage(e)
        onError(errorMessage)
      }
    }

  fun createGymClass(
    context: Context,
    gymId: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) =
    viewModelScope.launch {
      val request =
        UpdateClassRequest(
          name = _createGymClassRequest.value!!.name,
          description = _createGymClassRequest.value!!.description,
          duration = _createGymClassRequest.value!!.duration,
          maxParticipants = _createGymClassRequest.value!!.maxParticipants,
          dateTime = _createGymClassRequest.value!!.dateTime,
        )

      try {
        ApiClient.apiService.createGymClass(
          "Bearer ${TokenManager.getAccessToken(context)}",
          gymId,
          request,
        )
        onSuccess()
      } catch (e: Exception) {
        onError(e.message ?: "An error occurred")
      }
    }

  fun deleteGymClass(context: Context, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
    viewModelScope.launch {
      try {
        ApiClient.apiService.deleteGymClass(
          "Bearer ${TokenManager.getAccessToken(context)}",
          _selectedGymClass.value?.id ?: "",
        )
        onSuccess()
      } catch (e: HttpException) {
        onFailure(readErrorMessage(e))
      }
    }
  }
}
