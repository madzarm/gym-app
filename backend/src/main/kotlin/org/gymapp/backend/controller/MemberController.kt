package org.gymapp.backend.controller

import org.gymapp.backend.common.Common
import org.gymapp.backend.model.*
import org.gymapp.backend.service.ReviewService
import org.gymapp.backend.service.MemberService
import org.gymapp.library.request.ReviewGymClassRequest
import org.gymapp.library.request.ReviewTrainerRequest
import org.gymapp.library.response.GymClassReviewDto
import org.gymapp.library.response.GymMemberDto
import org.gymapp.library.response.GymTrainerReviewDto
import org.gymapp.library.response.GymVisitDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/members")
class MemberController (
    @Autowired private val memberService: MemberService,
    @Autowired private val common: Common,
    @Autowired private val reviewService: ReviewService,
){

    @PostMapping("/classes/{classId}")
    fun registerToClass(
        @PathVariable classId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<GymMemberDto> {
        return ResponseEntity.ok().body(memberService.registerToClass(common.getCurrentUser(jwt), classId))
    }

    @GetMapping("/gyms/{gymId}")
    fun getMemberByGym(
        @PathVariable gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<GymMemberDto> {
        return ResponseEntity.ok().body(memberService.getMember(common.getCurrentUser(jwt), gymId))
    }

    @PostMapping("/gyms/{gymId}/visit")
    fun visitGym(
        @PathVariable gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<GymVisitDto> {
        return ResponseEntity.ok().body(memberService.visitGym(common.getCurrentUser(jwt), gymId))
    }

    @PutMapping("/gyms/{gymId}/leave")
    fun leaveGym(
        @PathVariable gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<GymVisitDto> {
        return ResponseEntity.ok().body(memberService.leaveGym(common.getCurrentUser(jwt), gymId))
    }

    @PostMapping("/gyms/review-class")
    fun reviewGymClass(
        @RequestBody request: ReviewGymClassRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<GymClassReviewDto> {
        return ResponseEntity.ok().body(reviewService.reviewClass(common.getCurrentUser(jwt), request))
    }

    @PostMapping("/gyms/review-trainer")
    fun reviewTrainer(
        @RequestBody request: ReviewTrainerRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<GymTrainerReviewDto> {
        return ResponseEntity.ok().body(reviewService.reviewTrainer(common.getCurrentUser(jwt), request))
    }

}