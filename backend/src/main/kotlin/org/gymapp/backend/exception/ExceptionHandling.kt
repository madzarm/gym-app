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

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ExceptionResult> {
        val body = ExceptionResult(
            ex.message ?: "Illegal argument!",
            ex::class.simpleName ?: "IllegalArgumentException"
        )
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

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

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ExceptionResult> {
        val body = ExceptionResult(
            ex.message ?: "An error occurred!",
            ex::class.simpleName ?: "Exception"
        )
        ex.printStackTrace()
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}