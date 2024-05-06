package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import java.time.LocalTime

@Entity
class TimeBasedCriteria (
    @Id
    val id: String,

    var startTime: LocalTime?,

    var endTime: LocalTime?,

    @OneToOne
    val challenge: ChallengeCriteria,
) {

}
