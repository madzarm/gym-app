package com.example.gym_app.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.gym_app.common.Role

class UserViewModel : ViewModel() {

    private val _selectedRole = mutableStateOf<Role?>(null)
    val selectedRole
        get() = _selectedRole


    fun setRole(newRole: Role) {
        _selectedRole.value = newRole
    }
}