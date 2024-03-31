package com.example.gym_app.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.gymapp.library.response.GymClassDto

class GymClassViewModel: ViewModel() {
    private val _selectedGymClass = MutableLiveData<GymClassDto>()
    val selectedGymClass: MutableLiveData<GymClassDto> = _selectedGymClass

    fun setSelectedGymClass(gymClass: GymClassDto) {
        _selectedGymClass.value = gymClass
    }
}