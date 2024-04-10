package org.gymapp.backend.model

import jakarta.persistence.*

@Entity(name = "gym_trainers")
class GymTrainer (
    @Id val id: String? = null,
    @OneToOne @MapsId @JoinColumn(name = "id") val gymUser: GymUser,
    @OneToMany(mappedBy = "trainer") var classes: MutableList<GymClass> = mutableListOf()
) {

    companion object {
        fun fromGymUser(gymUser: GymUser): GymTrainer {
            val trainer =  GymTrainer(
                gymUser = gymUser
            )
            gymUser.gymTrainer = trainer
            return trainer
        }
    }
}