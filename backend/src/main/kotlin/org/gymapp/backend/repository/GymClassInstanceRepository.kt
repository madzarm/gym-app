package org.gymapp.backend.repository

import org.gymapp.backend.model.GymClassInstance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GymClassInstanceRepository : JpaRepository<GymClassInstance, String> {
}