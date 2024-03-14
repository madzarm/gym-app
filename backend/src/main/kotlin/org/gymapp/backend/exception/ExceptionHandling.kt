package org.gymapp.backend.security.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

class UserNotFoundException(message: String) : RuntimeException(message)
class UserAlreadyRegisteredException(message: String) : RuntimeException(message)

data class ExceptionResult(val message: String, val exception: String)

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ResponseEntity<ExceptionResult> {
        val body = ExceptionResult(
            ex.message ?: "User not found!",
            ex::class.simpleName ?: "UserNotFoundException"
        )
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UserAlreadyRegisteredException::class)
    fun handleUserAlreadyRegistered(ex: UserAlreadyRegisteredException): ResponseEntity<ExceptionResult> {
        val body = ExceptionResult(
            ex.message ?: "User is already registered!",
            ex::class.simpleName ?: "UserAlreadyRegisteredException"
        )
        return ResponseEntity(body, HttpStatus.CONFLICT)
    }
}