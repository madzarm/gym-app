package org.gymapp.backend.model

import jakarta.persistence.Entity

@Entity(name = "gym_members")
class GymMember(
    id: String,
    roles: MutableSet<Role>,
    gym: Gym,
    user: User,
) : GymUser(id, roles, user, gym) {

}