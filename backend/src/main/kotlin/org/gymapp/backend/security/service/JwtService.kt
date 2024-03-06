package org.gymapp.backend.security.service

import org.springframework.security.core.userdetails.UserDetails

interface JwtService {

    fun extractEmail(token: String): String

    fun generateAccessToken(extraClaims: Map<String, Any>, userDetails: UserDetails): String

    fun generateAccessToken(userDetails: UserDetails): String

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean

}