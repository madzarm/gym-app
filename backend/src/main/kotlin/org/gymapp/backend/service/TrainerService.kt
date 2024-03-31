package org.gymapp.backend.service

import org.gymapp.backend.common.Common
import org.gymapp.backend.extensions.addClass
import org.gymapp.backend.extensions.getGym
import org.gymapp.backend.mapper.GymTrainerMapper
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.GymClassRepository
import org.gymapp.backend.repository.GymUserRepository
import org.gymapp.backend.repository.GymTrainerRepository
import org.gymapp.library.response.GymTrainerDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@Service
class TrainerService(
    @Autowired private val accessCodeService: AccessCodeService,
    @Autowired private val roleService: RoleService,
    @Autowired private val gymUserRepository: GymUserRepository,
    @Autowired private val gymTrainerMapper: GymTrainerMapper,
    @Autowired private val gymTrainerRepository: GymTrainerRepository,
    private val gymClassRepository: GymClassRepository,
) {

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

    fun getTrainer(currentUser: User, gymId: String): GymTrainerDto {
        val trainer = currentUser.getTrainer(gymId)

        return gymTrainerMapper.modelToDto(trainer)
    }

    fun createClass(currentUser: User, gymId: String, request: CreateClassRequest): GymTrainerDto {
        val trainer = currentUser.getTrainer(gymId)
        val gym = trainer.getGym()

        val gymClass = GymClass(
            id = UUID.randomUUID().toString(),
            name = request.name,
            description = request.description,
            dateTime = LocalDateTime.parse(request.dateTime),
            duration = Duration.ofMinutes(request.duration.toLong()),
            maxParticipants = request.maxParticipants,
            trainer = trainer,
            gym = gym
        )

        gymClassRepository.save(gymClass)

        trainer.addClass(gymClass)
        return gymTrainerMapper.modelToDto(trainer)
    }

    fun updateClass(currentUser: User, classId: String, request: UpdateClassRequest): GymTrainerDto {
        val gymClass = gymClassRepository.findById(classId).orElseThrow { IllegalArgumentException("Class not found!") }
        val trainer = gymClass.trainer

        request.name?.let { gymClass.name = it }
        request.description?.let { gymClass.description = it }
        request.dateTime?.let { gymClass.dateTime = LocalDateTime.parse(it) }
        request.duration?.let { gymClass.duration = Duration.ofMinutes(it.toLong()) }
        request.maxParticipants?.let { gymClass.maxParticipants = it }

        gymClassRepository.save(gymClass)

        return gymTrainerMapper.modelToDto(trainer)
    }

}