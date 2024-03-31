package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.time.LocalDateTime

@Entity(name = "users")
class User(
    @Id var id: String?,
    var email: String?,
    var firstName: String?,
    var lastName: String?,
    var profilePicUrl: String?,
    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
    @OneToMany(mappedBy = "user") val gymUsers: MutableList<GymUser> = mutableListOf()
) {

    fun getGymUser(gymCode: String): GymUser? {
        return gymUsers.find { it.gym?.code == gymCode }
    }
}