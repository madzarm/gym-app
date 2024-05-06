package org.gymapp.backend.model

import jakarta.persistence.*
import java.util.UUID

enum class CriteriaType {
    TIMED_VISIT_BASED,
    FREQUENCY_BASED
}

@Entity
class ChallengeCriteria (
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Enumerated(EnumType.STRING)
    var type: CriteriaType,

    @OneToOne
    var challenge: Challenge? = null,
)