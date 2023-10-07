package ga.injuk.commentor.adapter.`in`.rest

import ga.injuk.commentor.adapter.core.exception.*
import ga.injuk.commentor.common.CommentorError
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

    @ExceptionHandler(InvalidJsonException::class, InvalidArgumentException::class, BadRequestException::class)
    fun handleBadRequestException(e: CommentorError) = ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(e.errorDetails)

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(e: CommentorError) = ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(e.errorDetails)
}