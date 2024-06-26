package org.gymapp.backend.service

import org.gymapp.backend.common.Common
import org.gymapp.backend.extensions.addClass
import org.gymapp.backend.extensions.getGym
import org.gymapp.backend.extensions.getUpcomingClasses
import org.gymapp.backend.mapper.GymTrainerMapper
import org.gymapp.backend.mapper.GymUserMapper
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.*
import org.gymapp.library.request.CreateClassRequest
import org.gymapp.library.request.CreateRecurringClassRequest
import org.gymapp.library.request.UpdateClassRequest
import org.gymapp.library.request.UpdateGymClassInstanceRequest
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
    @Autowired private val gymClassInstanceRepository: GymClassInstanceRepository,
    @Autowired private val recurringPatternService: RecurringPatternService,
    @Autowired private val gymClassModifiedInstanceRepository: GymClassModifiedInstanceRepository,
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

        //gymUserRepository.save(gymUser)
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

    fun createClassInstance(currentUser: User, gymId: String, request: CreateClassRequest): GymTrainerDto {
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
            gym = gym,
            isRecurring = false,
            isDeleted = false
        )

        val gymClassInstance = GymClassInstance(
            id = UUID.randomUUID().toString(),
            dateTime = gymClass.dateTime,
            gymClass = gymClass
        )

        gymClassRepository.save(gymClass)
        gymClassInstanceRepository.save(gymClassInstance)

        trainer.addClass(gymClass)
        return gymTrainerMapper.modelToDto(trainer)
    }

    fun createRecurringClass(currentUser: User, gymId: String, request: CreateRecurringClassRequest): GymTrainerDto {
        val trainer = currentUser.getTrainer(gymId)
        val gym = trainer.getGym()




        val gymClass = GymClass(
            id = UUID.randomUUID().toString(),
            name = request.name ?: "",
            description = request.description ?: "",
            dateTime = LocalDateTime.parse(request.dateTime),
            duration = Duration.ofMinutes(request.duration!!.toLong()),
            maxParticipants = request.maxParticipants ?: 0,
            trainer = trainer,
            gym = gym,
            isRecurring = true,
            isDeleted = false
        )
        gymClassRepository.save(gymClass)

        val recurringPattern = recurringPatternService.createRecurringPattern(request.maxNumOfOccurrences ?: 0, request.daysOfWeek ?: emptyList(), gymClass)
        gymClass.recurringPattern = recurringPattern
        gymClassRepository.save(gymClass)

        return gymTrainerMapper.modelToDto(trainer)
    }

    @Transactional
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
    fun updateGymClassInstance(currentUser: User, classId: String, request: UpdateGymClassInstanceRequest): GymTrainerDto {
        val dateTime = LocalDateTime.parse(request.originalDateTime)
        val date = dateTime.toLocalDate()
        val gymClassInstanceOptional = gymClassInstanceRepository.findByGymClassIdAndDateTime(classId, date)

        if (gymClassInstanceOptional.isPresent) {
            val gymClassInstance = gymClassInstanceOptional.get()
            val gymClass = gymClassInstance.gymClass!!
            val trainer = gymClass.trainer

            gymClassInstance.gymClassModifiedInstance?.let {
                it.description = request.description ?: gymClass.description
                it.dateTime = request.dateTime?.let { LocalDateTime.parse(it) } ?: gymClass.dateTime
                it.duration = Duration.ofMinutes(request.duration?.toLong() ?: gymClass.duration.toMinutes())
                it.maxParticipants = request.maxParticipants ?: gymClass.maxParticipants
                it.isCanceled = request.isCanceled ?: false

                gymClassModifiedInstanceRepository.save(it)
                return gymTrainerMapper.modelToDto(trainer)
            }

            val gymClassModifiedInstance = createModifiedClassInstance(gymClassInstance, request)

            gymClassModifiedInstanceRepository.save(gymClassModifiedInstance)
            return gymTrainerMapper.modelToDto(trainer)
        }

        val gymClassInstance = createClassInstance(classId, request.originalDateTime)

        val gymClassModifiedInstance = createModifiedClassInstance(gymClassInstance, request)
        gymClassModifiedInstanceRepository.save(gymClassModifiedInstance)

        val trainer = gymClassInstance.gymClass.trainer
        gymClassInstance.gymClass.addInstance(gymClassInstance)
        return gymTrainerMapper.modelToDto(trainer)
    }

    private fun createClassInstance(gymClassId: String, dateTime: String): GymClassInstance {
        val gymClass = gymClassRepository.findById(gymClassId).orElseThrow { IllegalArgumentException("Class not found!") }
        return GymClassInstance(
            dateTime = LocalDateTime.parse(dateTime),
            gymClass = gymClass
        )
    }

    private fun createModifiedClassInstance(gymClassInstance: GymClassInstance, request: UpdateGymClassInstanceRequest): GymClassModifiedInstance {
        val gymClass = gymClassInstance.gymClass
        return GymClassModifiedInstance(
            description = request.description ?: gymClass.description,
            dateTime = request.dateTime?.let { LocalDateTime.parse(it) } ?: request.originalDateTime.let { LocalDateTime.parse(it) },
            duration = Duration.ofMinutes(request.duration?.toLong() ?: gymClass.duration.toMinutes()),
            maxParticipants = request.maxParticipants ?: gymClass.maxParticipants,
            trainer = gymClass.trainer,
            isCanceled = request.isCanceled ?: false,
            gymClassInstance = gymClassInstance
        )
    }

    @Transactional
    fun deleteClass(currentUser: User, classId: String): GymTrainerDto {
        val gymClass = gymClassRepository.findById(classId).orElseThrow { IllegalArgumentException("Class not found!") }
        val trainer = gymClass.trainer

        gymClass.isDeleted = true
        gymClassRepository.save(gymClass)
        return gymTrainerMapper.modelToDto(trainer)
    }

    @Transactional
    fun cancelClass(currentUser: User, classId: String, dateTime: String): GymTrainerDto {
        val date = LocalDateTime.parse(dateTime).toLocalDate()
        val gymClassInstanceOptional = gymClassInstanceRepository.findByGymClassIdAndDateTime(classId, date)

        if (gymClassInstanceOptional.isPresent) {
            val gymClassInstance = gymClassInstanceOptional.get()
            val gymClass = gymClassInstance.gymClass
            val trainer = gymClass.trainer

            gymClassInstance.gymClassModifiedInstance?.let {
                it.isCanceled = true

                gymClassModifiedInstanceRepository.save(it)
                return gymTrainerMapper.modelToDto(trainer)
            }

            val gymClassModifiedInstance = createCanceledModifiedClassInstance(gymClassInstance)

            notificationService.notifyParticipants(gymClassInstance.participants, "Class Canceled", "The class ${gymClass.name} has been canceled!")

            gymClassModifiedInstanceRepository.save(gymClassModifiedInstance)
            return gymTrainerMapper.modelToDto(trainer)
        }

        val gymClassInstance = createCanceledClassInstance(classId, dateTime)

        val gymClassModifiedInstance = createCanceledModifiedClassInstance(gymClassInstance)
        gymClassModifiedInstanceRepository.save(gymClassModifiedInstance)

        val trainer = gymClassInstance.gymClass.trainer
        gymClassInstance.gymClass.addInstance(gymClassInstance)
        return gymTrainerMapper.modelToDto(trainer)
    }

    private fun createCanceledClassInstance(gymClassId: String, dateTime: String): GymClassInstance {
        val gymClass = gymClassRepository.findById(gymClassId).orElseThrow { IllegalArgumentException("Class not found!") }
        return GymClassInstance(
            dateTime = LocalDateTime.parse(dateTime),
            gymClass = gymClass
        )
    }

    private fun createCanceledModifiedClassInstance(gymClassInstance: GymClassInstance): GymClassModifiedInstance {
        val gymClass = gymClassInstance.gymClass
        return GymClassModifiedInstance(
            dateTime = gymClassInstance.dateTime,
            duration = gymClass.duration,
            maxParticipants = gymClass.maxParticipants,
            trainer = gymClass.trainer,
            isCanceled = true,
            gymClassInstance = gymClassInstance,
            description = gymClass.description
        )
    }

    fun findTrainerById(id: String): GymTrainer {
        return gymTrainerRepository.findById(id).orElseThrow { IllegalArgumentException("Trainer not found!") }
    }

}