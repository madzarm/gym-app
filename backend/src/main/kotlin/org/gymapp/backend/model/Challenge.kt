package org.gymapp.backend.model

import jakarta.persistence.*
import org.gymapp.library.response.ChallengeType
import java.time.LocalDateTime
import java.util.UUID



@Entity
class Challenge (

    @Id
    val id: String = UUID.randomUUID().toString(),

    var name: String,

    var description: String,

    var expiryDate: LocalDateTime,

    var pointsValue: Int,

    var isDeleted: Boolean,

    @Enumerated(EnumType.STRING)
    var type: ChallengeType,

    val createdAt: LocalDateTime,

    @OneToOne(mappedBy = "challenge", cascade = [CascadeType.ALL])
    var criteria: ChallengeCriteria,

    @ManyToOne
    val gym: Gym,

    @OneToMany(mappedBy = "challenge", cascade = [CascadeType.ALL])
    val memberChallenges: List<MemberChallenge> = mutableListOf()
){


}