package ga.injuk.commentor.adapter.`in`.rest

import ga.injuk.commentor.adapter.exception.InvalidJsonException
import ga.injuk.commentor.adapter.exception.UncaughtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CommentorExceptionHandler {

    @ExceptionHandler(UncaughtException::class)
    fun handleUncaughtException(e: UncaughtException) = ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(e.errorDetails)

    @ExceptionHandler(InvalidJsonException::class)
    fun handleInvalidJsonException(e: InvalidJsonException) = ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(e.errorDetails)
}