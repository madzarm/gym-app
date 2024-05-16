package com.example.gym_app.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_app.api.ApiClient
import com.example.gym_app.common.TokenManager
import java.time.LocalDateTime
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.gymapp.library.request.CreateFrequencyBasedChallengeRequest
import org.gymapp.library.request.CreateInviteFriendChallengeRequest
import org.gymapp.library.request.CreateTimedVisitBasedChallengeRequest
import org.gymapp.library.request.UpdateChallengeRequest
import org.gymapp.library.response.AccessCodeDto
import org.gymapp.library.response.ChallengeDto
import org.gymapp.library.response.ChallengeType
import org.gymapp.library.response.GymClassDto
import org.gymapp.library.response.GymClassInstanceDto
import org.gymapp.library.response.GymUserDto
import org.gymapp.library.response.PaymentSheetResponse

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

data class SubscriptionStatus(val subscribed: Boolean, val loading: Boolean)

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

  private val _points = MutableLiveData<Int>()
  val points: LiveData<Int> = _points

  private val _unclaimedChallenges = MutableLiveData<List<ChallengeDto>>()
  val unclaimedChallenges: LiveData<List<ChallengeDto>> = _unclaimedChallenges

  private val _selectedChallenge = MutableLiveData<ChallengeDto>()
  val selectedChallenge: LiveData<ChallengeDto> = _selectedChallenge

  private val _paymentSheet = MutableLiveData<PaymentSheetResponse>()
  val paymentSheet: LiveData<PaymentSheetResponse> = _paymentSheet

  private val _setupPaymentSheet = MutableLiveData<PaymentSheetResponse>()
  val setupPaymentSheet: LiveData<PaymentSheetResponse> = _setupPaymentSheet

  private val _stripeAccCompleted = MutableLiveData<Boolean>()
  val stripeAccCompleted: LiveData<Boolean> = _stripeAccCompleted

  private val _isSetupCompleted = MutableLiveData<Boolean>(false)
  val isSetupCompleted: LiveData<Boolean> = _isSetupCompleted

  private val _subscriptionStatus =
    MutableLiveData<SubscriptionStatus>(SubscriptionStatus(subscribed = false, loading = true))
  val subscriptionStatus: LiveData<SubscriptionStatus> = _subscriptionStatus

  fun selectGym(gymUserDto: GymUserDto) {
    _selectedGymUser.value = gymUserDto
  }

  fun setSelectedChallenge(challenge: ChallengeDto) {
    _selectedChallenge.value = challenge
  }

  fun updateSelectedChallenge(update: UpdatableChallenge.() -> UpdatableChallenge) {
    _updatableChallenge.value = update(_updatableChallenge.value ?: UpdatableChallenge())
  }

  fun getLiveStatus(context: Context) =
    viewModelScope.launch {
      val liveStatus: Int =
        ApiClient.apiService.getLiveStatus(
          "Bearer ${TokenManager.getAccessToken(context)}",
          _selectedGymUser.value?.gym?.id ?: "",
        )
      _liveStatus.value = liveStatus
    }

  fun generateAccessCode(context: Context) =
    viewModelScope.launch {
      val accessCodeDto: AccessCodeDto =
        ApiClient.apiService.generateAccessCode(
          "Bearer ${TokenManager.getAccessToken(context)}",
          _selectedGymUser.value?.gym?.id ?: "",
        )
      _accessCode.value = accessCodeDto
    }

  fun getTrainerGymClasses(context: Context) =
    viewModelScope.launch {
      val gymClasses: List<GymClassDto> =
        ApiClient.apiService
          .getTrainerWithUpcomingClasses(
            "Bearer ${TokenManager.getAccessToken(context)}",
            _selectedGymUser.value?.gym?.id ?: "",
          )
          .gymClasses
      _gymClasses.value = gymClasses
    }

  fun getUpcomingGymClasses(context: Context, gymId: String) {
    viewModelScope.launch {
      val gymClasses: List<GymClassDto> =
        ApiClient.apiService.getUpcomingGymClasses(
          "Bearer ${TokenManager.getAccessToken(context)}",
          gymId,
        )
      _gymClasses.value = gymClasses
    }
  }

  fun getGymClassesForReview(context: Context, gymId: String) {
    viewModelScope.launch {
      val gymClasses: List<GymClassInstanceDto> =
        ApiClient.apiService.getGymClassesForReview(
          "Bearer ${TokenManager.getAccessToken(context)}",
          gymId,
        )
      _gymClassesForReview.value = gymClasses
    }
  }

  fun fetchActiveChallenges(
    context: Context,
    gymId: String,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit,
  ) {
    viewModelScope.launch {
      try {
        val challengeDtos: List<ChallengeDto> =
          ApiClient.apiService.fetchActiveChallenges(
            "Bearer ${TokenManager.getAccessToken(context)}",
            gymId,
          )
        _challengeDtos.value = challengeDtos
        onSuccess()
      } catch (e: Exception) {
        onError(e.message ?: "An error occurred")
      }
    }
  }

  fun fetchPoints(
    context: Context,
    gymId: String,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit,
  ) {
    viewModelScope.launch {
      try {
        val points =
          ApiClient.apiService.fetchPoints("Bearer ${TokenManager.getAccessToken(context)}", gymId)
        _points.value = points
        onSuccess()
      } catch (e: Exception) {
        onError(e.message ?: "An error occurred")
      }
    }
  }

  fun fetchUnclaimedChallenges(
    context: Context,
    gymId: String,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {},
  ) {
    viewModelScope.launch {
      try {
        val auth = "Bearer ${TokenManager.getAccessToken(context)}"
        val challengeDtos: List<ChallengeDto> =
          ApiClient.apiService.fetchUnclaimedChallenges(auth, gymId)
        _unclaimedChallenges.value = challengeDtos
        onSuccess()
      } catch (e: Exception) {
        onError(e.message ?: "An error occurred")
      }
    }
  }

  fun createChallenge(context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val gymId = _selectedGymUser.value?.gym?.id ?: ""
    if (_updatableChallenge.value?.type == ChallengeType.TIMED_VISIT_BASED) {
      createTimeBasedChallenge(context, gymId, onSuccess, onError)
    } else if (_updatableChallenge.value?.type == ChallengeType.FREQUENCY_BASED) {
      createFrequencyBasedChallenge(context, gymId, onSuccess, onError)
    } else if (_updatableChallenge.value?.type == ChallengeType.INVITE_BASED) {
      createInviteFriendChallenge(context, gymId, onSuccess, onError)
    } else {
      onError("Invalid challenge type")
    }
  }

  fun createInviteFriendChallenge(
    context: Context,
    gymId: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) {
    viewModelScope.launch {
      try {
        val request =
          CreateInviteFriendChallengeRequest(
            name = _updatableChallenge.value?.name ?: "",
            description = _updatableChallenge.value?.description ?: "",
            expiryDate = _updatableChallenge.value?.expiryDate?.toString() ?: "",
            pointsValue = _updatableChallenge.value?.pointsValue?.toInt() ?: 0,
          )
        val response =
          ApiClient.apiService.createInviteFriendChallenge(
            "Bearer ${TokenManager.getAccessToken(context)}",
            gymId,
            request,
          )
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

  fun createTimeBasedChallenge(
    context: Context,
    gymId: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) {
    viewModelScope.launch {
      try {
        val request =
          CreateTimedVisitBasedChallengeRequest(
            name = _updatableChallenge.value?.name ?: "",
            description = _updatableChallenge.value?.description ?: "",
            expiryDate = _updatableChallenge.value?.expiryDate?.toString() ?: "",
            pointsValue = _updatableChallenge.value?.pointsValue?.toInt() ?: 0,
            startTime = _updatableChallenge.value?.startTimeCriteria?.toString() ?: "",
            endTime = _updatableChallenge.value?.endTimeCriteria?.toString() ?: "",
          )
        val response =
          ApiClient.apiService.createTimedBasedChallenge(
            "Bearer ${TokenManager.getAccessToken(context)}",
            gymId,
            request,
          )
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
    onError: (String) -> Unit,
  ) {
    viewModelScope.launch {
      try {
        val request =
          CreateFrequencyBasedChallengeRequest(
            name = _updatableChallenge.value?.name ?: "",
            description = _updatableChallenge.value?.description ?: "",
            expiryDate = _updatableChallenge.value?.expiryDate?.toString() ?: "",
            pointsValue = _updatableChallenge.value?.pointsValue?.toInt() ?: 0,
            frequencyCount = _updatableChallenge.value?.frequencyCount?.toInt() ?: 0,
          )
        val response =
          ApiClient.apiService.createFrequencyBasedChallenge(
            "Bearer ${TokenManager.getAccessToken(context)}",
            gymId,
            request,
          )
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

  fun updateChallenge(context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
    viewModelScope.launch {
      try {
        val request =
          UpdateChallengeRequest(
            name = _updatableChallenge.value?.name,
            description = _updatableChallenge.value?.description,
            expiryDate = _updatableChallenge.value?.expiryDate?.toString(),
            pointsValue = _updatableChallenge.value?.pointsValue?.toInt(),
            frequencyCount =
              if (_updatableChallenge.value?.frequencyCount.isNullOrEmpty())
                _updatableChallenge.value?.frequencyCount?.toInt()
              else null,
            startTimeCriteria = _updatableChallenge.value?.startTimeCriteria?.toString(),
            endTimeCriteria = _updatableChallenge.value?.endTimeCriteria?.toString(),
            type = _updatableChallenge.value?.type.toString(),
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

  fun deleteChallenge(context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
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

  fun claimChallenge(context: Context, challenge: ChallengeDto) {
    viewModelScope.launch {
      delay(3500)
      val updatedUnclaimedChallenges = _unclaimedChallenges.value?.toMutableList()
      updatedUnclaimedChallenges?.remove(challenge)
      _unclaimedChallenges.value = updatedUnclaimedChallenges ?: emptyList()

      val challengeId = challenge.id
      val auth = "Bearer ${TokenManager.getAccessToken(context)}"
      val response = ApiClient.apiService.claimChallenge(auth, challengeId)
      _points.value = (_points.value ?: 0) + challenge.pointsValue
    }
  }

  fun getPaymentSheet(context: Context) {
    println("Getting payment sheet")
    val gymId = _selectedGymUser.value?.gym?.id ?: ""
    viewModelScope.launch {
      val paymentSheetResponse =
        ApiClient.apiService.getPaymentSheet(
          "Bearer ${TokenManager.getAccessToken(context)}",
          gymId,
        )
      println("Payment sheet response: $paymentSheetResponse")

      _paymentSheet.value = paymentSheetResponse
    }
  }

  fun getSetupIntent(context: Context) {
    viewModelScope.launch {
      val gymId = _selectedGymUser.value?.gym?.id ?: ""
      val response =
        ApiClient.apiService.createSetupIntent(
          "Bearer ${TokenManager.getAccessToken(context)}",
          gymId,
        )

      _setupPaymentSheet.value = response
    }
  }

  fun confirmSetupIntent(context: Context) {
    viewModelScope.launch {
      val setupIntentId = _setupPaymentSheet.value?.paymentIntent ?: ""
      val gymId = _selectedGymUser.value?.gym?.id ?: ""
      val response =
        ApiClient.apiService.confirmSetupIntent(
          "Bearer ${TokenManager.getAccessToken(context)}",
          gymId,
          setupIntentId,
        )
      _isSetupCompleted.value = response.isSuccessful
    }
  }

  fun getSubscriptionStatus(context: Context) {
    viewModelScope.launch {
      val gymId = _selectedGymUser.value?.gym?.id ?: ""
      val response =
        ApiClient.apiService.getSubscriptionStatus(
          "Bearer ${TokenManager.getAccessToken(context)}",
          gymId,
        )
      _subscriptionStatus.value = SubscriptionStatus(response.subscribed, false)
    }
  }

  suspend fun isStripeConnectAccountCompleted(context: Context): Boolean {
    val gymId = _selectedGymUser.value?.gym?.id ?: ""
    return withContext(Dispatchers.IO) {
      val response =
        ApiClient.apiService.isStripeConnectAccountCompleted(
          "Bearer ${TokenManager.getAccessToken(context)}",
          gymId,
        )
      response.accountCompleted
    }
  }

  //    fun createAccountLink(
  //        context: Context,
  //        gymId: String,
  //        returnUri: String,
  //        refreshUri: String,
  //        onSuccess: () -> Unit,
  //        onError: (String) -> Unit
  //    ) {
  //        viewModelScope.launch {
  //            try {
  //                val auth = "Bearer ${TokenManager.getAccessToken(context)}"
  //                val response = ApiClient.apiService.createAccountLink(auth, gymId, returnUri,
  // refreshUri)
  //                val code = response.code()
  //                if (code == 200) {
  //                    onSuccess()
  //                } else {
  //                    onError(response.message() ?: "An error occurred")
  //                }
  //            } catch (e: Exception) {
  //                onError(e.message ?: "An error occurred")
  //            }
  //        }
  //    }
}
