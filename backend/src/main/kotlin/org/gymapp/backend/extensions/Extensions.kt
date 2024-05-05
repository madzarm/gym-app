package org.gymapp.backend.extensions

import org.gymapp.backend.model.*
import java.time.LocalDateTime

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

fun GymClassInstance.addParticipant(member: GymMember) {
    member.classes.add(this)
    this.participants.add(member)
}

fun GymClassInstance.getParticipantsFcmTokens(): List<String> {
    return this.participants.map { it.gymUser.user.fcmToken ?: "" }
}

fun GymTrainer.getUpcomingClasses(): List<GymClass> {
    return this.classes.filter { (it.dateTime.isAfter(LocalDateTime.now()) || it.isRecurring ) && !it.isDeleted }
}

fun LocalDateTime.readStringToDate(): LocalDateTime {
    return LocalDateTime.parse(this.toString())
}
