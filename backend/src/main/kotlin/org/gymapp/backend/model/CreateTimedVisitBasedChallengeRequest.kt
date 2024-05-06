package org.gymapp.backend.model

data class CreateTimedVisitBasedChallengeRequest(
    val name: String,
    val description: String,
    val expiryDate: String,
    val pointsValue: Int,
    val startTime: String,
    val endTime: String
)