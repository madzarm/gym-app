package org.gymapp.backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity(name = "gyms")
class Gym(
    @Id val id: String,
    @OneToMany(mappedBy = "gym") val members: List<GymUser>
) {

}