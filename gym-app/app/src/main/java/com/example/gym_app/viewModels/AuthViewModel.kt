package com.example.gym_app.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.gymapp.library.request.CreateUserRequest

class AuthViewModel : ViewModel() {

    private val _accessToken = mutableStateOf("")
    val accessToken = _accessToken

    private val _userState = mutableStateOf<CreateUserRequest?>(null)
    val userState = _userState

    fun setToken(token: String) {
        _accessToken.value = token
    }

    fun createUser(state: CreateUserRequest) {
        _userState.value = state
    }
}