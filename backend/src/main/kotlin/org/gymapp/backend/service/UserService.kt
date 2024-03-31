package org.gymapp.backend.service

import org.gymapp.backend.common.Common
import org.gymapp.backend.mapper.GymUserMapper
import org.gymapp.backend.mapper.UserMapper
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.GymRepository
import org.gymapp.backend.repository.UserRepository
import org.gymapp.backend.security.exception.UserAlreadyRegisteredException
import org.gymapp.library.request.CreateUserRequest
import org.gymapp.library.response.GymUserDto
import org.gymapp.library.response.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class UserService(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userMapper: UserMapper,
    @Autowired private val gymUserMappper: GymUserMapper,
    @Autowired private val gymRepository: GymRepository,
    @Autowired private val roleService: RoleService,
    @Autowired private val gymService: GymService,
    @Autowired private val accessCodeService: AccessCodeService,
) {

    fun createUser(request: CreateUserRequest, jwt: Jwt): UserDto {
        val id = jwt.getClaimAsString("sub").split("|")[1]
        if (userRepository.existsById(id)) {
            throw UserAlreadyRegisteredException("User already exists!")
        }
        request.id = id

        val user = userMapper.requestToUser(request)
        userRepository.save(user)
        return userMapper.modelToDto(user)
    }

    fun getAllUsers(): List<UserDto> {
        val users = userRepository.findAll()
        return userMapper.modelsToDtos(users)
    }

    fun getGymUsers(user: User): List<GymUserDto> {
        user.gymUsers?.let {
            return gymUserMappper.modelsToDtos(it)
        }

        return emptyList()
    }

    fun getCurrentUser(currentUser: User): UserDto? {
        return userMapper.modelToDto(currentUser)
    }



//    fun joinGymAsTrainer(currentUser: User, code: String): GymUserDto {
//        val accessCode = accessCodeService.findAccessCodeByCode(code) ?: throw IllegalArgumentException("Access code not found!")
//        if (accessCode.expiryDateTime.isBefore(LocalDateTime.now())) {
//            accessCodeService.deleteAccessCode(accessCode)
//            throw IllegalArgumentException("Access code expired!")
//        }
//
//        val gym = accessCode.gym
//        val existingGymUser = currentUser.gymUsers?.find { it.gym?.code == accessCode.gym.code }
//        if (existingGymUser != null) {
//            if (existingGymUser.roles.any { it.name == Common.Roles.ROLE_TRAINER.name }) {
//                throw IllegalArgumentException("User is already a trainer of this gym!")
//            } else {
//                val trainerRole = roleService.findByName(Common.Roles.ROLE_TRAINER.name)
//                existingGymUser.roles.add(trainerRole)
//                gymRepository.save(gym)
//                accessCodeService.deleteAccessCode(accessCode)
//                return gymUserMappper.modelToDto(existingGymUser)
//            }
//        }
//
//        val roleTrainer = roleService.findByName(Common.Roles.ROLE_TRAINER.name)
//        val gymUser = GymUser(
//            id = UUID.randomUUID().toString(),
//            roles = mutableListOf(roleTrainer),
//            user = currentUser,
//            gym = gym
//        )
//
//        gym.trainers.add(gymUser)
//        gymRepository.save(gym)
//        accessCodeService.deleteAccessCode(accessCode)
//        return gymUserMappper.modelToDto(gymUser)
//    }
}