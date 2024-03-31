package org.gymapp.backend.service

import org.gymapp.backend.common.Common
import org.gymapp.backend.mapper.GymTrainerMapper
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.GymUserRepository
import org.gymapp.backend.repository.GymTrainerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class TrainerService(
    @Autowired private val trainerRepository: GymTrainerRepository,
    @Autowired private val gymService: GymService,
    @Autowired private val accessCodeService: AccessCodeService,
    @Autowired private val roleService: RoleService,
    @Autowired private val gymUserRepository: GymUserRepository,
    @Autowired private val gymTrainerMapper: GymTrainerMapper,
    @Autowired private val gymTrainerRepository: GymTrainerRepository,
) {
//    fun getCurrentTrainer(currentUser: User, gymId: String): TrainerDto? {
//        val gym = gymService.findById(gymId)
//        val trainer: GymTrainer = trainerRepository.findByI(currentUser)?.let { TrainerDto(it) }
//    }
//
//
//    fun findByGymUserId(gymUserId: String): TrainerDto? {
//        return trainerRepository.findByGymUserId(gymUserId)?.let { TrainerDto(it) }
//    }
    fun joinGymAsTrainer(currentUser: User, code: String): GymTrainerDto {
        val accessCode = accessCodeService.findAccessCodeByCode(code)
        if (accessCode.expiryDateTime.isBefore(LocalDateTime.now())) {
            accessCodeService.deleteAccessCode(accessCode)
            throw IllegalArgumentException("Access code expired!")
        }

        val gym = accessCode.gym
        val existingGymUser = currentUser.gymUsers.find { it.gym?.code == accessCode.gym.code }
        if (existingGymUser != null) {
            if (existingGymUser.roles.any { it.name == Common.Roles.ROLE_TRAINER.name }) {
                throw IllegalArgumentException("User is already a trainer of this gym!")
            } else {
                val trainerRole = roleService.findByName(Common.Roles.ROLE_TRAINER.name)
                existingGymUser.roles.add(trainerRole)
                gymUserRepository.save(existingGymUser)
                accessCodeService.deleteAccessCode(accessCode)

                val trainer = GymTrainer.fromGymUser(existingGymUser)
                gymTrainerRepository.save(trainer)
                return gymTrainerMapper.modelToDto(trainer)
            }
        }

        val roleTrainer = roleService.findByName(Common.Roles.ROLE_TRAINER.name)
        val gymUser = GymUser(
            id = UUID.randomUUID().toString(),
            roles = mutableListOf(roleTrainer),
            user = currentUser,
            gym = gym
        )

        gymUserRepository.save(gymUser)
        accessCodeService.deleteAccessCode(accessCode)

        val trainer = GymTrainer.fromGymUser(gymUser)
        gymTrainerRepository.save(trainer)
        return gymTrainerMapper.modelToDto(trainer)
    }

}