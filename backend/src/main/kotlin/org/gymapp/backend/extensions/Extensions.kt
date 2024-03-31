package org.gymapp.backend.extensions

import org.gymapp.backend.model.*

fun GymTrainer.addClass(gymClass: GymClass) {
    this.classes.add(gymClass)
}

fun GymTrainer.getGym(): Gym {
    return this.gymUser.gym ?: throw IllegalArgumentException("Trainer is not associated with a gym!")
}

fun Gym.getMember(user: User): GymMember? {
    this.gymUsers.forEach {
        if (it.user.id == user.id) {
            return it.gymMember
        }
    }
    return null
}

fun GymClass.addParticipant(member: GymMember) {
    member.classes.add(this)
    this.participants.add(member)
}
