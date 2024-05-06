package org.gymapp.backend.model;

import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeRepository : JpaRepository<Challenge, String> {
}