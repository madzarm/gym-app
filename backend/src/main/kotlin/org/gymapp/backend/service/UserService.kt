package org.gymapp.backend.service

import org.gymapp.backend.mapper.UserMapper
import org.gymapp.backend.repository.UserRepository
import org.gymapp.backend.security.exception.UserAlreadyRegisteredException
import org.gymapp.library.request.CreateUserRequest
import org.gymapp.library.response.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class UserService(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userMapper: UserMapper
) {

    fun createUser(request: CreateUserRequest, jwt: Jwt): UserDto {
        val id = jwt.getClaimAsString("sub").split("|")[1]
        if (userRepository.existsById(id)) {
            throw UserAlreadyRegisteredException("User already exists!")
        }
        request.id = id

        val user = userMapper.requestToUser(request)
        userRepository.save(user)
        return userMapper.userToUserDto(user)
    }

    fun getAllUsers(): List<UserDto> {
        val users = userRepository.findAll()
        return userMapper.usersToUserDtos(users)
    }
}