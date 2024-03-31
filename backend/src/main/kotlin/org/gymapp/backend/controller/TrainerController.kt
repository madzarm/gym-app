package org.gymapp.backend.controller

import jakarta.websocket.server.PathParam
import org.gymapp.backend.common.Common
import org.gymapp.backend.service.TrainerService
import org.gymapp.library.response.GymTrainerDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}