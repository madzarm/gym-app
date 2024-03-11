package com.example.gym_app.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_app.common.Role
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val role: Role? = null,
)

class UserViewModel : ViewModel() {
  private var _userState = MutableStateFlow(UserState())
  val userState = _userState.asStateFlow()

    private var _error = MutableStateFlow(false)
    val error = _error.asStateFlow()

  private var passwordConfirmationCheckJob: Job? = null

  fun updateUserState(update: UserState.() -> UserState) {
    _userState.value = update(_userState.value)
  }

  fun confirmPasswordDelayed(confirmPassword: String) {
    passwordConfirmationCheckJob?.cancel()
    passwordConfirmationCheckJob =
        viewModelScope.launch {
          updateUserState { copy(confirmPassword = confirmPassword) }
          delay(500)
          val passwordMatch = _userState.value.password == confirmPassword
          _error.value = !passwordMatch
        }
  }

  //  private fun checkPasswordsMatch() {
  //    confirmPasswordError.value =
  //        if (userState.value.password != userState.value.confirmPassword &&
  //            userState.value.confirmPassword.isNotEmpty()) {
  //          "Passwords do not match"
  //        } else {
  //          ""
  //        }
  //  }

  //  fun setConfirmPassword(confirmPassword: String) {
  //    // passwordCheckJob?.cancel()
  //
  //    // passwordCheckJob = CoroutineScope(Dispatchers.Default).launch { delay(500) }
  //
  //    viewModelScope.launch {
  //      println("Launching with pass: ${confirmPassword}...")
  //      delay(500)
  //      updateState(confirmPassword = confirmPassword)
  //    }
  //  }
}
// updateState(confirmPassword = confirmPassword)
// confirmPasswordError.value = if (userState.value.password != confirmPassword &&
// confirmPassword.isNotEmpty()) {
//    "Passwords do not match"
// } else {
//    ""
// }
