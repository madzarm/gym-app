package org.gymapp.backend.model

import jakarta.persistence.*
import java.util.*

@Entity
class RecurringPattern (

    @Id
    val id: String = UUID.randomUUID().toString(),

    val maxNumOfOccurrences: Int,

    @ManyToMany
    @JoinTable(
        name = "recurring_pattern_days_of_week",
        joinColumns = [JoinColumn(name = "recurring_pattern_id")],
        inverseJoinColumns = [JoinColumn(name = "day_of_week_id")])
    val dayOfWeeks: List<DayOfWeek> = mutableListOf(),

    @OneToOne
    val gymClass: GymClass,
){


}