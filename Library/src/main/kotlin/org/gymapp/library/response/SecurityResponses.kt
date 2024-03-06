package org.gymapp.library.response

import java.time.LocalDateTime

data class SecurityUserDto(
    val username: String,
    val password: String,
    val role: String,
    val createdAt: LocalDateTime
)