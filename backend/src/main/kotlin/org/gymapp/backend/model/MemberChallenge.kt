package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import java.time.LocalDateTime

@Entity
class MemberChallenge (

    @Id
    val id: String,

    @OneToOne
    val member: GymMember,

    @OneToOne
    val challenge: Challenge,

    var dateCompleted: LocalDateTime,

    var isClaimed: Boolean,
) {
}