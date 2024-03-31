package com.example.gym_app.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gym_app.api.ApiClient
import com.example.gym_app.common.TokenManager
import com.example.gym_app.common.readErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.gymapp.library.request.CreateGymRequest
import org.gymapp.library.response.GymUserDto
import org.gymapp.library.response.UserDto
import retrofit2.HttpException

class HomeViewModel(private val accessToken: String) : ViewModel() {
  private val _gymUserDtos = MutableStateFlow<List<GymUserDto>>(emptyList())
  val gymUserDtos: StateFlow<List<GymUserDto>> = _gymUserDtos

  private val _currentUser = MutableStateFlow<UserDto?>(null)
  val currentUser: StateFlow<UserDto?> = _currentUser

  init {
    loadItems(accessToken)
  }

  fun joinGymAsTrainer(
    context: Context,
    code: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) =
    viewModelScope.launch {
      try {
        val gymUserDto: GymUserDto =
          ApiClient.apiService.joinGymAsTrainer(
            "Bearer ${TokenManager.getAccessToken(context)}",
            code,
          )
        val existingList = _gymUserDtos.value

        val existingIndex = existingList.indexOfFirst { it.id == gymUserDto.id }

        val updatedList =
          if (existingIndex != -1) {
            existingList.toMutableList().apply { this[existingIndex] = gymUserDto }
          } else {
            existingList.toMutableList().apply { add(gymUserDto) }
          }

        _gymUserDtos.value = updatedList
        onSuccess()
      } catch (e: HttpException) {
        val errorMessage = readErrorMessage(e)
        onError(errorMessage)
      }
    }

  fun joinGymAsMember(
    context: Context,
    code: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) =
    viewModelScope.launch {
      try {
        val gymUserDto: GymUserDto =
          ApiClient.apiService.joinGymAsMember(
            "Bearer ${TokenManager.getAccessToken(context)}",
            code,
          )
        val existingList = _gymUserDtos.value ?: listOf()

        val existingIndex = existingList.indexOfFirst { it.id == gymUserDto.id }

        val updatedList =
          if (existingIndex != -1) {
            existingList.toMutableList().apply { this[existingIndex] = gymUserDto }
          } else {
            existingList.toMutableList().apply { add(gymUserDto) }
          }

        _gymUserDtos.value = updatedList
        onSuccess()
      } catch (e: HttpException) {
        val errorMessage = readErrorMessage(e)
        onError(errorMessage)
      }
    }

  fun createGym(
    context: Context,
    name: String,
    imageBase64: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) =
    viewModelScope.launch {
      try {
        val gymUserDto: GymUserDto =
          ApiClient.apiService.createGym(
            "Bearer ${TokenManager.getAccessToken(context)}",
            CreateGymRequest(name, imageBase64),
          )
        val updatedList = _gymUserDtos.value.toMutableList().apply { add(gymUserDto) }
        _gymUserDtos.value = updatedList
        onSuccess.invoke()
      } catch (e: HttpException) {
        val errorMessage = readErrorMessage(e)
        onError(errorMessage)
      }
    }

  fun loadItems(accessToken: String) =
    viewModelScope.launch {
      val gymUsersDeferred =
        async(Dispatchers.IO) { ApiClient.apiService.getUserGyms("Bearer ${accessToken}") }
      val currentUserDeferred =
        async(Dispatchers.IO) { ApiClient.apiService.getCurrentUser("Bearer ${accessToken}") }

      launch {
        try {
          val fetchedItems = gymUsersDeferred.await()
          _gymUserDtos.value = fetchedItems.body() ?: emptyList()
        } catch (e: Exception) {
          println("Error fetching gym user DTOs: ${e.message}")
        }
      }

      launch {
        try {
          val currentUser = currentUserDeferred.await()
          _currentUser.value = currentUser.body()
        } catch (e: Exception) {
          println("Error fetching current user: ${e.message}")
        }
      }
    }
}

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return HomeViewModel(TokenManager.getAccessToken(context) ?: "") as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
