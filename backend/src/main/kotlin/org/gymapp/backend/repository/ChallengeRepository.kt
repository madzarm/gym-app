package org.gymapp.backend.repository;

import org.gymapp.backend.model.Challenge
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChallengeRepository : JpaRepository<Challenge, String> {
}