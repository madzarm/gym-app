package org.gymapp.backend.controller

import org.gymapp.backend.common.Common
import org.gymapp.backend.model.TrainerDto
import org.gymapp.backend.service.TrainerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/trainers")
class TrainerController (
    @Autowired private val trainerService: TrainerService,
    @Autowired private val common: Common
){


//    @GetMapping
//    fun getCurrentTrainer(@AuthenticationPrincipal jwt: Jwt, gymId: String): ResponseEntity<TrainerDto> {
//        return ResponseEntity.ok(trainerService.getCurrentTrainer(common.getCurrentUser(jwt), gymId))
//    }
}