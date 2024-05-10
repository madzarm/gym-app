package org.gymapp.backend.extensions

import org.gymapp.backend.model.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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

fun GymMember.getNumberOfGymVisitsThisMonth(): Int {
    val now = LocalDateTime.now()
    val startOfMonth = LocalDate.of(now.year, now.month, 1)
    return this.visits.filter { it.date.isAfter(startOfMonth.atStartOfDay()) }.size
}

fun GymMember.getGym(): Gym {
    return this.gymUser.gym ?: throw IllegalArgumentException("Member is not associated with a gym!")
}

fun GymMember.getGymCode(): String {
    return this.getGym().code
}

fun GymMember.alreadyCompletedChallenge(challenge: Challenge): Boolean {
    return this.completedChallenges.any { it.challenge.id == challenge.id }
}

fun GymMember.alreadyCompletedChallengeToday(challenge: Challenge): Boolean {
    return this.completedChallenges.any { it.challenge.id == challenge.id && it.dateCompleted.toLocalDate() == LocalDate.now() }
}

fun GymMember.alreadyCompletedChallengeThisMonth(challenge: Challenge): Boolean {
    val now = LocalDateTime.now()
    val startOfMonth = LocalDate.of(now.year, now.month, 1)
    return this.completedChallenges.any { it.challenge.id == challenge.id && it.dateCompleted.toLocalDate().isAfter(startOfMonth) }
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

fun String.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

fun String.toLocalTime(): LocalTime {
    return LocalTime.parse(this, DateTimeFormatter.ISO_LOCAL_TIME)
}

fun Gym.getActiveChallenges(): List<Challenge> {
    return this.challenges.filter { !it.isDeleted && it.expiryDate.isAfter(LocalDateTime.now()) }
}