package org.gymapp.backend.security.controller

import org.gymapp.backend.security.model.SecurityUser
import org.gymapp.backend.security.service.SecurityService
import org.gymapp.library.request.CreateAccountRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/security")
@CrossOrigin("*")
class SecurityController(
    private val securityService: SecurityService
) {

    @PostMapping("/register")
    fun register(@RequestBody request: CreateAccountRequest) = securityService.register(request)

    @PostMapping("/login")
    fun login(
        @RequestParam email: String,
        @RequestParam password: String
    ) = securityService.login(email, password)

    @PatchMapping("/update-roles")
    fun updateRoles(
        @RequestParam email: String,
        @RequestParam role: String
    ) = securityService.updateRoles(email, role)

    @GetMapping("/users/current")
    fun getCurrentUser(@AuthenticationPrincipal securityUser: SecurityUser) =
        securityService.getCurrentUser(securityUser)

    @GetMapping("/users")
    fun getAllUsers() = securityService.getAllUsers()

    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id: String) = securityService.deleteUser(id)
}
