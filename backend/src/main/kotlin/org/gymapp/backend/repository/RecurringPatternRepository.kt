package org.gymapp.backend.repository

import org.gymapp.backend.model.RecurringPattern
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecurringPatternRepository : JpaRepository<RecurringPattern, String> {
}