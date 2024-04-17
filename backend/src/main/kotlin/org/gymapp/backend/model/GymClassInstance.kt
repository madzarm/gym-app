package org.gymapp.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
class GymClassInstance (

    @Id
    val id: String = UUID.randomUUID().toString(),

    var dateTime: LocalDateTime,

    @OneToOne(mappedBy = "gymClassInstance", cascade = [CascadeType.ALL])
    val gymClassModifiedInstance: GymClassModifiedInstance? = null,

    @ManyToOne
    val gymClass: GymClass,

    @ManyToMany
    @JoinTable(
        name = "gym_member_classes",
        joinColumns = [JoinColumn(name = "class_id")],
        inverseJoinColumns = [JoinColumn(name = "member_id")])
    val participants: MutableList<GymMember> = mutableListOf(),
) {
}