package org.gymapp.backend.service

import jakarta.transaction.Transactional
import org.gymapp.backend.common.Common
import org.gymapp.backend.mapper.GymMapper
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.GymRepository
import org.gymapp.backend.repository.RoleRepository
import org.gymapp.backend.repository.UserRepository
import org.gymapp.library.request.CreateGymRequest
import org.gymapp.library.response.GymDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.util.*

@Service
class GymService(
    @Autowired private val gymRepository: GymRepository,
    @Autowired private val roleRepository: RoleRepository,
    @Autowired private val gymUserRepository: GymUserRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val gymMapper: GymMapper,
    @Autowired private val common: Common
) {

    @Transactional
    fun createGym(request: CreateGymRequest, jwt: Jwt): GymDto {
        val userId = common.extractId(jwt)
        val gym = Gym(
            UUID.randomUUID().toString(),
            request.name,
            common.generateRandomGymCode(),
            request.picture,
            mutableListOf(),
            null
        )

        val user: User = userRepository.findById(userId).get()

        val role = roleRepository.findByName(Common.Roles.ROLE_ADMIN.name).get()
        val gymUser = GymUser(
            id = UUID.randomUUID().toString(),
            roles = mutableSetOf(role),
            user = user,
            gym = gym
        )
        gym.owner = gymUser
        gymUserRepository.save(gymUser)

        return GymDto(name = gym.name, picture = gym.picture, code = gym.code)
    }

    fun findUserGyms(user: User): List<GymDto> {
        val gymUsers = gymUserRepository.findByUserId(user.id!!)
        val gyms = gymUsers.map { it.gym }
        return gymMapper.modelsToDtos(gyms)
    }
}