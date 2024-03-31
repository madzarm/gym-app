package org.gymapp.backend.model

import jakarta.persistence.*

@Entity(name = "gyms")
class Gym(
    @Id var id: String,
    var name: String,
    var code: String,
    @Column(columnDefinition = "LONGTEXT")
    var picture: String?,
    @OneToMany(mappedBy = "gym",cascade = [CascadeType.ALL]) val gymUsers: MutableList<GymUser> = mutableListOf(),
    @OneToOne var owner: GymOwner?,
    @OneToMany var accessCodes: MutableList<AccessCode> = mutableListOf(),
    @OneToMany(mappedBy = "gym") val classes: MutableList<GymClass> = mutableListOf(),
) {

}