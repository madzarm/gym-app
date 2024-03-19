package org.gymapp.library.response

import java.time.LocalDateTime

data class SecurityUserDto(
    val username: String,
    val password: String,
    val role: String,
    val createdAt: LocalDateTime
)

data class GymDto(
    val name: String?,
    val picture: String?,
    val code: String?
)

data class UserDto(
    val id: String?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val profilePicUrl: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val gymUsersIds: List<String>?
)