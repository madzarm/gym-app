package org.gymapp.backend.repository

import org.gymapp.backend.model.GymTrainer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GymTrainerRepository: JpaRepository<GymTrainer, String> {
}