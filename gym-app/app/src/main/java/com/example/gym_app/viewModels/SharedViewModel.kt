package com.example.gym_app.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_app.api.ApiClient
import com.example.gym_app.common.TokenManager
import kotlinx.coroutines.launch
import org.gymapp.library.response.AccessCodeDto
import org.gymapp.library.response.GymUserDto

class SharedViewModel : ViewModel() {
    private val _selectedGymUser = MutableLiveData<GymUserDto>()
    val selectedGymUser: LiveData<GymUserDto> = _selectedGymUser

    private val _accessCode = MutableLiveData<AccessCodeDto>()
    val accessCode: LiveData<AccessCodeDto> = _accessCode

    fun selectGym(gymUserDto: GymUserDto) {
        _selectedGymUser.value = gymUserDto
    }

    fun generateAccessCode(context: Context) =
        viewModelScope.launch {
            val accessCodeDto: AccessCodeDto = ApiClient.apiService.generateAccessCode("Bearer ${TokenManager.getAccessToken(context)}", _selectedGymUser.value?.gym?.id ?: "")
            _accessCode.value = accessCodeDto
        }
}