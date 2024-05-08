package org.gymapp.backend.controller

import org.gymapp.backend.common.Common
import org.gymapp.backend.service.MemberService
import org.gymapp.backend.service.TrainerService
import org.gymapp.backend.service.UserService
import org.gymapp.library.request.CreateUserRequest
import org.gymapp.library.response.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    @Autowired private val userService: UserService,
    @Autowired private val common: Common,
    @Autowired private val memberService: MemberService,
    @Autowired private val trainerService: TrainerService
) {

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest, @AuthenticationPrincipal jwt: Jwt): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.createUser(request, jwt))
    }

    @PutMapping
    fun updateUser(@RequestBody request: CreateUserRequest, @AuthenticationPrincipal jwt: Jwt): ResponseEntity<*> {
        return ResponseEntity.ok(userService.updateUser(request, jwt))
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
        return ResponseEntity.ok(memberService.joinGymAsMember(common.getCurrentUser(jwt), code))
    }

    @PostMapping("/join-as-trainer")
    fun joinGymAsTrainer(@AuthenticationPrincipal jwt: Jwt, @RequestParam code: String): ResponseEntity<GymUserDto> {
        return ResponseEntity.ok(trainerService.joinGymAsTrainer(common.getCurrentUser(jwt), code))
    }


}