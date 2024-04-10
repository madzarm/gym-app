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
    @ManyToMany
    @JoinTable(
        name = "gym_member_classes",
        joinColumns = [JoinColumn(name = "class_id")],
        inverseJoinColumns = [JoinColumn(name = "member_id")])
    val participants: MutableList<GymMember> = mutableListOf(),
    @OneToMany(mappedBy = "gymClass", cascade = [CascadeType.ALL])
    val reviews: List<GymClassReview> = mutableListOf()
){
}