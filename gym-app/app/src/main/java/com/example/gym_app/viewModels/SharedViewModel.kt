package com.example.gym_app.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_app.api.ApiClient
import com.example.gym_app.common.TokenManager
import kotlinx.coroutines.launch
import org.gymapp.library.request.CreateFrequencyBasedChallengeRequest
import org.gymapp.library.request.CreateTimedVisitBasedChallengeRequest
import org.gymapp.library.request.UpdateChallengeRequest
import org.gymapp.library.response.AccessCodeDto
import org.gymapp.library.response.ChallengeDto
import org.gymapp.library.response.ChallengeType
import org.gymapp.library.response.GymClassDto
import org.gymapp.library.response.GymClassInstanceDto
import org.gymapp.library.response.GymUserDto
import java.time.LocalDateTime
import java.time.LocalTime

data class UpdatableChallenge(
    var id: String? = "",
    var name: String? = "",
    var description: String? = "",
    var expiryDate: LocalDateTime? = null,
    var pointsValue: String? = "",
    var type: ChallengeType? = null,
    var frequencyCount: String? = null,
    var startTimeCriteria: LocalTime? = null,
    var endTimeCriteria: LocalTime? = null,
)

class SharedViewModel : ViewModel() {
    private val _selectedGymUser = MutableLiveData<GymUserDto>()
    val selectedGymUser: LiveData<GymUserDto> = _selectedGymUser

    private val _accessCode = MutableLiveData<AccessCodeDto>()
    val accessCode: LiveData<AccessCodeDto> = _accessCode

    private val _gymClasses = MutableLiveData<List<GymClassDto>>()
    val gymClasses: LiveData<List<GymClassDto>> = _gymClasses

    private val _gymClassesForReview = MutableLiveData<List<GymClassInstanceDto>>()
    val gymClassesForReview: LiveData<List<GymClassInstanceDto>> = _gymClassesForReview

    private val _liveStatus = MutableLiveData<Int>()
    val liveStatus: LiveData<Int> = _liveStatus

    private val _challengeDtos = MutableLiveData<List<ChallengeDto>>()
    val challengeDtos: LiveData<List<ChallengeDto>> = _challengeDtos

    private val _updatableChallenge = MutableLiveData<UpdatableChallenge>()
    val updatableChallenge: LiveData<UpdatableChallenge> = _updatableChallenge

    fun selectGym(gymUserDto: GymUserDto) {
        _selectedGymUser.value = gymUserDto
    }

    fun updateSelectedChallenge(update: UpdatableChallenge.() -> UpdatableChallenge) {
        _updatableChallenge.value = update(_updatableChallenge.value ?: UpdatableChallenge())
    }


    fun getLiveStatus(context: Context) =
        viewModelScope.launch {
            val liveStatus: Int = ApiClient.apiService.getLiveStatus("Bearer ${TokenManager.getAccessToken(context)}", _selectedGymUser.value?.gym?.id ?: "")
            _liveStatus.value = liveStatus
        }

    fun generateAccessCode(context: Context) =
        viewModelScope.launch {
            val accessCodeDto: AccessCodeDto = ApiClient.apiService.generateAccessCode("Bearer ${TokenManager.getAccessToken(context)}", _selectedGymUser.value?.gym?.id ?: "")
            _accessCode.value = accessCodeDto
        }

    fun getTrainerGymClasses(context: Context) =
        viewModelScope.launch {
            val gymClasses: List<GymClassDto> = ApiClient.apiService.getTrainerWithUpcomingClasses("Bearer ${TokenManager.getAccessToken(context)}", _selectedGymUser.value?.gym?.id ?: "").gymClasses
            _gymClasses.value = gymClasses
        }

    fun getUpcomingGymClasses(context: Context, gymId: String) {
        viewModelScope.launch {
            val gymClasses: List<GymClassDto> = ApiClient.apiService.getUpcomingGymClasses("Bearer ${TokenManager.getAccessToken(context)}", gymId)
            _gymClasses.value = gymClasses
        }
    }

