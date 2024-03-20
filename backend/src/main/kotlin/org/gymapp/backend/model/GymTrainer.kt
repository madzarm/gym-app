package org.gymapp.backend.model

import jakarta.persistence.Entity

@Entity(name = "gym_trainers")
class GymTrainer (
    id: String,
    roles: MutableList<Role>,
    gym: Gym,
    user: User,
) : GymUser(id, roles, user, gym) {

}