package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.time.LocalDateTime

@Entity(name = "users")
class User(

    @Id
    var id: String?,

    var email: String?,

    var firstName: String?,

    var lastName: String?,

    var profilePicUrl: String?,

    var createdAt: LocalDateTime?,

    var updatedAt: LocalDateTime?,

    var fcmToken: String? = null,

    @OneToMany(mappedBy = "user")
    val gymUsers: MutableList<GymUser> = mutableListOf()
) {

    fun getGymUser(gymCode: String): GymUser? {
        return gymUsers.find { it.gym?.code == gymCode }
    }

    fun getTrainer(gymId: String): GymTrainer {
        return gymUsers.find { it.gym?.id == gymId }?.gymTrainer ?: throw IllegalArgumentException("User is not a trainer of this gym!")
    }

    fun getMember(gymId: String): GymMember {
        return gymUsers.find { it.gym?.id == gymId }?.gymMember ?: throw IllegalArgumentException("User is not a member of this gym!")
    }
}