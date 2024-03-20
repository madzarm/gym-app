package org.gymapp.library.response

import java.time.LocalDateTime

data class SecurityUserDto(
    val username: String,
    val password: String,
    val role: String,
    val createdAt: LocalDateTime
)

data class GymDto(
        val id: String?,
        val name: String?,
        val picture: String?,
        val code: String?
    )

data class GymUserDto(
    val id: String?,
    val user: UserDto?,
    val gym: GymDto?,
    val roles: List<String>?,
)

data class UserDto(
    val id: String?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val profilePicUrl: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val gymUsersIds: List<String>?
)

data class ExceptionResult(val message: String, val exception: String)

data class ActionResponse(val message: String, val success: Boolean)