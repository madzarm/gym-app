package org.gymapp.backend.repository

import org.gymapp.backend.model.DayOfWeek
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DayOfWeekRepository : JpaRepository<DayOfWeek, String> {
    fun findAllByDayOfWeekIn(daysOfWeek: List<Int>): List<DayOfWeek>
}