package org.gymapp.backend.security.service

import org.gymapp.backend.security.model.SecurityUser
import org.gymapp.library.request.CreateAccountRequest
import org.gymapp.library.response.SecurityUserDto
import org.springframework.http.ResponseEntity

interface SecurityService {

    fun register(request: CreateAccountRequest): ResponseEntity<Void>

    fun login(email: String, password: String): ResponseEntity<Map<String, String>>

    fun updateRoles(email: String, role: String): ResponseEntity<Void>

    fun getCurrentUser(securityUser: SecurityUser): ResponseEntity<SecurityUserDto>

    fun getAllUsers(): ResponseEntity<SecurityUserDto>

    fun deleteUser(id: String): ResponseEntity<Void>
}