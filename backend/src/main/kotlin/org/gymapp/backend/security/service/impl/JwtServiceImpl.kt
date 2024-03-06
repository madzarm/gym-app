package org.gymapp.backend.security.service.impl

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.gymapp.backend.security.service.JwtService
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtServiceImpl: JwtService {

    companion object {
        private const val SECRET_KEY = "8B8D526499AB3D4D0C882449421712DC1A8711E882539296501E0344E12FDE98"
    }

    override fun extractEmail(token: String): String = extractClaim(token, Claims::getSubject)

    private fun getSignInKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(SECRET_KEY)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    override fun generateAccessToken(extraClaims: Map<String, Any>, userDetails: UserDetails): String {
        val roles = userDetails.authorities.map(GrantedAuthority::getAuthority)

        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .claim("roles", roles)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 30))
            .signWith(getSignInKey(), Jwts.SIG.HS256)
            .compact()
    }

    override fun generateAccessToken(userDetails: UserDetails): String =
        generateAccessToken(emptyMap(), userDetails)

    override fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val email = extractEmail(token)
        return email == userDetails.username && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean =
        extractClaim(token, Claims::getExpiration).before(Date())

    private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T =
        claimsResolver(extractAllClaims(token))

    private fun extractAllClaims(token: String): Claims = Jwts
        .parser()
        .verifyWith(getSignInKey())
        .build()
        .parseSignedClaims(token)
        .payload
}