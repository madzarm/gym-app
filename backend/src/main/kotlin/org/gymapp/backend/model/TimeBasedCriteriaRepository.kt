package org.gymapp.backend.model;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TimeBasedCriteriaRepository : JpaRepository<TimeBasedCriteria, String> {
    fun findByBaseCriteriaId(Id: String): Optional<TimeBasedCriteria>
}