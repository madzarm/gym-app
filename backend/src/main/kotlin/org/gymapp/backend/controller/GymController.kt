package org.gymapp.backend.controller

import org.gymapp.backend.common.Common
import org.gymapp.backend.model.GymMemberDtoFull
import org.gymapp.backend.service.GymService
import org.gymapp.library.request.CreateGymRequest
import org.gymapp.library.response.GymClassDto
import org.gymapp.library.response.GymDto
import org.gymapp.library.response.GymUserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/gyms")
class GymController(
    @Autowired private val gymService: GymService,
    @Autowired private val common: Common
) {

    @PostMapping
    fun createGym(
        @RequestBody request: CreateGymRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<GymUserDto> {
        return ResponseEntity.status(201).body(gymService.createGym(request, common.getCurrentUser(jwt)))
    }

    @GetMapping
    fun findUserGyms(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<List<GymDto>> {
        return ResponseEntity.ok(gymService.findUserGyms(common.getCurrentUser(jwt)))
    }

    @GetMapping("/{gymId}/classes")
    fun getGymClasses(
        @PathVariable gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<GymClassDto>> {
        return ResponseEntity.ok(gymService.getGymClasses(common.getCurrentUser(jwt), gymId))
    }

    @GetMapping("/members/{memberId}")
    fun getGymMemberFull(
        @PathVariable memberId: String,
    ): ResponseEntity<GymMemberDtoFull> {
        return ResponseEntity.ok(gymService.getGymMemberFull(memberId))
    }


}