package org.gymapp.backend.service

import jakarta.persistence.EntityManager
import org.gymapp.backend.common.Common
import org.gymapp.backend.extensions.addParticipant
import org.gymapp.backend.extensions.getMember
import org.gymapp.backend.mapper.GymMemberMapper
import org.gymapp.backend.mapper.GymUserMapper
import org.gymapp.backend.mapper.GymVisitMapper
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.*
import org.gymapp.library.response.GymMemberDto
import org.gymapp.library.response.GymUserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@Service
class MemberService(
    @Autowired private val gymService: GymService,
    @Autowired private val roleService: RoleService,
    @Autowired private val gymRepository: GymRepository,
    @Autowired private val gymMemberMapper: GymMemberMapper,
    @Autowired private val gymMemberRepository: GymMemberRepository,
    @Autowired private val gymUserRepository: GymUserRepository,
    @Autowired private val gymUserMapper: GymUserMapper,
    @Autowired private val entityManager: EntityManager, private val gymClassRepository: GymClassRepository,
    @Autowired private val gymVisitMapper: GymVisitMapper,
    @Autowired private val gymVisitRepository: GymVisitRepository,
) {

    fun joinGymAsMember(currentUser: User, code: String): GymUserDto {
        val gym = gymService.findGymByCode(code)

        var gymUser = currentUser.getGymUser(gym.code)
        if (gymUser != null) {
            if (gymUser.hasRole(Common.Roles.ROLE_MEMBER.name)) {
                throw IllegalArgumentException("User is already a member of this gym")
            }

            gymUser.roles.add(roleService.findByName(Common.Roles.ROLE_MEMBER.name))
            gymUserRepository.save(gymUser)
            val member = GymMember(gymUser = gymUser)
            gymUser.gymMember = member
            gymMemberRepository.save(member)
            return gymUserMapper.modelToDto(gymUser)
        }

        gymUser = GymUser(
            id = UUID.randomUUID().toString(),
            user = currentUser,
            gym = gym,
            roles = mutableListOf(roleService.findByName(Common.Roles.ROLE_MEMBER.name))
        )
        gymUserRepository.save(gymUser)

        val member = GymMember(gymUser = gymUser)
        gymUser.gymMember = member
        gymMemberRepository.save(member)
        return gymUserMapper.modelToDto(gymUser)
    }

    fun registerToClass(currentUser: User, classId: String): GymMemberDto {
        val gymClass = gymClassRepository.findById(classId)
            .orElseThrow { throw IllegalArgumentException("Class with that id does not exist") }

        val member = gymClass.gym.getMember(currentUser) ?: throw IllegalArgumentException("User is not a member of this gym")

        if (gymClass.participants.any { it.gymUser.user.id == currentUser.id }) {
            throw IllegalArgumentException("User is already registered to this class")
        }

        if (gymClass.participants.size >= gymClass.maxParticipants) {
            throw IllegalArgumentException("Class is full")
        }


        gymClass.addParticipant(member)
        gymClassRepository.save(gymClass)
        return gymMemberMapper.modelToDto(member)
    }

    fun getMember(currentUser: User, gymId: String): GymMemberDto {
        val member = currentUser.getMember(gymId)
        return gymMemberMapper.modelToDto(member)
    }

    fun visitGym(currentUser: User, gymId: String): GymVisitDto? {
        val gym = gymService.findById(gymId)
        val member = currentUser.getMember(gymId)

        val visit = GymVisit(
            id = UUID.randomUUID().toString(),
            gym = gym,
            gymMember = member
        )

        gymVisitRepository.save(visit)
        return gymVisitMapper.modelToDto(visit)
    }

    fun leaveGym(currentUser: User, gymId: String): GymVisitDto? {
        val member = currentUser.getMember(gymId)

        val visit: GymVisit = gymVisitRepository.findByGymIdAndGymMemberIdAndDurationNull(gymId, member.id)
            .firstOrNull() ?: throw IllegalArgumentException("User is not in the gym")

        visit.duration = Duration.between(visit.date, LocalDateTime.now())
        gymVisitRepository.save(visit)
        return gymVisitMapper.modelToDto(visit)
    }


}