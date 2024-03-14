package org.gymapp.backend.model

import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
open class GymUser(
    @Id var id: String,
    @ManyToMany
    @JoinTable(
        name = "gym_user_roles",
        joinColumns = [JoinColumn(name = "gym_user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: Set<Role>,
    @ManyToOne var user: User,
    @ManyToOne var gym: Gym
) {

}