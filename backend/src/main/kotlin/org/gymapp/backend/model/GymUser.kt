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
    var roles: MutableSet<Role>,
    @ManyToOne(cascade = [CascadeType.ALL]) var user: User,
    @ManyToOne(cascade = [CascadeType.ALL]) var gym: Gym?
) {

}