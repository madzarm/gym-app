package org.gymapp.backend.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity(name = "gym_members")
class GymMember (
    @Id val id: String? = null,

    @ManyToMany(mappedBy = "participants")
    val classes: MutableList<GymClassInstance> = mutableListOf(),

    @OneToOne @MapsId @JoinColumn(name = "id") val gymUser: GymUser,

    val firstJoined: LocalDate = LocalDate.now(),

    @OneToMany(mappedBy = "gymMember")
    val visits: MutableList<GymVisit> = mutableListOf(),

    @OneToMany(mappedBy = "member")
    val gymClassReviews: MutableList<GymClassReview> = mutableListOf(),

    @OneToMany(mappedBy = "member")
    val gymTrainerReviews: MutableList<GymTrainerReview> = mutableListOf()
) {

}