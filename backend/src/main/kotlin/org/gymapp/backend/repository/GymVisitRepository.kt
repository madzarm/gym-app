package org.gymapp.backend.repository

import org.gymapp.backend.model.GymVisit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GymVisitRepository : JpaRepository<GymVisit, String>  {

    fun findByGymIdAndDurationNull(gymId: String): List<GymVisit>
    fun findByGymIdAndGymMemberIdAndDurationNull(gymId: String, id: String?): List<GymVisit>

    @Query(value = """
        SELECT DAYOFWEEK(date) as dayOfWeek, HOUR(date) as hour, COUNT(*) as visitCount
        FROM gym_visit
        WHERE gym_id = :gymId
        GROUP BY DAYOFWEEK(date), HOUR(date)
        ORDER BY DAYOFWEEK(date), HOUR(date)
    """, nativeQuery = true)
    fun findVisitsGroupedByDayAndHour(@Param("gymId") gymId: String): List<VisitCountByDayHour>

}

interface VisitCountByDayHour {
    fun getDayOfWeek(): Int
    fun getHour(): Int
    fun getVisitCount(): Long
}