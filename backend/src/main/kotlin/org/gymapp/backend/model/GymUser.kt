package org.gymapp.backend.model

import jakarta.persistence.*

@Entity
open class GymUser(
    @Id var id: String,
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "gym_user_roles",
        joinColumns = [JoinColumn(name = "gym_user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableList<Role>,
    @ManyToOne var user: User,
    @ManyToOne(cascade = [CascadeType.ALL]) var gym: Gym?,
    @OneToOne(mappedBy = "gymUser") var gymOwner: GymOwner? = null,
    @OneToOne(mappedBy = "gymUser") var gymTrainer: GymTrainer? = null,
    @OneToOne(mappedBy = "gymUser") var gymMember: GymMember? = null,
) {

    fun hasRole(roleName: String): Boolean {
        return roles.any { it.name == roleName }
    }

    fun addRole(role: Role) {
        roles.add(role)
    }
}