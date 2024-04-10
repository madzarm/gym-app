package org.gymapp.backend.service

import org.gymapp.backend.common.Common
import org.gymapp.backend.extensions.addClass
import org.gymapp.backend.extensions.getGym
import org.gymapp.backend.extensions.getParticipantsFcmTokens
import org.gymapp.backend.extensions.getUpcomingClasses
import org.gymapp.backend.mapper.GymTrainerMapper
import org.gymapp.backend.mapper.GymUserMapper
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.GymClassRepository
import org.gymapp.backend.repository.GymUserRepository
import org.gymapp.backend.repository.GymTrainerRepository
import org.gymapp.library.request.CreateClassRequest
import org.gymapp.library.request.UpdateClassRequest
import org.gymapp.library.response.GymTrainerDto
import org.gymapp.library.response.GymUserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
    @Autowired private val gymUserMapper: GymUserMapper,
    @Autowired private val gymClassRepository: GymClassRepository,
    @Autowired private val notificationService: NotificationService,
) {

    fun joinGymAsTrainer(currentUser: User, code: String): GymUserDto {
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
                existingGymUser.gymTrainer = trainer
                return gymUserMapper.modelToDto(existingGymUser)
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
        return gymUserMapper.modelToDto(gymUser)
    }

    fun getTrainer(currentUser: User, gymId: String): GymTrainerDto {
        val trainer = currentUser.getTrainer(gymId)

        return gymTrainerMapper.modelToDto(trainer)
    }

    fun getTrainerWithUpcomingClasses(currentUser: User, gymId: String): GymTrainerDto {
        val trainer = currentUser.getTrainer(gymId)
        val upcomingClasses = trainer.getUpcomingClasses()
        trainer.classes = upcomingClasses.toMutableList()
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

    @Transactional
    fun deleteClass(currentUser: User, classId: String): GymTrainerDto {
        val gymClass = gymClassRepository.findById(classId).orElseThrow { IllegalArgumentException("Class not found!") }
        val trainer = gymClass.trainer

        gymClassRepository.delete(gymClass)

        val fcmTokens = gymClass.getParticipantsFcmTokens()
        notificationService.sendNotifications(fcmTokens, "Class cancelled!", "The class ${gymClass.name} has been cancelled!")

        return gymTrainerMapper.modelToDto(trainer)
    }

    fun findTrainerById(id: String): GymTrainer {
        return gymTrainerRepository.findById(id).orElseThrow { IllegalArgumentException("Trainer not found!") }
    }

}