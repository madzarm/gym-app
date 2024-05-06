package org.gymapp.backend.model;

import org.springframework.data.jpa.repository.JpaRepository

interface FrequencyBasedCriteriaRepository : JpaRepository<FrequencyBasedCriteria, String> {
}