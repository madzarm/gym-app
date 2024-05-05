package com.example.gym_app.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_app.api.ApiClient
import com.example.gym_app.common.TokenManager
import com.example.gym_app.common.readErrorMessage
import kotlinx.coroutines.launch
import org.gymapp.library.request.CreateRecurringClassRequest
import org.gymapp.library.request.ReviewGymClassRequest
import org.gymapp.library.request.ReviewTrainerRequest
import org.gymapp.library.request.UpdateClassRequest
import org.gymapp.library.request.UpdateGymClassInstanceRequest
import org.gymapp.library.response.GymClassDto
import org.gymapp.library.response.GymClassInstanceDto
import org.gymapp.library.response.GymMemberDto
import retrofit2.HttpException

data class GymClassReview(val gymClassInstanceDto: GymClassInstanceDto, val rating: Int, val review: String)

data class TrainerReview(val trainerId: String, val rating: Int, val review: String)

data class GymClassInstanceModel(
  val classId: String = "",
  val name: String = "",
  var description: String = "",
  var dateTime: String = "",
  var originalDateTime: String = "",
  var duration: String = "",
  var maxParticipants: String = "",
  var participantsIds: List<String> = emptyList(),
  var trainerId: String = "",
  var isCanceled: Boolean = false,
)

class GymClassViewModel : ViewModel() {
  private val _selectedGymClass = MutableLiveData<GymClassDto>()
  val selectedGymClass: MutableLiveData<GymClassDto> = _selectedGymClass

  private val _updatedGymClass = MutableLiveData<GymClassDto>()
  val updatedGymClass: MutableLiveData<GymClassDto> = _updatedGymClass

  private val _selectedInstance = MutableLiveData<GymClassInstanceModel>()
  val selectedInstance: MutableLiveData<GymClassInstanceModel> = _selectedInstance

  private val _selectedInstanceDto = MutableLiveData<GymClassInstanceDto>()
  val selectedInstanceDto: MutableLiveData<GymClassInstanceDto> = _selectedInstanceDto

  private val _createGymClassRequest = MutableLiveData<UpdateClassRequest>()
  val createGymClassRequest: MutableLiveData<UpdateClassRequest> = _createGymClassRequest

  private val _createRecurringGymClassRequest = MutableLiveData<CreateRecurringClassRequest>()
  val createRecurringGymClassRequest: MutableLiveData<CreateRecurringClassRequest> =
    _createRecurringGymClassRequest

  private val _gymClassReview = MutableLiveData<GymClassReview>()
  val gymClassReview: MutableLiveData<GymClassReview> = _gymClassReview

  private val _trainerReview = MutableLiveData<TrainerReview>()
  val trainerReview: MutableLiveData<TrainerReview> = _trainerReview

  fun setSelectedGymClass(gymClass: GymClassDto) {
    _selectedGymClass.value = gymClass
    _updatedGymClass.value = gymClass
  }

  fun setSelectedInstanceDto(gymClassInstanceDto: GymClassInstanceDto) {
    _selectedInstanceDto.value = gymClassInstanceDto
  }

  fun updateGymClass(update: GymClassDto.() -> GymClassDto) {
    _updatedGymClass.value = update(_updatedGymClass.value!!)
  }

  fun updateInstance(update: GymClassInstanceModel.() -> GymClassInstanceModel) {
    _selectedInstance.value = update(_selectedInstance.value ?: GymClassInstanceModel())
  }

  fun updateInstanceDto(update: GymClassInstanceDto.() -> GymClassInstanceDto) {
    _selectedInstanceDto.value = update(_selectedInstanceDto.value ?: GymClassInstanceDto(
        id = "",
        classId = "",
        name = "",
        description = "",
        dateTime = "",
        duration = "",
        maxParticipants = "",
        participantsIds = emptyList(),
        trainerId = "",
        gymClassModifiedInstance = null,
    ))
  }

  fun updateGymClassReview(update: GymClassReview.() -> GymClassReview) {
    _gymClassReview.value =
      update(
        _gymClassReview.value
          ?: GymClassReview(gymClassInstanceDto = _selectedInstanceDto.value!!, rating = 1, review = "")
      )
  }

  fun updateTrainerReview(update: TrainerReview.() -> TrainerReview) {
    _trainerReview.value =
      update(_trainerReview.value ?: TrainerReview(trainerId = "", rating = 1, review = ""))
  }

  fun updateRequest(update: UpdateClassRequest.() -> UpdateClassRequest) {
    _createGymClassRequest.value = update(_createGymClassRequest.value ?: UpdateClassRequest())
  }

  fun updateRecurringClassRequest(
    update: CreateRecurringClassRequest.() -> CreateRecurringClassRequest
  ) {
    _createRecurringGymClassRequest.value =
      update(_createRecurringGymClassRequest.value ?: CreateRecurringClassRequest())
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
          gymClassInstanceId = _selectedInstanceDto.value?.id ?: "",
          memberId = memberId,
        )
      val trainerReview =
        ReviewTrainerRequest(
          review = _trainerReview.value?.review ?: "",
          rating = _trainerReview.value?.rating ?: 1,
          trainerId = _selectedInstanceDto.value?.trainerId ?: "",
          memberId = memberId,
        )
      try {
        ApiClient.apiService.reviewGymClass(
          "Bearer ${TokenManager.getAccessToken(context)}",
          classReview,
        )
        ApiClient.apiService.reviewTrainer(
          "Bearer ${TokenManager.getAccessToken(context)}",
          trainerReview,
        )
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

  fun updateGymClassInstance(context: Context, onSuccess: () -> Unit, onFailure: (String) -> Unit) =
    viewModelScope.launch {
      val request =
        UpdateGymClassInstanceRequest(
          originalDateTime = _selectedInstance.value!!.originalDateTime,
          description = _selectedInstance.value!!.description,
          duration = _selectedInstance.value!!.duration,
          maxParticipants = _selectedInstance.value!!.maxParticipants.toInt(),
          dateTime = _selectedInstance.value!!.dateTime,
          isCanceled = _selectedInstance.value!!.isCanceled,
        )
      try {
        print(_selectedInstance.value.toString())
        ApiClient.apiService.updateRecurringGymClass(
          "Bearer ${TokenManager.getAccessToken(context)}",
          _selectedInstance.value?.classId!!,
          request,
        )
        onSuccess()
      } catch (e: HttpException) {
        onFailure(readErrorMessage(e))
      }
    }

  fun joinGymClass(
    context: Context,
    gymClassId: String,
    dateTime: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) =
    viewModelScope.launch {
      try {
        val gymMemberDto: GymMemberDto =
          ApiClient.apiService.registerToClass(
            "Bearer ${TokenManager.getAccessToken(context)}",
            gymClassId,
            dateTime
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

  fun createRecurringGymClass(
    context: Context,
    gymId: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) =
    viewModelScope.launch {
      val request =
        CreateRecurringClassRequest(
          name = _createGymClassRequest.value!!.name ?: "",
          description = _createGymClassRequest.value!!.description ?: "",
          duration = _createGymClassRequest.value!!.duration ?: "0",
          maxParticipants = createGymClassRequest.value!!.maxParticipants ?: 0,
          dateTime = _createGymClassRequest.value!!.dateTime ?: "",
          maxNumOfOccurrences = _createRecurringGymClassRequest.value!!.maxNumOfOccurrences,
          daysOfWeek = _createRecurringGymClassRequest.value!!.daysOfWeek,
        )

      try {
        ApiClient.apiService.createRecurringGymClass(
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
