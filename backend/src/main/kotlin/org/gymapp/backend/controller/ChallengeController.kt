package org.gymapp.backend.controller

import jakarta.websocket.server.PathParam
import org.gymapp.backend.common.Common
import org.gymapp.backend.service.ChallengeService
import org.gymapp.library.request.CreateFrequencyBasedChallengeRequest
import org.gymapp.library.request.CreateInviteFriendChallengeRequest
import org.gymapp.library.request.CreateTimedVisitBasedChallengeRequest
import org.gymapp.library.request.UpdateChallengeRequest
import org.gymapp.library.response.ChallengeDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @PostMapping("/invite-based")
    fun createInviteFriendChallenge(
        @RequestBody request: CreateInviteFriendChallengeRequest,
        @PathParam("gymId") gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Void> {
        challengeService.createInviteFriendChallenge(request, gymId, common.getCurrentUser(jwt))
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

    @PutMapping("/{challengeId}")
    fun updateChallenge(
        @PathVariable("challengeId") challengeId: String,
        @RequestBody request: UpdateChallengeRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Void> {
        challengeService.updateChallenge(challengeId, request, common.getCurrentUser(jwt))
        return ResponseEntity.status(204).build()
    }

    @GetMapping("/unclaimed")
    fun getUnclaimedChallenges(
        @PathParam("gymId") gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<ChallengeDto>> {
        return ResponseEntity.ok(challengeService.getUnclaimedChallenges(gymId, common.getCurrentUser(jwt)))
    }

    @PostMapping("/{challengeId}/claim")
    fun claimChallenge(
        @PathVariable("challengeId") challengeId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Void> {
        challengeService.claimChallenge(challengeId, common.getCurrentUser(jwt))
        return ResponseEntity.status(204).build()
    }

    @GetMapping("/points")
    fun getMemberPoints(
        @PathParam("gymId") gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Int> {
        return ResponseEntity.ok(challengeService.getMemberPoints(gymId, common.getCurrentUser(jwt)))
    }
}