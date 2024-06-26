package org.gymapp.backend.model

import jakarta.persistence.*

@Entity(name = "gyms")
class Gym(

    @Id var id: String,

    var name: String,

    var code: String,

    var subscriptionFee: Long,

    var stripeAccountId: String,

    var subscriptionPriceId: String?,

    @Column(columnDefinition = "LONGTEXT")
    var picture: String?,

    @OneToMany(mappedBy = "gym", cascade = [CascadeType.ALL])
    val gymUsers: MutableList<GymUser> = mutableListOf(),

    @OneToOne
    var owner: GymOwner?,

    @OneToMany
    var accessCodes: MutableList<AccessCode> = mutableListOf(),

    @OneToMany(mappedBy = "gym")
    val classes: MutableList<GymClass> = mutableListOf(),

    @OneToMany(mappedBy = "gym")
    val visits: MutableList<GymVisit> = mutableListOf(),

    @OneToMany(mappedBy = "gym")
    val challenges: MutableList<Challenge> = mutableListOf(),
) {
}