package org.gymapp.backend.repository;

import org.gymapp.backend.model.GymClass
import org.springframework.data.jpa.repository.JpaRepository

interface GymClassRepository : JpaRepository<GymClass, String> {
}