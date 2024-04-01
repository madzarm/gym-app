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

    private val _gymClasses = MutableLiveData<List<GymClassDto>>()
    val gymClasses: LiveData<List<GymClassDto>> = _gymClasses

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
            _gymClasses.value = gymClasses
        }

    fun getGymClasses(context: Context, gymId: String) {
        viewModelScope.launch {
            val gymClasses: List<GymClassDto> = ApiClient.apiService.getGymClasses("Bearer ${TokenManager.getAccessToken(context)}", gymId)
            _gymClasses.value = gymClasses
        }
    }
}