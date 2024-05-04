package org.gymapp.backend.repository

import org.gymapp.backend.model.GymClassInstance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional

@Repository
interface GymClassInstanceRepository : JpaRepository<GymClassInstance, String> {

    @Query("SELECT g FROM GymClassInstance g JOIN g.gymClass gc WHERE gc.id = :classId AND CAST(g.dateTime AS date) = :date")
    fun findByGymClassIdAndDateTime(@Param("classId") classId: String, @Param("date") dateTime: LocalDate): Optional<GymClassInstance>
}