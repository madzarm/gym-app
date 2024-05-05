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
    val isRecurring: Boolean,
    val trainerId: String,
    val instances: List<GymClassInstanceDto>,
    val recurringPattern: RecurringPatternDto?,
)

data class RecurringPatternDto (
    val id: String, 
    val maxNumOfOccurrences: Int? = null,
    val dayOfWeeks: List<Int> = mutableListOf(),
) 

data class GymClassInstanceDto (
    val id: String,
    val classId: String,
    val name: String,
    var description: String,
    var dateTime: String,
    var duration: String,
    var maxParticipants: String,
    var participantsIds: List<String>,
    var trainerId: String,
    var gymClassModifiedInstance: GymClassModifiedInstanceDto?
)

data class GymClassModifiedInstanceDto(
    val id: String,
    var description: String,
    var dateTime: String,
    var duration: String,
    var maxParticipants: String,
    var isCanceled: Boolean,
    var trainerId: String,
)

data class GymMemberDto (
    val id: String?,
    val gymClasses: List<GymClassDto>?
) 

    data class GymOwnerDto(
    val id: String?,
)

data class GymTrainerDto(
    val id: String,
    val gymClasses: List<GymClassDto> = mutableListOf()
)

data class GymTrainerWithReviewsDto(
    val id: String, 
    val user: UserDto,
    val reviews: List<GymTrainerReviewDto> = mutableListOf()
)

data class GymVisitDto (
    val id: String,
    val gymId: String,
    val gymMemberId: String,
    val date: String,
    val duration: String? = null,
)

data class GymClassReviewDto(
    val id: String,
    val review: String,
    val rating: Int,
    val gymClassId: String,
    val memberId: String,
    val date: String,
)

data class GymTrainerReviewDto (
    val id: String,
    val review: String,
    val rating: Int,
    val trainerId: String,
    val memberId: String,
    val date: String,
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