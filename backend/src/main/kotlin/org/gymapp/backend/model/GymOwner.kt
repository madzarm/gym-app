package org.gymapp.backend.model

import jakarta.persistence.*

@Entity(name = "gym_owners")
class GymOwner (

    @Id
    val id: String? = null,

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    val gymUser: GymUser,
) {
    companion object {
        fun fromGymUser(gymUser: GymUser): GymOwner {
            val gymOwner = GymOwner(gymUser = gymUser)
            gymUser.gymOwner = gymOwner
            return gymOwner
        }
    }

}