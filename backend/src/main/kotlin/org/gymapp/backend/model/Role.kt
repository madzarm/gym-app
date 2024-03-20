package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "roles")
class Role(
    @Id val id: String,
    var name: String

) {
}
