package org.gymapp.backend.service

import jakarta.persistence.EntityManager
import org.gymapp.backend.common.Common
import org.gymapp.backend.mapper.GymMemberMapper
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.GymRepository
import org.gymapp.backend.repository.GymUserRepository
import org.gymapp.backend.repository.GymMemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class MemberService (
    @Autowired private val gymService: GymService,
    @Autowired private val roleService: RoleService,
    @Autowired private val gymRepository: GymRepository,
    @Autowired private val gymMemberMapper: GymMemberMapper,
    @Autowired private val gymMemberRepository: GymMemberRepository,
    @Autowired private val gymUserRepository: GymUserRepository,
    @Autowired private val entityManager: EntityManager,
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

}