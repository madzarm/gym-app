package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany

@Entity
class DayOfWeek (
    @Id
    val id: String,

    val dayOfWeek: Int,

    @ManyToMany(mappedBy = "dayOfWeeks")
    val recurringPattern: List<RecurringPattern>,
){
}