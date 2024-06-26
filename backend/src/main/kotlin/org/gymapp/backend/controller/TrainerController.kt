package org.gymapp.backend.controller

import jakarta.websocket.server.PathParam
import org.gymapp.backend.common.Common
import org.gymapp.backend.service.TrainerService
import org.gymapp.library.request.CreateClassRequest
import org.gymapp.library.request.CreateRecurringClassRequest
import org.gymapp.library.request.UpdateClassRequest
import org.gymapp.library.request.UpdateGymClassInstanceRequest
import org.gymapp.library.response.GymTrainerDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/trainers")
class TrainerController(
    @Autowired private val trainerService: TrainerService,
    @Autowired private val common: Common
) {

    @GetMapping("/gyms/{gymId}")
    fun getCurrentTrainer(@AuthenticationPrincipal jwt: Jwt, @PathVariable("gymId") gymId: String): GymTrainerDto {
        return trainerService.getTrainer(common.getCurrentUser(jwt), gymId)
    }

    @GetMapping("/gyms/{gymId}/upcoming-classes")
    fun getTrainerWithUpcomingClasses(@AuthenticationPrincipal jwt: Jwt, @PathVariable("gymId") gymId: String): GymTrainerDto {
        return trainerService.getTrainerWithUpcomingClasses(common.getCurrentUser(jwt), gymId)
    }

    @PostMapping("/gyms/{gymId}/classes")
    fun createClassInstance(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable("gymId") gymId: String,
        @RequestBody request: CreateClassRequest
    ): GymTrainerDto {
        return trainerService.createClassInstance(common.getCurrentUser(jwt), gymId, request)
    }

    @PostMapping("/gyms/{gymId}/classes/recurring")
    fun createRecurringClass(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable("gymId") gymId: String,
        @RequestBody request: CreateRecurringClassRequest
    ): GymTrainerDto {
        return trainerService.createRecurringClass(common.getCurrentUser(jwt), gymId, request)
    }

    @PutMapping("/gyms/classes/{classId}/instances")
    fun modifyRecurringClassInstance(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable("classId") classId: String,
        @RequestBody request: UpdateGymClassInstanceRequest
    ): GymTrainerDto {
        return trainerService.updateGymClassInstance(common.getCurrentUser(jwt), classId, request)
    }

    @PutMapping("/gyms/classes/{classId}")
    fun updateClass(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable("classId") classId: String,
        @RequestBody request: UpdateClassRequest
    ): GymTrainerDto {
        return trainerService.updateClass(common.getCurrentUser(jwt), classId, request)
    }

    @DeleteMapping("/gyms/classes/{classId}")
    fun deleteClass(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable("classId") classId: String,
    ): GymTrainerDto {
        return trainerService.deleteClass(common.getCurrentUser(jwt), classId)
    }

    @DeleteMapping("/gyms/classes/{classId}/cancel")
    fun cancelClass(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable("classId") classId: String,
        @PathParam("dateTime") dateTime: String,
    ): GymTrainerDto {
        return trainerService.cancelClass(common.getCurrentUser(jwt), classId, dateTime)
    }
}