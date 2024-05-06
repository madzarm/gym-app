package org.gymapp.backend.controller

import jakarta.websocket.server.PathParam
import org.gymapp.backend.common.Common
import org.gymapp.backend.model.ChallengeDto
import org.gymapp.backend.model.CreateFrequencyBasedChallengeRequest
import org.gymapp.backend.model.CreateTimedVisitBasedChallengeRequest
import org.gymapp.backend.service.ChallengeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/challenges")
class ChallengeController (
    @Autowired private val challengeService: ChallengeService,
    @Autowired private val common: Common
) {


    @PostMapping("/timed-visit-based")
    fun createTimedVisitBasedChallenge(
        @RequestBody request: CreateTimedVisitBasedChallengeRequest,
        @PathParam("gymId") gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Void> {
        challengeService.createTimedVisitBasedChallenge(request, gymId, common.getCurrentUser(jwt))
        return ResponseEntity.status(201).build()
    }

    @PostMapping("/frequency-based")
    fun createFrequencyBasedChallenge(
        @RequestBody request: CreateFrequencyBasedChallengeRequest,
        @PathParam("gymId") gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Void> {
        challengeService.createFrequencyBasedChallenge(request, gymId, common.getCurrentUser(jwt))
        return ResponseEntity.status(201).build()
    }

    @DeleteMapping("/{challengeId}")
    fun deleteChallenge(
        @PathVariable("challengeId") challengeId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Void> {
        challengeService.deleteChallenge(challengeId, common.getCurrentUser(jwt))
        return ResponseEntity.status(204).build()
    }

    @GetMapping
    fun getAllActiveChallenges(
        @PathParam("gymId") gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<ChallengeDto>> {
        return ResponseEntity.ok(challengeService.getAllActiveChallenges(gymId, common.getCurrentUser(jwt)))
    }
}