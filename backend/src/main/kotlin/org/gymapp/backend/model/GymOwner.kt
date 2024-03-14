package org.gymapp.backend.model

import jakarta.persistence.Entity

@Entity(name = "gym_owners")
class GymOwner(
    id: String,
    roles: Set<Role>,
    gym: Gym,
    user: User,
) : GymUser(id, roles, user, gym) {

}