    fun getGymClassesForReview(context: Context, gymId: String) {
        viewModelScope.launch {
            val gymClasses: List<GymClassInstanceDto> = ApiClient.apiService.getGymClassesForReview("Bearer ${TokenManager.getAccessToken(context)}", gymId)
            _gymClassesForReview.value = gymClasses
        }
    }

    fun fetchActiveChallenges(
        context: Context,
        gymId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {

            try {
                val challengeDtos: List<ChallengeDto> = ApiClient.apiService.fetchActiveChallenges("Bearer ${TokenManager.getAccessToken(context)}", gymId)
                _challengeDtos.value = challengeDtos
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred")

            }
        }
    }

    fun createTimeBasedChallenge(
        context: Context,
        gymId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = CreateTimedVisitBasedChallengeRequest(
                    name = _updatableChallenge.value?.name ?: "",
                    description = _updatableChallenge.value?.description ?: "",
                    expiryDate = _updatableChallenge.value?.expiryDate?.toString() ?: "",
                    pointsValue = _updatableChallenge.value?.pointsValue?.toInt() ?: 0,
                    startTime = _updatableChallenge.value?.startTimeCriteria?.toString() ?: "",
                    endTime = _updatableChallenge.value?.endTimeCriteria?.toString() ?: ""
                )
                val response = ApiClient.apiService.createTimedBasedChallenge("Bearer ${TokenManager.getAccessToken(context)}", gymId, request)
                val code = response.code()
                if (code == 201) {
                    onSuccess()
                } else {
                    onError(response.message() ?: "An error occurred")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred")
            }
        }
    }

    fun createFrequencyBasedChallenge(
        context: Context,
        gymId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = CreateFrequencyBasedChallengeRequest(
                    name = _updatableChallenge.value?.name ?: "",
                    description = _updatableChallenge.value?.description ?: "",
                    expiryDate = _updatableChallenge.value?.expiryDate?.toString() ?: "",
                    pointsValue = _updatableChallenge.value?.pointsValue?.toInt() ?: 0,
                    frequencyCount = _updatableChallenge.value?.frequencyCount?.toInt() ?: 0
                )
                val response = ApiClient.apiService.createFrequencyBasedChallenge("Bearer ${TokenManager.getAccessToken(context)}", gymId, request)
                val code = response.code()
                if (code == 201) {
                    onSuccess()
                } else {
                    onError(response.message() ?: "An error occurred")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred")
            }
        }
    }

    fun updateChallenge(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = UpdateChallengeRequest(
                    name = _updatableChallenge.value?.name,
                    description = _updatableChallenge.value?.description,
                    expiryDate = _updatableChallenge.value?.expiryDate?.toString(),
                    pointsValue = _updatableChallenge.value?.pointsValue?.toInt(),
                    frequencyCount = if (_updatableChallenge.value?.frequencyCount.isNullOrEmpty()) _updatableChallenge.value?.frequencyCount?.toInt() else null,
                    startTimeCriteria = _updatableChallenge.value?.startTimeCriteria?.toString(),
                    endTimeCriteria = _updatableChallenge.value?.endTimeCriteria?.toString(),
                    type = _updatableChallenge.value?.type.toString()
                )
                val challengeId = _updatableChallenge.value?.id ?: ""
                val auth = "Bearer ${TokenManager.getAccessToken(context)}"
                val response = ApiClient.apiService.updateChallenge(auth, challengeId, request)
                val code = response.code()
                if (code == 204) {
                    onSuccess()
                } else {
                    onError(response.message() ?: "An error occurred")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred")
                throw e
            }
        }
    }

    fun deleteChallenge(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val challengeId = _updatableChallenge.value?.id ?: ""
                val auth = "Bearer ${TokenManager.getAccessToken(context)}"
                val response = ApiClient.apiService.deleteChallenge(auth, challengeId)
                val code = response.code()
                if (code == 204) {
                    onSuccess()
                } else {
                    onError(response.message() ?: "An error occurred")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred")
            }
        }
    }
}