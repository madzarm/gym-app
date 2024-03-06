package org.gymapp.library.request

data class CreateAccountRequest(
    val email: String,
    val password: String,
    val role: String
)
