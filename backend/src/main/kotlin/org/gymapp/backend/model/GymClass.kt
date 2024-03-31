package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import java.time.Duration
import java.time.LocalDateTime

@Entity
class GymClass (
    @Id
    val id: String,
    val name: String,
    val description: String,
    val dateTime: LocalDateTime,
    val duration: Duration,
    val maxParticipants: Int,
    @ManyToOne val trainer: GymTrainer,
    @ManyToOne val gym: Gym,
    @ManyToMany val participants: List<GymMember> = mutableListOf()
){
}