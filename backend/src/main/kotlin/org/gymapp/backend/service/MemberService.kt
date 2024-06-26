package org.gymapp.backend.service

import jakarta.persistence.EntityManager
import org.gymapp.backend.common.Common
import org.gymapp.backend.extensions.addParticipant
import org.gymapp.backend.extensions.getGymCode
import org.gymapp.backend.extensions.getMember
import org.gymapp.backend.mapper.GymClassInstanceMapper
import org.gymapp.backend.mapper.GymClassMapper
import org.gymapp.backend.mapper.GymMemberMapper
import org.gymapp.backend.mapper.GymUserMapper
import org.gymapp.backend.mapper.GymVisitMapper
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.*
import org.gymapp.library.response.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

@Service
class MemberService(
    @Autowired private val gymService: GymService,
    @Autowired private val roleService: RoleService,
    @Autowired private val gymMemberMapper: GymMemberMapper,
    @Autowired private val gymMemberRepository: GymMemberRepository,
    @Autowired private val gymUserRepository: GymUserRepository,
    @Autowired private val gymUserMapper: GymUserMapper,
    @Autowired private val gymClassRepository: GymClassRepository,
    @Autowired private val gymVisitMapper: GymVisitMapper,
    @Autowired private val gymClassMapper: GymClassMapper,
    @Autowired private val gymVisitRepository: GymVisitRepository,
    @Autowired private val gymClassInstanceRepository: GymClassInstanceRepository,
    @Autowired private val gymClassInstanceMapper: GymClassInstanceMapper,
    @Autowired private val challengeService: ChallengeService,
    @Autowired private val stripeService: StripeService,
) {

    fun joinGymAsMember(currentUser: User, code: String): GymUserDto {
        val gym = gymService.findGymByCode(code)
        val customerId = stripeService.createStripeCustomer(currentUser.email ?: "", gym.stripeAccountId).id
        var gymUser = currentUser.getGymUser(gym.code)
        if (gymUser != null) {
            if (gymUser.hasRole(Common.Roles.ROLE_MEMBER.name)) {
                throw IllegalArgumentException("User is already a member of this gym")
            }

            gymUser.roles.add(roleService.findByName(Common.Roles.ROLE_MEMBER.name))
            gymUserRepository.save(gymUser)


            val member = GymMember(gymUser = gymUser, customerId = customerId)
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

        val member = GymMember(gymUser = gymUser, customerId = customerId)
        gymMemberRepository.save(member)
        return gymUserMapper.modelToDto(gymUser)
    }

    fun joinGymWithInvite(currentUser: User, code: String): GymUserDto {
        val invitingMember = gymMemberRepository.findByInviteCode(code) ?: throw IllegalArgumentException("Invite code is invalid")
        val result = joinGymAsMember(currentUser, invitingMember.getGymCode())
        challengeService.checkForInviteChallenge(invitingMember)
        return result
    }

    fun registerToClass(currentUser: User, classId: String, dateTimeString: String): GymMemberDto {
        val dateTime = LocalDateTime.parse(dateTimeString)

        val gymClass = gymClassRepository.findById(classId)
            .orElseThrow { throw IllegalArgumentException("Class with that id does not exist") }

        val member = gymClass.gym.getMember(currentUser) ?: throw IllegalArgumentException("User is not a member of this gym")

        val gymClassInstance =
            if (gymClass.instances.any {
                if (it.gymClassModifiedInstance != null) {
                    it.gymClassModifiedInstance.dateTime == dateTime
                } else {
                    it.dateTime == dateTime
                }
                }) {
                gymClass.instances.first {
                    if (it.gymClassModifiedInstance != null) {
                        it.gymClassModifiedInstance.dateTime == dateTime
                    } else {
                        it.dateTime == dateTime
                    }
                }
            } else {
                GymClassInstance(
                    dateTime = dateTime,
                    gymClass = gymClass
                )
            }


        if (gymClassInstance.participants.any { it.gymUser.user.id == currentUser.id }) {
            throw IllegalArgumentException("User is already registered to this class")
        }

        if (gymClassInstance.participants.size >= gymClass.maxParticipants) {
            throw IllegalArgumentException("Class is full")
        }

        gymClassInstance.addParticipant(member)
        gymClassInstanceRepository.save(gymClassInstance)
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

        challengeService.checkForChallenges(member, visit)
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

    fun getClassesForReview(currentUser: User, gymId: String): List<GymClassInstanceDto> {
        val member = currentUser.getMember(gymId)

        // TODO uncomment this when testing is finished
        // val finishedClasses = member.classes.filter { it.dateTime.plusMinutes(it.duration.toMinutes()).isBefore(LocalDateTime.now()) }
        val finishedClassesInstances = member.classes.filter { it.dateTime.isBefore(LocalDateTime.now()) }
        val nonReviewedFinishedClasses = finishedClassesInstances.filter { it.reviews.none { review -> review.member.id == member.id } }

        return gymClassInstanceMapper.instancesToDtos(nonReviewedFinishedClasses)
    }


}