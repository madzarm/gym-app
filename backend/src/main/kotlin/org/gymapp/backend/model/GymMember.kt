package org.gymapp.backend.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity(name = "gym_members")
class GymMember (

    @Id
    val id: String? = null,

    @ManyToMany(mappedBy = "participants")
    val classes: MutableList<GymClassInstance> = mutableListOf(),

    @MapsId
    @OneToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "id")
    val gymUser: GymUser,

    val firstJoined: LocalDate = LocalDate.now(),

    val inviteCode: String = generateRandomInviteCode(),

    val customerId: String,

    @OneToMany(mappedBy = "gymMember")
    val visits: MutableList<GymVisit> = mutableListOf(),

    @OneToMany(mappedBy = "member")
    val gymClassReviews: MutableList<GymClassReview> = mutableListOf(),

    @OneToMany(mappedBy = "member")
    val gymTrainerReviews: MutableList<GymTrainerReview> = mutableListOf(),

    @OneToMany(mappedBy = "member")
    val completedChallenges: MutableList<MemberChallenge> = mutableListOf()
) {

    companion object {
        fun generateRandomInviteCode(): String {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            return (1..6)
                .map { chars.random() }
                .joinToString("")
        }
    }

}