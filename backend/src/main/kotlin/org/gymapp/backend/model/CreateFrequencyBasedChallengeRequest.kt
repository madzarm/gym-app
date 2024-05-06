package org.gymapp.backend.model

data class CreateFrequencyBasedChallengeRequest(
    val name: String,
    val description: String,
    val expiryDate: String,
    val pointsValue: Int,
    val frequencyCount: Int
)