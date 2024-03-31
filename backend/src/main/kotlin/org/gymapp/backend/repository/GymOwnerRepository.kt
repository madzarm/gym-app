package org.gymapp.backend.repository;

import org.gymapp.backend.model.GymOwner
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GymOwnerRepository : JpaRepository<GymOwner, String> {
}