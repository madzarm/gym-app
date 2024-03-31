package org.gymapp.backend.model

import jakarta.persistence.*

@Entity(name = "gym_members")
class GymMember (
    @Id val id: String? = null,
    @OneToOne @MapsId @JoinColumn(name = "id") val gymUser: GymUser,
) {

}