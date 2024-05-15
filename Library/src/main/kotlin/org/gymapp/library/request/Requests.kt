package org.gymapp.library.request

data class CreateUserRequest(
    var id: String?,
    var firstName: String?,
    var lastName: String?,
    var email: String?,
    var profilePicUrl: String?
)

data class CreateGymRequest(
    var name: String,
    var picture: String? = null,
    var subscriptionFee: String? = null,
) 

class CreateClassRequest {
    val name: String = ""
    val description: String = ""
    val dateTime: String = ""
    val duration: String = ""
    val maxParticipants: Int = 0
}

data class CreateRecurringClassRequest(
    val name: String? = null,
    val description: String? = null,
    val dateTime: String? = null,
    val duration: String? = null,
    val maxParticipants: Int? = null,
    val maxNumOfOccurrences: Int? = null,
    val daysOfWeek: List<Int>? = mutableListOf(),
)

data class UpdateClassRequest(
    val name: String? = null,
    val description: String? = null,
    val dateTime: String? = null,
    val duration: String? = null,
    val maxParticipants: Int? = null
)

data class UpdateGymClassInstanceRequest(
    val description: String? = null,
    val originalDateTime: String,
    val dateTime: String? = null,
    val duration: String? = null,
    val maxParticipants: Int? = null,
    val isCanceled: Boolean? = false,
)

data class ReviewGymClassRequest (
    var review: String = "",
    var rating: Int = 0,
    var gymClassInstanceId: String = "",
    var memberId: String = ""
)

data class ReviewTrainerRequest (
    var review: String = "",
    var rating: Int = 0,
    var trainerId: String = "",
    var memberId: String = ""
)

data class CreateFrequencyBasedChallengeRequest(
    val name: String,
    val description: String,
    val expiryDate: String,
    val pointsValue: Int,
    val frequencyCount: Int
)

data class CreateInviteFriendChallengeRequest(
    val name: String,
    val description: String,
    val expiryDate: String,
    val pointsValue: Int,
)

data class CreateTimedVisitBasedChallengeRequest(
    val name: String,
    val description: String,
    val expiryDate: String,
    val pointsValue: Int,
    val startTime: String,
    val endTime: String
)

data class UpdateChallengeRequest(
    var name: String? = null,
    var description: String? = null,
    var expiryDate: String? = null,
    var pointsValue: Int? = null,
    var type: String,
    var frequencyCount: Int? = null,
    var startTimeCriteria: String? = null,
    var endTimeCriteria: String? = null 
)