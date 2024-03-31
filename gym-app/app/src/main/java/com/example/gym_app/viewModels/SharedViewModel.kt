package com.example.gym_app.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gym_app.api.ApiClient
import com.example.gym_app.common.TokenManager
import kotlinx.coroutines.launch
import org.gymapp.library.response.AccessCodeDto
import org.gymapp.library.response.GymClassDto
import org.gymapp.library.response.GymUserDto

class SharedViewModel : ViewModel() {
    private val _selectedGymUser = MutableLiveData<GymUserDto>()
    val selectedGymUser: LiveData<GymUserDto> = _selectedGymUser

    private val _accessCode = MutableLiveData<AccessCodeDto>()
    val accessCode: LiveData<AccessCodeDto> = _accessCode

    private val _trainerGymClasses = MutableLiveData<List<GymClassDto>>()
    val trainerGymClasses: LiveData<List<GymClassDto>> = _trainerGymClasses

    fun selectGym(gymUserDto: GymUserDto) {
        _selectedGymUser.value = gymUserDto
    }

    fun generateAccessCode(context: Context) =
        viewModelScope.launch {
            val accessCodeDto: AccessCodeDto = ApiClient.apiService.generateAccessCode("Bearer ${TokenManager.getAccessToken(context)}", _selectedGymUser.value?.gym?.id ?: "")
            _accessCode.value = accessCodeDto
        }

    fun getTrainerGymClasses(context: Context) =
        viewModelScope.launch {
            val gymClasses: List<GymClassDto> = ApiClient.apiService.getTrainer("Bearer ${TokenManager.getAccessToken(context)}", _selectedGymUser.value?.gym?.id ?: "").gymClasses
            _trainerGymClasses.value = gymClasses
        }
}

class SharedViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(TokenManager.getAccessToken(context) ?: "") as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}