package com.example.gym_app.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.gymapp.library.response.GymClassDto

class GymClassViewModel: ViewModel() {
    private val _selectedGymClass = MutableLiveData<GymClassDto>()
    val selectedGymClass: MutableLiveData<GymClassDto> = _selectedGymClass

    private val _updatedGymClass = MutableLiveData<GymClassDto>()
    val updatedGymClass: MutableLiveData<GymClassDto> = _updatedGymClass

    fun setSelectedGymClass(gymClass: GymClassDto) {
        _selectedGymClass.value = gymClass
        _updatedGymClass.value = gymClass
    }

    fun updateGymClass(update: GymClassDto.() -> GymClassDto) {
        _updatedGymClass.value = update(_updatedGymClass.value!!)
    }

    fun updateGymClass() {
        _selectedGymClass.value = _updatedGymClass.value
    }
}