package org.gymapp.backend.repository

import org.gymapp.backend.model.GymVisit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GymVisitRepository : JpaRepository<GymVisit, String>  {
}