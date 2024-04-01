package org.gymapp.backend.controller

import org.gymapp.backend.common.Common
import org.gymapp.backend.model.GymVisitDto
import org.gymapp.backend.service.MemberService
import org.gymapp.library.response.GymMemberDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/members")
class MemberController (
    @Autowired private val memberService: MemberService,
    @Autowired private val common: Common,
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

    @PostMapping("/gyms/{gymId}/gym-visit")
    fun visitGym(
        @PathVariable gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<GymVisitDto> {
        return ResponseEntity.ok().body(memberService.visitGym(common.getCurrentUser(jwt), gymId))
    }

}