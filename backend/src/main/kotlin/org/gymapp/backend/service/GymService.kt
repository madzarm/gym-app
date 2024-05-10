package org.gymapp.backend.service

import jakarta.transaction.Transactional
import org.gymapp.backend.common.Common
import org.gymapp.backend.mapper.*
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.*
import org.gymapp.library.request.CreateGymRequest
import org.gymapp.library.response.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class GymService(
    @Autowired private val gymRepository: GymRepository,
    @Autowired private val roleRepository: RoleRepository,
    @Autowired private val gymUserRepository: GymUserRepository,
    @Autowired private val gymMapper: GymMapper,
    @Autowired private val common: Common,
    @Autowired private val gymUserMapper: GymUserMapper,
    @Autowired private val gymOwnerRepository: GymOwnerRepository,
    @Autowired private val gymClassMapper: GymClassMapper,
    @Autowired private val gymMemberRepository: GymMemberRepository,
    @Autowired private val gymMemberMapper: GymMemberMapper,
    @Autowired private val gymVisitRepository: GymVisitRepository,
    @Autowired private val gymVisitMapper: GymVisitMapper,
) {

    @Transactional
    fun createGym(request: CreateGymRequest, currentUser: User): GymUserDto {
        val gym = Gym(
            UUID.randomUUID().toString(),
            request.name,
            common.generateRandomCode(),
            request.picture,
            mutableListOf(),
            null
        )


        val role = roleRepository.findByName(Common.Roles.ROLE_ADMIN.name).get()
        val gymUser = GymUser(
            id = UUID.randomUUID().toString(),
            roles = mutableListOf(role),
            user = currentUser,
            gym = gym
        )

        val owner = GymOwner.fromGymUser(gymUser)


        currentUser.gymUsers.add(gymUser)
        gym.owner = owner
        gymOwnerRepository.save(owner)

        return gymUserMapper.modelToDto(gymUser)
    }

    fun getGymMemberFull(userId: String): GymMemberDtoFull? {
        val gymMember = gymMemberRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Member not found") }

        return gymMemberMapper.modelToDtoFullNoVisits(gymMember)
    }

    fun getGymMembersFull(userIds: List<String>): List<GymMemberDtoFull> {
        val gymMembers = gymMemberRepository.findAllById(userIds)
        return gymMemberMapper.modelsToDtosFull(gymMembers)
    }

    fun getUpcomingGymClasses(currentUser: User, gymId: String): List<GymClassDto> {
        val gym = findById(gymId)
        val classes = gym.classes.filter {
            if (!it.isDeleted) {
                if (it.isRecurring) {
                    it.dateTime.plusWeeks(((it.recurringPattern?.maxNumOfOccurrences
                        ?: (52 / it.recurringPattern?.dayOfWeeks?.size!!))).toLong()).isAfter(LocalDateTime.now())
                } else {
                    it.dateTime.isAfter(LocalDateTime.now())
                }
            } else {
                false
            }

        }

        return gymClassMapper.modelsToDtos(classes)
    }

    fun findUserGyms(user: User): List<GymDto> {
        val gymUsers = gymUserRepository.findByUserId(user.id!!)
        val gyms = gymUsers.map { it.gym }
        return gymMapper.modelsToDtos(gyms)
    }

    fun getGymVisitsHeatMapData(currentUser: User, gymId: String): List<VisitCountByDay> {
        val visitCounts = gymVisitRepository.findVisitsGroupedByDayAndHour(gymId)

        return visitCounts.groupBy { it.getDayOfWeek() }
            .map { entry ->
                VisitCountByDay(
                    dayOfWeek = entry.key,
                    hours = entry.value.map { VisitCountByHour(hour = it.getHour(), visitCount = it.getVisitCount()) }
                )
            }
            .map { entry ->
                if (entry.dayOfWeek == 1) {
                    VisitCountByDay(
                        dayOfWeek = 7,
                        hours = entry.hours.sortedBy { it.hour }
                    )
                } else {
                    VisitCountByDay(
                        dayOfWeek = entry.dayOfWeek - 1,
                        hours = entry.hours.sortedBy { it.hour }
                    )
                }
            }
            .sortedBy { it.dayOfWeek }

    }

    fun findGymByCode(code: String): Gym {
        return gymRepository.findByCode(code) ?: throw IllegalArgumentException("Gym not found")
    }

    fun findById(gymId: String): Gym {
        return gymRepository.findById(gymId)
            .orElseThrow { IllegalArgumentException("Gym not found")}
    }

    fun getLiveStatus(gymId: String): Int {
        val active = gymVisitRepository.findByGymIdAndDurationNull(gymId)
        return active.size
    }

    fun getGymVisits(currentUser: User, gymId: String): List<GymVisitDto>? {
        val gym = findById(gymId)
        return gymVisitMapper.modelsToDtos(gym.visits)
    }



}