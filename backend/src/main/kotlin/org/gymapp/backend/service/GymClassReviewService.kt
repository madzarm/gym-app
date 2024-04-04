package org.gymapp.backend.service

import org.gymapp.backend.mapper.GymClassReviewMapper
import org.gymapp.backend.model.GymClassReview
import org.gymapp.backend.model.GymClassReviewDto
import org.gymapp.backend.model.User
import org.gymapp.backend.repository.GymClassReviewRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GymClassReviewService(
    @Autowired private val gymClassService: GymClassService,
    @Autowired private val gymClassReviewRepository: GymClassReviewRepository,
    @Autowired private val gymClassReviewMapper: GymClassReviewMapper,
) {

    fun reviewClass(user: User, classId: String, review: String): GymClassReviewDto {
        val gymClass = gymClassService.findById(classId)
        val member = user.getMember(gymClass.gym.id)

        val gymReview = GymClassReview(
            review = review,
            gymClass = gymClass,
            member = member
        )
        gymClassReviewRepository.save(gymReview)
        return gymClassReviewMapper.modelToDto(gymReview)
    }

}