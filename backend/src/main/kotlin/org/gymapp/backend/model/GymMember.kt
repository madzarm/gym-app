package org.gymapp.backend.model

import jakarta.persistence.Entity

@Entity(name = "gym_members")
class GymMember(
    id: String,
    roles: MutableList<Role>,
    gym: Gym,
    user: User,
) : GymUser(id, roles, user, gym) {

}