package com.example.gym_app.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.gymapp.library.request.CreateUserRequest

class AuthViewModel : ViewModel() {

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    fun setLoggedIn(loggedIn: Boolean) {
        _isLoggedIn.value = loggedIn
    }

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