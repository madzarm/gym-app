package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToOne

@Entity
class FrequencyBasedCriteria (

    @Id
    val id: String,

    var frequencyCount: Int,

    @OneToOne
    val challenge: ChallengeCriteria,
) {
}