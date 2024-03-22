package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class AccessCode (
    @Id var id: String,
    var code: String,
    var expiryDateTime: LocalDateTime,
    @ManyToOne var gym: Gym
){


}