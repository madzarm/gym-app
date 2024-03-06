package org.gymapp.backend.security.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

class UserNotFoundException(message: String) : RuntimeException(message)

// Global exception handler
@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ResponseEntity<*> {
        val body = mapOf("message" to ex.message)
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }
}