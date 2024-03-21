package org.gymapp.backend.service

import org.gymapp.backend.common.Common
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

    fun joinGymAsMember(currentUser: User, code: String): GymUserDto {
        val gym = gymService.findGymByCode(code) ?: throw IllegalArgumentException("Gym not found!")

        val existingGymUser = currentUser.gymUsers?.find { it.gym?.code == code }
        if (existingGymUser != null) {
            if (existingGymUser.roles.any { it.name == Common.Roles.ROLE_MEMBER.name }) {
                throw IllegalArgumentException("User is already a member of this gym!")
            } else {
                val memberRole = roleService.findByName(Common.Roles.ROLE_MEMBER.name).orElseThrow { IllegalArgumentException("Role not found!") }
                existingGymUser.roles.add(memberRole)
                gymRepository.save(gym)
                return gymUserMappper.modelToDto(existingGymUser)
            }
        }

        val roleMember = roleService.findByName(Common.Roles.ROLE_MEMBER.name).orElseThrow { IllegalArgumentException("Role not found!") }
        val gymUser = GymUser(
            id = UUID.randomUUID().toString(),
            roles = mutableListOf(roleMember),
            user = currentUser,
            gym = gym
        )
        gym.members?.add(gymUser) ?: mutableListOf(gymUser)
        gymRepository.save(gym)
        return gymUserMappper.modelToDto(gymUser)
    }
}