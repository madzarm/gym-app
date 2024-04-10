package org.gymapp.backend.service

import org.gymapp.backend.mapper.GymClassReviewMapper
import org.gymapp.backend.mapper.GymTrainerReviewMapper
import org.gymapp.backend.model.*
import org.gymapp.backend.repository.GymClassReviewRepository
import org.gymapp.backend.repository.GymTrainerReviewRepository
import org.gymapp.library.request.ReviewGymClassRequest
import org.gymapp.library.request.ReviewTrainerRequest
import org.gymapp.library.response.GymClassReviewDto
import org.gymapp.library.response.GymTrainerReviewDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReviewService(
    @Autowired private val gymClassService: GymClassService,
    @Autowired private val gymClassReviewRepository: GymClassReviewRepository,
    @Autowired private val gymClassReviewMapper: GymClassReviewMapper,
    @Autowired private val gymTrainerService: TrainerService,
    @Autowired private val gymTrainerReviewRepository: GymTrainerReviewRepository,
    @Autowired private val gymTrainerReviewMapper: GymTrainerReviewMapper
) {

    fun reviewClass(user: User, request: ReviewGymClassRequest): GymClassReviewDto {
        val gymClass = gymClassService.findById(request.classId)
        val member = user.getMember(gymClass.gym.id)

        val classReview = GymClassReview(
            review = request.review,
            rating = request.rating,
            gymClass = gymClass,
            member = member
        )

        gymClassReviewRepository.save(classReview)
        return gymClassReviewMapper.modelToDto(classReview)
    }

    fun reviewTrainer(user: User, request: ReviewTrainerRequest): GymTrainerReviewDto {
        val trainer: GymTrainer = gymTrainerService.findTrainerById(request.trainerId)
        val member = user.getMember(trainer.gymUser.gym?.id ?: "")

        val trainerReview = GymTrainerReview(
            review = request.review,
            rating = request.rating,
            trainer = trainer,
            member = member
        )

        gymTrainerReviewRepository.save(trainerReview)
        return gymTrainerReviewMapper.modelToDto(trainerReview)
    }

}