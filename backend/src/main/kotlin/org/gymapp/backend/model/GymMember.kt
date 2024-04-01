package org.gymapp.backend.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity(name = "gym_members")
class GymMember (
    @Id val id: String? = null,
    @ManyToMany
    @JoinTable(
        name = "gym_member_classes",
        joinColumns = [JoinColumn(name = "member_id")],
        inverseJoinColumns = [JoinColumn(name = "class_id")])
    val classes: MutableList<GymClass> = mutableListOf(),
    @OneToOne @MapsId @JoinColumn(name = "id") val gymUser: GymUser,
    val firstJoined: LocalDate = LocalDate.now()
) {

}