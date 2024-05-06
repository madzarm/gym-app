package org.gymapp.backend.model

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
