package org.gymapp.backend.service

import org.gymapp.backend.model.GymClass
import org.gymapp.backend.model.RecurringPattern
import org.gymapp.backend.repository.DayOfWeekRepository
import org.gymapp.backend.repository.RecurringPatternRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RecurringPatternService (
    @Autowired private val recurringPatternRepository: RecurringPatternRepository,
    @Autowired private val dayOfWeekRepository: DayOfWeekRepository,
){

    fun createRecurringPattern(maxNumOfOccurrences: Int, daysOfWeek: List<Int>, gymClass: GymClass): RecurringPattern {
        val dayOfWeeks = dayOfWeekRepository.findAllByDayOfWeekIn(daysOfWeek)
        val recurringPattern = RecurringPattern(maxNumOfOccurrences = maxNumOfOccurrences, dayOfWeeks = dayOfWeeks, gymClass = gymClass)
        recurringPatternRepository.save(recurringPattern)
        return recurringPattern
    }
}