package org.gymapp.backend.controller

import org.gymapp.backend.common.Common
import org.gymapp.backend.service.UserService
import org.gymapp.library.request.CreateUserRequest
import org.gymapp.library.response.ActionResponse
import org.gymapp.library.response.GymDto
import org.gymapp.library.response.GymUserDto
import org.gymapp.library.response.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    @Autowired private val userService: UserService,
    @Autowired private val common: Common
) {

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest, @AuthenticationPrincipal jwt: Jwt): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.createUser(request, jwt))
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        return ResponseEntity.ok(userService.getAllUsers())
    }

    @GetMapping("/gyms")
    fun getGyms(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<List<GymUserDto>> {
        return ResponseEntity.ok(userService.getGymUsers(common.getCurrentUser(jwt)))
    }

    @GetMapping("/current")
    fun getCurrentUser(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.getCurrentUser(common.getCurrentUser(jwt)))
    }

    @PostMapping("/join-as-member")
    fun joinGymAsMember(@AuthenticationPrincipal jwt: Jwt, @RequestParam code: String): ResponseEntity<GymUserDto> {

        return ResponseEntity.ok(userService.joinGymAsMember(common.getCurrentUser(jwt), code))
    }

}