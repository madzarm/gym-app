package org.gymapp.backend.service

import jakarta.transaction.Transactional
import org.gymapp.backend.common.Common
import org.gymapp.backend.common.Roles
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.GymRepository
import org.gymapp.backend.repository.RoleRepository
import org.gymapp.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.util.*

data class CreateGymRequest(
    val name: String,
    val picture: String
) {}

data class GymDto(
    val name: String,
    val picture: String
){}

@Service
class GymService(
    @Autowired private val gymRepository: GymRepository,
    @Autowired private val roleRepository: RoleRepository,
    @Autowired private val gymUserRepository: GymUserRepository, private val userRepository: UserRepository
) {

    @Transactional
    fun createGym(request: CreateGymRequest, jwt: Jwt): GymDto {
        val userId = Common.extractId(jwt)
        val gym = Gym(
            UUID.randomUUID().toString(),
            request.name,
            Common.generateRandomGymCode(),
            request.picture,
            mutableListOf(),
            null
        )

        val user: User = userRepository.findById(userId).get()

        val role = roleRepository.findByName(Roles.ROLE_ADMIN.name).get()
        val gymUser = GymUser(
            id = UUID.randomUUID().toString(),
            roles = mutableSetOf(role),
            user = user,
            gym = gym
        )
        gym.owner = gymUser
        gymUserRepository.save(gymUser)

        return GymDto(name = gym.name, picture = gym.picture)
    }
}