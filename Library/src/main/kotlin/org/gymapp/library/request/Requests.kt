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
    var picture: String?
) 

class CreateClassRequest {
    val name: String = ""
    val description: String = ""
    val dateTime: String = ""
    val duration: String = ""
    val maxParticipants: Int = 0
}

data class CreateRecurringClassRequest(
    val name: String,
    val description: String,
    val dateTime: String,
    val duration: String,
    val maxParticipants: Int,
    val maxNumOfOccurrences: Int,
    val daysOfWeek: List<Int>,
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
    var classId: String = "",
    var memberId: String = ""
)

data class ReviewTrainerRequest (
    var review: String = "",
    var rating: Int = 0,
    var trainerId: String = "",
    var memberId: String = ""
)