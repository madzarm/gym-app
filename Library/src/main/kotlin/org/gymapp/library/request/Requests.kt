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