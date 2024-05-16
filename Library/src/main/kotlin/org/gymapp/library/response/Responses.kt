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
    val isDeleted: Boolean?,
)

data class RecurringPatternDto (
    val id: String, 
    val maxNumOfOccurrences: Int? = null,
    val dayOfWeeks: List<Int> = mutableListOf(),
) 

data class GymMemberDtoFull (
    val user: UserDto?,
    val classes: List<GymClassInstanceDto>? = mutableListOf(),
    val firstJoined: String?,
    val visits: List<GymVisitDto>? = mutableListOf()
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
    val firstName: String?,
    val lastName: String?,
    val inviteCode: String?,
    val gymClasses: List<GymClassInstanceDto>?
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
    val gymClassInstanceId: String,
    val member: GymMemberDto,
    val date: String,
)

data class GymClassWithReviewsDto(
    val gymClass: GymClassDto?,
    val reviews: List<GymClassReviewDto>?,
)

data class GymTrainerReviewDto (
    val id: String,
    val review: String,
    val rating: Int,
    val trainerId: String,
    val member: GymMemberDto,
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

data class ChallengeDto(
    var id: String,
    var name: String,
    var description: String,
    var expiryDate: String,
    var pointsValue: Int,
    var createdAt: String,
    var isDeleted: Boolean,
    var type: String,
    var gymId: String,
    var criteriaDto: CriteriaDto?
)

data class CriteriaDto (
    var criteriaId: String,
    var type: String,
    var frequencyCount: Int?,
    var startTimeCriteria: String?,
    var endTimeCriteria: String?
)

enum class ChallengeType {
    TIMED_VISIT_BASED,
    FREQUENCY_BASED,
    INVITE_BASED
}

enum class CriteriaType {
    TIMED_VISIT_BASED,
    FREQUENCY_BASED
}

data class VisitCountByDay (
    var dayOfWeek: Int,
    var hours: List<VisitCountByHour>
)

data class VisitCountByHour (
    var hour: Int,
    var visitCount: Long
)

data class PaymentSheetResponse(
    val paymentIntent: String,
    val ephemeralKey: String,
    val customer: String,
    val publishableKey: String,
    val stripeAccountId: String,
)

data class AccountLinkDto (
    val accountLinkUrl: String
)

data class AccountCompletedDto (
    val accountCompleted: Boolean
)

data class SubscriptionStatusDto (
    val subscribed: Boolean
)