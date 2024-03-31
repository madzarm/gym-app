package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne

@Entity(name = "gym_trainers")
class GymTrainer (
    @Id val id: String? = null,
    @OneToOne @MapsId @JoinColumn(name = "id") val gymUser: GymUser
) {

    companion object {
        fun fromGymUser(gymUser: GymUser): GymTrainer {
            return GymTrainer(
                gymUser = gymUser
            )
        }
    }
}