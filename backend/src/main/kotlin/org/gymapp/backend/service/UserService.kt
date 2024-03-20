package org.gymapp.backend.service

import org.gymapp.backend.mapper.GymUserMapper
import org.gymapp.backend.mapper.UserMapper
import org.gymapp.backend.model.GymUser
import org.gymapp.backend.model.Role
import org.gymapp.backend.model.User
import org.gymapp.backend.repository.GymRepository
import org.gymapp.backend.repository.UserRepository
import org.gymapp.backend.security.exception.UserAlreadyRegisteredException
import org.gymapp.library.request.CreateUserRequest
import org.gymapp.library.response.GymUserDto
import org.gymapp.library.response.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userMapper: UserMapper,
    @Autowired private val gymUserMappper: GymUserMapper,
    @Autowired private val gymRepository: GymRepository,
    @Autowired private val roleService: RoleService,
    @Autowired private val gymService: GymService,
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

    fun joinGymAsMember(currentUser: User, code: String): Unit {
        val gym = gymRepository.findByCode(code) ?: throw IllegalArgumentException("Gym not found!")

        currentUser.gymUsers?.let {
            if (it.any { gymUser -> gymUser.gym?.code == code }) {
                throw IllegalArgumentException("User already joined gym!")
            }
        }

        val role = roleService.findByName("ROLE_MEMBER").get()

        val gymUser = GymUser(
            id = UUID.randomUUID().toString(),
            roles = mutableListOf(role),
            user = currentUser,
            gym = gym
        )
        gym.members?.add(gymUser) ?: mutableListOf(gymUser)
        gymRepository.save(gym)

    }
}