package com.example.gym_app.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_app.api.ApiClient
import com.example.gym_app.common.TokenManager
import com.example.gym_app.common.readErrorMessage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.gymapp.library.response.ExceptionResult
import org.gymapp.library.response.GymUserDto
import org.gymapp.library.response.UserDto
import retrofit2.HttpException

class HomeViewModel : ViewModel() {
  private val _gymUserDtos = MutableStateFlow<List<GymUserDto>>(emptyList())
  val gymUserDtos: StateFlow<List<GymUserDto>> = _gymUserDtos

  private val _currentUser = MutableStateFlow<UserDto?>(null)
  val currentUser: StateFlow<UserDto?> = _currentUser

  fun addGym(context: Context, code: String, onSuccess: () -> Unit, onError: (String) -> Unit) =
    viewModelScope.launch {
      try {
        ApiClient.apiService.joinGymAsMember("Bearer ${TokenManager.getAccessToken(context)}", code)
        onSuccess()
      } catch (e: HttpException) {
        val errorMessage = readErrorMessage(e)
        onError(errorMessage)
      }
    }

    fun loadItems(context: Context) =
    viewModelScope.launch {
      val gymUsersDeferred =
        async(Dispatchers.IO) {
          ApiClient.apiService.getUserGyms("Bearer ${TokenManager.getAccessToken(context)}")
        }
      val currentUserDeferred =
        async(Dispatchers.IO) {
          ApiClient.apiService.getCurrentUser("Bearer ${TokenManager.getAccessToken(context)}")
        }

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
