package org.gymapp.backend.controller

import jakarta.websocket.server.PathParam
import org.gymapp.backend.common.Common
import org.gymapp.backend.model.CreateTimedVisitBasedChallengeRequest
import org.gymapp.backend.service.ChallengeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
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


    @PostMapping
    fun createTimedVisitBasedChallenge(
        @RequestBody request: CreateTimedVisitBasedChallengeRequest,
        @PathParam("gymId") gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Void> {
        challengeService.createTimedVisitBasedChallenge(request, gymId, common.getCurrentUser(jwt))
        return ResponseEntity.status(201).build()
    }
}