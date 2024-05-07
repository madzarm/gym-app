package org.gymapp.backend.service

import jakarta.persistence.EntityNotFoundException
import org.gymapp.backend.extensions.getActiveChallenges
import org.gymapp.backend.extensions.toLocalDateTime
import org.gymapp.backend.extensions.toLocalTime
import org.gymapp.backend.mapper.ChallengeMapper
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.ChallengeRepository
import org.gymapp.backend.repository.FrequencyBasedCriteriaRepository
import org.gymapp.backend.repository.TimeBasedCriteriaRepository
import org.gymapp.library.request.CreateFrequencyBasedChallengeRequest
import org.gymapp.library.request.CreateTimedVisitBasedChallengeRequest
import org.gymapp.library.request.UpdateChallengeRequest
import org.gymapp.library.response.ChallengeDto
import org.gymapp.library.response.ChallengeType
import org.gymapp.library.response.CriteriaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class ChallengeService (
    @Autowired private val gymService: GymService,
    @Autowired private val timeBasedCriteriaRepository: TimeBasedCriteriaRepository,
    @Autowired private val challengeRepository: ChallengeRepository,
    @Autowired private val frequencyBasedCriteriaRepository: FrequencyBasedCriteriaRepository,
    @Autowired private val challengeMapper: ChallengeMapper,
) {


    fun createTimedVisitBasedChallenge(request: CreateTimedVisitBasedChallengeRequest, gymId: String, user: User) {
        val gym = gymService.findById(gymId)

        val criteria = ChallengeCriteria(
            id = UUID.randomUUID().toString(),
            type = CriteriaType.TIMED_VISIT_BASED
        )

        val timeBasedCriteria = TimeBasedCriteria(
            id = UUID.randomUUID().toString(),
            startTime = request.startTime.toLocalTime(),
            endTime = request.endTime.toLocalTime(),
            baseCriteria = criteria
        )

        val challenge = Challenge(
            id = UUID.randomUUID().toString(),
            name = request.name,
            description = request.description,
            expiryDate = request.expiryDate.toLocalDateTime(),
            pointsValue = request.pointsValue,
            isDeleted = false,
            type = ChallengeType.TIMED_VISIT_BASED,
            createdAt = LocalDateTime.now(),
            criteria = criteria,
            gym = gym,
        )

        criteria.challenge = challenge
        gym.challenges.add(challenge)

        challengeRepository.save(challenge)
        timeBasedCriteriaRepository.save(timeBasedCriteria)
    }

    fun createFrequencyBasedChallenge(request: CreateFrequencyBasedChallengeRequest, gymId: String, user: User) {
        val gym = gymService.findById(gymId)

        val criteria = ChallengeCriteria(
            id = UUID.randomUUID().toString(),
            type = CriteriaType.FREQUENCY_BASED
        )

        val frequencyBasedCriteria = FrequencyBasedCriteria(
            id = UUID.randomUUID().toString(),
            frequencyCount = request.frequencyCount,
            baseCriteria = criteria
        )

        val challenge = Challenge(
            id = UUID.randomUUID().toString(),
            name = request.name,
            description = request.description,
            expiryDate = request.expiryDate.toLocalDateTime(),
            pointsValue = request.pointsValue,
            isDeleted = false,
            type = ChallengeType.FREQUENCY_BASED,
            createdAt = LocalDateTime.now(),
            criteria = criteria,
            gym = gym,
        )

        criteria.challenge = challenge
        gym.challenges.add(challenge)

        challengeRepository.save(challenge)
        frequencyBasedCriteriaRepository.save(frequencyBasedCriteria)
    }

    fun deleteChallenge(challengeId: String, user: User) {
        val challenge = findById(challengeId)
        challenge.isDeleted = true
        challengeRepository.save(challenge)
    }

    fun getAllActiveChallenges(gymId: String, user: User): List<ChallengeDto> {
        val gym = gymService.findById(gymId)
        val challenges = gym.getActiveChallenges()
        val challengeDtos = challengeMapper.modelsToDtos(challenges)
        return challengeDtos
    }

    @Transactional
    fun updateChallenge(challengeId: String, request: UpdateChallengeRequest, user: User) {
        val challenge = findById(challengeId)

        request.name?.let { challenge.name = it }
        request.description?.let { challenge.description = it }
        request.expiryDate?.let { challenge.expiryDate = it.toLocalDateTime() }
        request.pointsValue?.let { challenge.pointsValue = it }

        val baseCriteriaId = challenge.criteria.id
        when (challenge.type) {
            ChallengeType.TIMED_VISIT_BASED -> {
                val timeBasedCriteria = timeBasedCriteriaRepository.findByBaseCriteriaId(baseCriteriaId).orElseThrow()
                request.startTimeCriteria?.let { timeBasedCriteria.startTime = it.toLocalTime() }
                request.endTimeCriteria?.let { timeBasedCriteria.endTime = it.toLocalTime() }
                timeBasedCriteriaRepository.save(timeBasedCriteria)
            }
            ChallengeType.FREQUENCY_BASED -> {
                val frequencyBasedCriteria = frequencyBasedCriteriaRepository.findByBaseCriteriaId(baseCriteriaId).orElseThrow()
                request.frequencyCount?.let { frequencyBasedCriteria.frequencyCount = it }
                frequencyBasedCriteriaRepository.save(frequencyBasedCriteria)
            }
        }

        challengeRepository.save(challenge)
    }


    fun findById(challengeId: String): Challenge {
        return challengeRepository.findById(challengeId)
            .orElseThrow { throw EntityNotFoundException("Challenge not found") }
    }
}