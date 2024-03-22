package org.gymapp.backend.model

import jakarta.persistence.*

@Entity(name = "gyms")
class Gym(
    @Id var id: String,
    var name: String,
    var code: String,
    @Column(columnDefinition = "LONGTEXT")
    var picture: String?,
    @OneToMany(mappedBy = "gym",cascade = [CascadeType.ALL]) val members: MutableList<GymUser> = mutableListOf(),
    @OneToOne var owner: GymUser?,
    @OneToMany var accessCodes: MutableList<AccessCode> = mutableListOf()
) {

}