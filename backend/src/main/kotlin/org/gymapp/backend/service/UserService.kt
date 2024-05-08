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
import org.springframework.transaction.annotation.Transactional
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

        val user = User(
            id = request.id,
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName,
            createdAt = LocalDateTime.now(),
            profilePicUrl = request.profilePicUrl,
            gymUsers = mutableListOf(),
            updatedAt = null
        )
        userRepository.save(user)
        return userMapper.modelToDto(user)
    }

    @Transactional
    fun updateUser(request: CreateUserRequest, jwt: Jwt): Unit {
        val id = jwt.getClaimAsString("sub").split("|")[1]
        val user = userRepository.findById(id).get()
        request.firstName?.let { user.firstName = it }
        request.lastName?.let { user.lastName = it }
        user.updatedAt = LocalDateTime.now()
        userRepository.save(user)
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

}