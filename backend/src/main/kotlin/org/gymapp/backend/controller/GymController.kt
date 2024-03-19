package org.gymapp.backend.controller

import org.gymapp.backend.common.Common
import org.gymapp.backend.service.GymService
import org.gymapp.library.request.CreateGymRequest
import org.gymapp.library.response.GymDto
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
    ): ResponseEntity<GymDto> {
        return ResponseEntity.status(201).body(gymService.createGym(request, jwt))
    }

    @GetMapping
    fun findUserGyms(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<List<GymDto>> {
        return ResponseEntity.ok(gymService.findUserGyms(common.getCurrentUser(jwt)))
    }

}