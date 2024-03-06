package org.gymapp.library.request

data class CreateAccountRequest(
    val username: String,
    val password: String,
    val role: String
)
