package org.gymapp.backend.model;

import org.springframework.data.jpa.repository.JpaRepository

interface GymUserRepository : JpaRepository<GymUser, String> {
    fun findByUserId(userId: String): List<GymUser>
}