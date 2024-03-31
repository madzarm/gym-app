package org.gymapp.backend.controller

import org.gymapp.backend.common.Common
import org.gymapp.backend.model.AccessCodeDto
import org.gymapp.backend.service.GymService
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
    @Autowired private val gymService: GymService,
    @Autowired private val common: Common,
){

    @GetMapping("/gyms/{gymId}/accessCode")
    fun generateAccessCode(
        @PathVariable gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<AccessCodeDto> {
        return ResponseEntity.ok(gymService.generateAccessCode(gymId, common.getCurrentUser(jwt)))
    }
}