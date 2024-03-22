package org.gymapp.backend.model

data class AccessCodeDto (
    val id: String,
    val code: String,
    val expiryDateTime: String,
    val gymId: String,
)