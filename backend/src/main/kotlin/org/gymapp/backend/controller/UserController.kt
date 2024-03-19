package org.gymapp.backend.controller

import org.gymapp.backend.service.UserService
import org.gymapp.library.request.CreateUserRequest
import org.gymapp.library.response.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
    @Autowired private val userService: UserService
) {

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest, @AuthenticationPrincipal jwt: Jwt): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.createUser(request, jwt))
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        return ResponseEntity.ok(userService.getAllUsers())
    }

}