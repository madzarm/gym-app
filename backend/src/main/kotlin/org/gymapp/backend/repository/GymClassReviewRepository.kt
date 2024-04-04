package org.gymapp.backend.repository

import org.gymapp.backend.model.GymClassReview
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GymClassReviewRepository: JpaRepository<GymClassReview, String>{
}