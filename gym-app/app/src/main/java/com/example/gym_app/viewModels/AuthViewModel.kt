package com.example.gym_app.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    private val _token = mutableStateOf("")
    val token = _token

    fun setToken(token: String) {
        _token.value = token
    }
}