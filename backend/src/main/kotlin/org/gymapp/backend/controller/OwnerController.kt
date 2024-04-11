package org.gymapp.backend.controller

import org.gymapp.backend.common.Common
import org.gymapp.backend.service.GymService
import org.gymapp.backend.service.OwnerService
import org.gymapp.library.response.AccessCodeDto
import org.gymapp.library.response.GymTrainerDto
import org.gymapp.library.response.GymTrainerWithReviewsDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/owners")
class OwnerController (
    @Autowired private val ownerService: OwnerService,
    @Autowired private val common: Common,
){

    @GetMapping("/gyms/{gymId}/accessCode")
    fun generateAccessCode(
        @PathVariable gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<AccessCodeDto> {
        return ResponseEntity.ok(ownerService.generateAccessCode(gymId, common.getCurrentUser(jwt)))
    }

    @GetMapping("/gyms/{gymId}/trainers")
    fun getTrainersWithReviews(
        @PathVariable gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<GymTrainerWithReviewsDto>> {
        return ResponseEntity.ok(ownerService.getTrainersWithReviews(gymId, common.getCurrentUser(jwt)))
    }
}