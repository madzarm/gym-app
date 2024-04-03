package org.gymapp.backend.repository

import org.gymapp.backend.model.GymVisit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GymVisitRepository : JpaRepository<GymVisit, String>  {

    fun findByGymIdAndDurationNotNull(gymId: String): List<GymVisit>
    fun findByGymIdAndGymMemberIdAndDurationNull(gymId: String, id: String?): List<GymVisit>

}