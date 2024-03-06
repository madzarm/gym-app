package org.gymapp.backend.security.service.impl

import org.gymapp.backend.security.model.SecurityUser
import org.gymapp.backend.security.service.SecurityService
import org.gymapp.library.request.CreateAccountRequest
import org.gymapp.library.response.SecurityUserDto
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class SecurityServiceImpl: SecurityService {
    override fun register(request: CreateAccountRequest): ResponseEntity<Void> {
        TODO("Not yet implemented")
    }

    override fun login(email: String, password: String): ResponseEntity<Map<String, String>> {
        TODO("Not yet implemented")
    }

    override fun updateRoles(email: String, role: String): ResponseEntity<Void> {
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