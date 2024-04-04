package org.gymapp.backend.model

data class GymClassReviewDto(
    val id: String,
    val review: String,
    val gymClassId: String,
    val memberId: String,
    val date: String,
)