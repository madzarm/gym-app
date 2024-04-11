package org.gymapp.backend.service

import org.gymapp.backend.mapper.GymTrainerMapper
import org.gymapp.backend.model.GymTrainer
import org.gymapp.backend.model.User
import org.gymapp.backend.repository.GymRepository
import org.gymapp.library.response.AccessCodeDto
import org.gymapp.library.response.GymTrainerWithReviewsDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OwnerService(
    @Autowired private val gymRepository: GymRepository,
    @Autowired private val accessCodeService: AccessCodeService,
    @Autowired private val gymTrainerMapper: GymTrainerMapper,
) {

    fun generateAccessCode(gymId: String, user: User): AccessCodeDto {
        val gym = gymRepository.findById(gymId).get()
        if (gym.owner?.gymUser?.user?.id != user.id) {
            throw IllegalArgumentException("User is not the owner of the gym")
        }
        val accessCodeDto = accessCodeService.generateAccessCodeDto(gym)
        return accessCodeDto
    }

    fun getTrainersWithReviews(gymId: String, currentUser: User): List<GymTrainerWithReviewsDto>? {
        val gym = gymRepository.findById(gymId).get()
        if (gym.owner?.gymUser?.user?.id != currentUser.id) {
            throw IllegalArgumentException("User is not the owner of the gym")
        }

        val gymTrainers = gym.gymUsers.filter { it.gymTrainer != null }.map { it.gymTrainer!! }
        return gymTrainerMapper.modelsToDtosWithReviews(gymTrainers)
    }
}