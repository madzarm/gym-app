package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

@Entity
class GymVisit (

    @Id
    val id: String = UUID.randomUUID().toString(),

    @ManyToOne
    val gym: Gym? = null,

    @ManyToOne
    val gymMember: GymMember? = null,

    val date: LocalDateTime = LocalDateTime.now(),

    val duration: Duration? = null,
)