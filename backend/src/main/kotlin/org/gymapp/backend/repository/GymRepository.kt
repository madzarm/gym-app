package org.gymapp.backend.repository

import org.gymapp.backend.model.Gym
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GymRepository: JpaRepository<Gym, String> {

    fun findByCode(code: String): Gym?
}