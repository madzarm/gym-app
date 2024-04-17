package org.gymapp.backend.repository

import org.gymapp.backend.model.GymClassModifiedInstance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GymClassModifiedInstanceRepository : JpaRepository<GymClassModifiedInstance, String>{
}