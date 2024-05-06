package org.gymapp.backend.model

data class CriteriaDto (
    var criteriaId: String,
    var type: String,
    var frequencyCount: Int?,
    var startTimeCriteria: String?,
    var endTimeCriteria: String?
)