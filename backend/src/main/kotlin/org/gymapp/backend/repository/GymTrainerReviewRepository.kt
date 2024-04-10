package org.gymapp.backend.repository;

import org.gymapp.backend.model.GymTrainerReview
import org.springframework.data.jpa.repository.JpaRepository

interface GymTrainerReviewRepository : JpaRepository<GymTrainerReview, String> {
}