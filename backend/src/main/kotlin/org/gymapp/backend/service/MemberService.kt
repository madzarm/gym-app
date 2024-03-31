package org.gymapp.backend.service

import jakarta.persistence.EntityManager
import org.gymapp.backend.common.Common
import org.gymapp.backend.extensions.addParticipant
import org.gymapp.backend.extensions.getMember
import org.gymapp.backend.mapper.GymMemberMapper
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.GymClassRepository
import org.gymapp.backend.repository.GymRepository
import org.gymapp.backend.repository.GymUserRepository
import org.gymapp.backend.repository.GymMemberRepository
import org.gymapp.library.response.GymMemberDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class MemberService(
    @Autowired private val gymService: GymService,
    @Autowired private val roleService: RoleService,
    @Autowired private val gymRepository: GymRepository,
    @Autowired private val gymMemberMapper: GymMemberMapper,
    @Autowired private val gymMemberRepository: GymMemberRepository,
    @Autowired private val gymUserRepository: GymUserRepository,
    @Autowired private val entityManager: EntityManager, private val gymClassRepository: GymClassRepository,
) {

    fun joinGymAsMember(currentUser: User, code: String): GymMemberDto {
        val gym = gymService.findGymByCode(code)

        var gymUser = currentUser.getGymUser(gym.code)
        if (gymUser != null) {
            if (gymUser.hasRole(Common.Roles.ROLE_MEMBER.name)) {
                throw IllegalArgumentException("User is already a member of this gym")
            }

            gymUser.roles.add(roleService.findByName(Common.Roles.ROLE_MEMBER.name))
            gymUserRepository.save(gymUser)
            val member = GymMember(gymUser = gymUser)
            gymMemberRepository.save(member)
            return gymMemberMapper.modelToDto(member)
        }

        gymUser = GymUser(
            id = UUID.randomUUID().toString(),
            user = currentUser,
            gym = gym,
            roles = mutableListOf(roleService.findByName(Common.Roles.ROLE_MEMBER.name))
        )
        gymUserRepository.save(gymUser)

        val member = GymMember(gymUser = gymUser)
        gymMemberRepository.save(member)
        return gymMemberMapper.modelToDto(member)
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



}