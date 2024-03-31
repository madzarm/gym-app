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
    val gymOwner: GymOwnerDto?,
    val gymMember: GymMemberDto?,
    val gymTrainer: GymTrainerDto?
)

data class GymClassDto(
    val id: String?,
    val name: String?,
    val description: String?,
    val dateTime: String?,
    val duration: String?,
    val maxParticipants: String?,
    val participants: List<GymMemberDto>?
)

data class GymMemberDto (
    val id: String?,
) 

    data class GymOwnerDto(
    val id: String?,
)

data class GymTrainerDto(
    val id: String?,
    val gymClasses: List<GymClassDto>?
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

data class AccessCodeDto (
    val id: String,
    val code: String,
    val expiryDateTime: String,
    val gymId: String,
)