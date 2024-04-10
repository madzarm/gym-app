package org.gymapp.backend.controller

import jakarta.websocket.server.PathParam
import org.gymapp.backend.common.Common
import org.gymapp.backend.mapper.GymVisitMapper
import org.gymapp.backend.model.GymVisit
import org.gymapp.backend.model.GymVisitDto
import org.gymapp.backend.repository.GymRepository
import org.gymapp.backend.repository.GymVisitRepository
import org.gymapp.backend.service.GymService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.math.roundToInt

@RestController
@RequestMapping("/test")
class TestController (
    @Autowired private val gymVisitMapper: GymVisitMapper,
    @Autowired private val common: Common,
    @Autowired private val gymService: GymService, private val gymVisitRepository: GymVisitRepository,
) {

    val probabilities: Map<Int, Float> = mapOf(
        7 to 0.015f,
        8 to 0.03f,
        9 to 0.03f,
        10 to 0.03f,
        11 to 0.03f,
        12 to 0.0375f,
        13 to 0.045f,
        14 to 0.045f,
        15 to 0.06f,
        16 to 0.075f,
        17 to 0.105f,
        18 to 0.135f,
        19 to 0.135f,
        20 to 0.120f,
        21 to 0.09f,
        22 to 0.06f,
        23 to 0.4f,
    )

    @GetMapping("/generateRandomVisits")
    fun generateRandomVisits (
        @PathParam("gymId") gymId: String,
    ): ResponseEntity<List<GymVisitDto>>{
        return ResponseEntity.ok(generateRandomVisits(2000, gymId).map { gymVisitMapper.modelToDto(it) })
    }


    fun generateRandomVisits(n: Int, gymId: String): List<GymVisit> {
        val probabilities: Map<Int, Float> = mapOf(
            7 to 0.015f, 8 to 0.03f, 9 to 0.03f, 10 to 0.03f, 11 to 0.03f, 12 to 0.0375f,
            13 to 0.045f, 14 to 0.045f, 15 to 0.06f, 16 to 0.075f, 17 to 0.105f,
            18 to 0.135f, 19 to 0.135f, 20 to 0.120f, 21 to 0.09f, 22 to 0.06f
        )

        val dayProbabilities = mapOf(
            1 to 0.14f, // Monday
            2 to 0.14f, // Tuesday
            3 to 0.14f, // Wednesday
            4 to 0.14f, // Thursday
            5 to 0.14f, // Friday
            6 to 0.20f, // Saturday
            7 to 0.10f  // Sunday
        )


        val gym = gymService.findById(gymId)
        val gymMembers = gym.gymUsers.map { it.gymMember }

        if (gymMembers.isEmpty()) {
            throw IllegalStateException("No gym members found for gym ID: $gymId")
        }

        val cumulativeProbabilities = mutableListOf<Pair<Int, Float>>()
        var cumSum = 0f
        for ((time, prob) in probabilities) {
            cumSum += prob
            cumulativeProbabilities.add(time to cumSum)
        }

        val cumulativeDayProbabilities = mutableListOf<Pair<Int, Float>>()
        var dayCumSum = 0f
        dayProbabilities.forEach { (day, prob) ->
            dayCumSum += prob
            cumulativeDayProbabilities.add(day to dayCumSum)
        }

        val gymVisits = mutableListOf<GymVisit>()
        val random = Random()

        val meanDuration = (40 + 110) / 2.0
        val stdDeviation = 15

        repeat(n) {
            val dayRand = random.nextFloat()
            val chosenDayOfWeek = cumulativeDayProbabilities.first { it.second >= dayRand }.first
            val chosenDate = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - chosenDayOfWeek + 7) % 7L)


            val r = random.nextFloat()
            val hour = cumulativeProbabilities.first { it.second >= r }.first

            var durationMinutes = (random.nextGaussian() * stdDeviation + meanDuration).roundToInt()
            durationMinutes = durationMinutes.coerceIn(40, 110)

            val visitTime = LocalDateTime.of(chosenDate, LocalTime.of(hour, 0))

            val gymMember = gymMembers[random.nextInt(gymMembers.size)]

            gymVisits.add(GymVisit(date = visitTime, gym = gym, gymMember = gymMember, duration = Duration.ofMinutes(durationMinutes.toLong())))
        }

        gymVisitRepository.saveAll(gymVisits)


        return gymVisits
    }
}