package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime
import java.util.UUID

@Entity
class GymClassReview (
    @Id
    val id: String = UUID.randomUUID().toString(),
    val review: String,
    val date: LocalDateTime = LocalDateTime.now(),
    @ManyToOne
    val gymClass: GymClass,
    @ManyToOne
    val member: GymMember
) {


}