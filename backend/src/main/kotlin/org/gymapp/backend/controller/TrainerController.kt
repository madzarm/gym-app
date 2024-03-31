package org.gymapp.backend.controller

import jakarta.websocket.server.PathParam
import org.gymapp.backend.common.Common
import org.gymapp.backend.model.CreateClassRequest
import org.gymapp.backend.service.TrainerService
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

    @PostMapping("/gyms/{gymId}/classes")
    fun createClass(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable("gymId") gymId: String,
        @RequestBody request: CreateClassRequest
    ): GymTrainerDto {
        return trainerService.createClass(common.getCurrentUser(jwt), gymId, request)
    }
}