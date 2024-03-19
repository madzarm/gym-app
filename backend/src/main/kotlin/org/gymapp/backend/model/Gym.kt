package org.gymapp.backend.model

import jakarta.persistence.*

@Entity(name = "gyms")
class Gym(
    @Id var id: String,
    var name: String?,
    var code: String?,
    var picture: String?,
    @OneToMany(mappedBy = "gym",cascade = [CascadeType.ALL]) val members: MutableList<GymUser>?,
    @OneToOne var owner: GymUser?
) {

}