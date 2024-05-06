package org.gymapp.backend.model;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FrequencyBasedCriteriaRepository : JpaRepository<FrequencyBasedCriteria, String> {
    fun findByBaseCriteriaId(Id: String): Optional<FrequencyBasedCriteria>
}