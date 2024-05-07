package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class MemberChallenge (

    @Id
    val id: String,

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id", unique = false)
    val member: GymMember,

    @ManyToOne
    @JoinColumn(name = "challenge_id", referencedColumnName = "id", unique = false)
    val challenge: Challenge,

    var dateCompleted: LocalDateTime,

    var isClaimed: Boolean,
) {
}