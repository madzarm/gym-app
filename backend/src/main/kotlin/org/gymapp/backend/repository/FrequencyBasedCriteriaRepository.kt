package org.gymapp.backend.repository;

import org.gymapp.backend.model.FrequencyBasedCriteria
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FrequencyBasedCriteriaRepository : JpaRepository<FrequencyBasedCriteria, String> {
    fun findByBaseCriteriaId(Id: String): Optional<FrequencyBasedCriteria>
}