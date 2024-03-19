package org.gymapp.backend.controller

import org.gymapp.backend.service.CreateGymRequest
import org.gymapp.backend.service.GymService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/gym")
class GymController(
    @Autowired private val gymService: GymService
) {

    @PostMapping
    fun createGym(
        @RequestBody request: CreateGymRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<*> {
        return ResponseEntity.status(201).body(gymService.createGym(request, jwt))
    }

}