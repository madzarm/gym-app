package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToOne

enum class CriteriaType {
    TIMED_VISIT_BASED,
    FREQUENCY_BASED
}

@Entity
class ChallengeCriteria (
    @Id
    val id: String,

    var type: CriteriaType,

    @OneToOne(mappedBy = "criteria")
    val challenge: Challenge,
)