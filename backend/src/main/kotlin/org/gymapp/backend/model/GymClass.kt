package org.gymapp.backend.model

import jakarta.persistence.*
import java.time.Duration
import java.time.LocalDateTime

@Entity
class GymClass (
    @Id
    val id: String,

    var name: String,

    var description: String,

    var dateTime: LocalDateTime,

    var isDeleted: Boolean,

    var duration: Duration,

    var maxParticipants: Int,

    var isRecurring: Boolean,

    @ManyToOne
    val trainer: GymTrainer,

    @ManyToOne
    val gym: Gym,

    @OneToMany(mappedBy = "gymClass", cascade = [CascadeType.ALL])
    var instances: MutableList<GymClassInstance> = mutableListOf(),

    @OneToOne(mappedBy = "gymClass", cascade = [CascadeType.ALL])
    var recurringPattern: RecurringPattern? = null,
) {

    fun addInstance(instance: GymClassInstance) {
        this.instances.add(instance)
    }

    fun removeInstance(instance: GymClassInstance) {
        this.instances.remove(instance)
    }
}