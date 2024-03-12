package org.gymapp.backend.security.service.impl

import org.gymapp.backend.security.exception.UserAlreadyRegisteredException
import org.gymapp.backend.security.exception.UserNotFoundException
import org.gymapp.backend.security.model.SecurityUser
import org.gymapp.backend.security.repository.SecurityUserRepository
import org.gymapp.backend.security.service.JwtService
import org.gymapp.backend.security.service.SecurityService
import org.gymapp.library.request.CreateAccountRequest
import org.gymapp.library.response.SecurityUserDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class SecurityServiceImpl(
    private val securityUserRepository: SecurityUserRepository,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val passwordEncoder: BCryptPasswordEncoder,
) : SecurityService {

    override fun register(request: CreateAccountRequest): ResponseEntity<Void> {
        if (securityUserRepository.existsByUsername(request.username)) {
            throw UserAlreadyRegisteredException("User is already registered!")
        }

        val securityUser = SecurityUser(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            role = request.role
        )

        securityUserRepository.save(securityUser)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    override fun login(username: String, password: String): ResponseEntity<Map<String, String>> {
        val securityUser = securityUserRepository.findByUsername(username)
            .orElseThrow { UserNotFoundException("User not found!") }

        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))

        val token = jwtService.generateAccessToken(securityUser)
        return ResponseEntity.ok(mapOf("accessToken" to token))
    }

    override fun updateRoles(username: String, role: String): ResponseEntity<Void> {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(securityUser: SecurityUser): ResponseEntity<SecurityUserDto> {
        TODO("Not yet implemented")
    }

    override fun getAllUsers(): ResponseEntity<SecurityUserDto> {
        TODO("Not yet implemented")
    }

    override fun deleteUser(id: String): ResponseEntity<Void> {
        TODO("Not yet implemented")
    }
}