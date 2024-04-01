package com.example.gym_app.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_app.api.ApiClient
import com.example.gym_app.common.TokenManager
import kotlinx.coroutines.launch
import org.gymapp.library.request.UpdateClassRequest
import org.gymapp.library.response.GymClassDto

class GymClassViewModel : ViewModel() {
  private val _selectedGymClass = MutableLiveData<GymClassDto>()
  val selectedGymClass: MutableLiveData<GymClassDto> = _selectedGymClass

  private val _updatedGymClass = MutableLiveData<GymClassDto>()
  val updatedGymClass: MutableLiveData<GymClassDto> = _updatedGymClass

  private val _createGymClassRequest = MutableLiveData<UpdateClassRequest>()
  val createGymClassRequest: MutableLiveData<UpdateClassRequest> = _createGymClassRequest

  fun setSelectedGymClass(gymClass: GymClassDto) {
    _selectedGymClass.value = gymClass
    _updatedGymClass.value = gymClass
  }

  fun updateGymClass(update: GymClassDto.() -> GymClassDto) {
    _updatedGymClass.value = update(_updatedGymClass.value!!)
  }

  fun updateRequest(update: UpdateClassRequest.() -> UpdateClassRequest) {
    _createGymClassRequest.value = update(_createGymClassRequest.value ?: UpdateClassRequest())
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
}
