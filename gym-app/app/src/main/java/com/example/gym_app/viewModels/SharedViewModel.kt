package com.example.gym_app.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.gymapp.library.response.GymUserDto

class SharedViewModel : ViewModel() {
    private val _selectedGym = MutableLiveData<GymUserDto>()
    val selectedGym: LiveData<GymUserDto> = _selectedGym

    fun selectGym(gymUserDto: GymUserDto) {
        _selectedGym.value = gymUserDto
    }
}