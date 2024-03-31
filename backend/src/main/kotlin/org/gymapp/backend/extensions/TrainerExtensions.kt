package org.gymapp.backend.extensions

import org.gymapp.backend.model.Gym
import org.gymapp.backend.model.GymClass
import org.gymapp.backend.model.GymTrainer

fun GymTrainer.addClass(gymClass: GymClass) {
    this.classes.add(gymClass)
}

fun GymTrainer.getGym(): Gym {
    return this.gymUser.gym ?: throw IllegalArgumentException("Trainer is not associated with a gym!")
}