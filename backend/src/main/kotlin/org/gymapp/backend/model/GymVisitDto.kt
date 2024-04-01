package org.gymapp.backend.model

data class GymVisitDto (
    val id: String,
    val gymId: String,
    val gymMemberId: String,
    val date: String,
    val duration: String? = null,
)