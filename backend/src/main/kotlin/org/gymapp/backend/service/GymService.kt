package org.gymapp.backend.service

import jakarta.transaction.Transactional
import org.gymapp.backend.common.Common
import org.gymapp.backend.mapper.AccessCodeMapper
import org.gymapp.backend.mapper.GymMapper
import org.gymapp.backend.mapper.GymUserMapper
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.GymRepository
import org.gymapp.backend.repository.RoleRepository
import org.gymapp.backend.repository.UserRepository
import org.gymapp.library.request.CreateGymRequest
import org.gymapp.library.response.GymDto
import org.gymapp.library.response.GymUserDto
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
    @Autowired private val common: Common,
    @Autowired private val gymUserMapper: GymUserMapper,
    @Autowired private val accessCodeService: AccessCodeService,
    @Autowired private val accessCodeMapper: AccessCodeMapper
) {

    @Transactional
    fun createGym(request: CreateGymRequest, jwt: Jwt): GymUserDto {
        val userId = common.extractId(jwt)
        val gym = Gym(
            UUID.randomUUID().toString(),
            request.name,
            common.generateRandomCode(),
            request.picture,
            mutableListOf(),
            null
        )

        val user: User = userRepository.findById(userId).get()

        val role = roleRepository.findByName(Common.Roles.ROLE_ADMIN.name).get()
        val gymUser = GymUser(
            id = UUID.randomUUID().toString(),
            roles = mutableListOf(role),
            user = user,
            gym = gym
        )
        gym.owner = gymUser
        gymUserRepository.save(gymUser)

        return gymUserMapper.modelToDto(gymUser)
    }

    fun findUserGyms(user: User): List<GymDto> {
        val gymUsers = gymUserRepository.findByUserId(user.id!!)
        val gyms = gymUsers.map { it.gym }
        return gymMapper.modelsToDtos(gyms)
    }

    fun findGymByCode(code: String): Gym? {
        return gymRepository.findByCode(code)
    }

    fun generateAccessCode(gymId: String, user: User): AccessCodeDto {
        val gym = gymRepository.findById(gymId).get()
        if (gym.owner?.user?.id != user.id) {
            throw IllegalArgumentException("User is not the owner of the gym")
        }
        val accessCodeDto = accessCodeService.generateAccessCodeDto(gym)
        return accessCodeDto
    }
}