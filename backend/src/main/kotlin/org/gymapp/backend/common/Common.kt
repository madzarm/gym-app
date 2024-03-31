package org.gymapp.backend.common

import org.gymapp.backend.model.User
import org.gymapp.backend.repository.UserRepository
import org.gymapp.backend.security.exception.UserNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class Common(@Autowired val userRepository: UserRepository) {

    enum class Roles {
        ROLE_ADMIN, ROLE_MEMBER, ROLE_TRAINER
    }

    enum class Difficulty {
        BEGINNER, INTERMEDIATE, ADVANCED
    }

    fun extractId(jwt: Jwt) = jwt.getClaimAsString("sub").split("|")[1]

    fun generateRandomCode(codeLength: Int = 4): String {
        val randomNumber = Random.nextInt(0, Math.pow(10.0, codeLength.toDouble()).toInt() + 1)
        return String.format("%0${codeLength}d", randomNumber)
    }

    fun getCurrentUser(jwt: Jwt): User {
        val id = extractId(jwt)
        return userRepository.findById(id).orElseThrow {
            UserNotFoundException("User not found")
        }
    }


}

