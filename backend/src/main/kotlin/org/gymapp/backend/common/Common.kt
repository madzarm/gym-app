package org.gymapp.backend.common

import org.springframework.security.oauth2.jwt.Jwt
import kotlin.random.Random

object Common {

    fun extractId(jwt: Jwt) = jwt.getClaimAsString("sub").split("|")[1]

    fun generateRandomGymCode(): String {
        val randomNumber = Random.nextInt(0, 10000)
        return String.format("%04d", randomNumber)
    }

}

enum class Roles {
    ROLE_ADMIN, ROLE_MEMBER, ROLE_TRAINER
}
