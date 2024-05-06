package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import java.time.LocalDateTime

enum class ChallengeType {
    TIMED_VISIT_BASED,
    FREQUENCY_BASED
}

@Entity
class Challenge (
    @Id
    val id: String,

    var name: String,

    var description: String,

    var expiryDate: LocalDateTime,

    var pointsValue: Int,

    var isDeleted: String,

    var type: ChallengeType,

    val createdAt: LocalDateTime,

    @OneToOne(mappedBy = "challenge")
    var criteria: ChallengeCriteria
){


}