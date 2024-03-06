package org.gymapp.backend.security.repository

import org.gymapp.backend.security.model.SecurityUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface SecurityUserRepository: JpaRepository<SecurityUser, String> {

    fun findByUsername(username: String): Optional<SecurityUser>

    fun existsByUsername(username: String): Boolean
}