package org.gymapp.backend.repository

import org.gymapp.backend.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleRepository: JpaRepository<Role, String> {

    fun findByName(name: String): Optional<Role>

}