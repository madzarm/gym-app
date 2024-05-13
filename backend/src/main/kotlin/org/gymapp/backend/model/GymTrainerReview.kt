package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime
import java.util.*

@Entity
class GymTrainerReview (

    @Id
    val id: String = UUID.randomUUID().toString(),

    val review: String = "",

    val rating: Int = 0,

    val date: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    val trainer: GymTrainer,

    @ManyToOne
    val member: GymMember
){
}