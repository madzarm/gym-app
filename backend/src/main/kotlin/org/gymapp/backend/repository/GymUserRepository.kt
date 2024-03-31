package org.gymapp.backend.repository;

import org.gymapp.backend.model.GymUser
import org.springframework.data.jpa.repository.JpaRepository

interface GymUserRepository : JpaRepository<GymUser, String> {
    fun findByUserId(userId: String): List<GymUser>
}