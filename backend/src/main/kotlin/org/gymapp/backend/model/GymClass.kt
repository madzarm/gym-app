package org.gymapp.backend.model

import jakarta.persistence.*
import java.time.Duration
import java.time.LocalDateTime

@Entity
class GymClass (
    @Id
    val id: String,
    var name: String,
    var description: String,
    var dateTime: LocalDateTime,
    var duration: Duration,
    var maxParticipants: Int,
    @ManyToOne val trainer: GymTrainer,
    @ManyToOne val gym: Gym,
    @ManyToMany(mappedBy = "classes") val participants: MutableList<GymMember> = mutableListOf()
){
}