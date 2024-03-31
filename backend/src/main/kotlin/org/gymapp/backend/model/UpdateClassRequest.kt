package org.gymapp.backend.model

data class UpdateClassRequest(
    val name: String? = null,
    val description: String? = null,
    val dateTime: String? = null,
    val duration: String? = null,
    val maxParticipants: Int? = null
)