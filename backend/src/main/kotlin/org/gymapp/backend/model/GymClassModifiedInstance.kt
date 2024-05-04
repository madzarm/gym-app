package org.gymapp.backend.model

import jakarta.persistence.*
import java.time.Duration
import java.time.LocalDateTime

@Entity
class GymClassModifiedInstance (
    @Id
    val id: String? = null,

    @OneToOne @MapsId @JoinColumn(name = "id")
    val gymClassInstance: GymClassInstance,

    var description: String,

    var dateTime: LocalDateTime,

    var duration: Duration,

    var maxParticipants: Int,

    var isCanceled: Boolean,

    @ManyToOne
    val trainer: GymTrainer,

    ) {
}