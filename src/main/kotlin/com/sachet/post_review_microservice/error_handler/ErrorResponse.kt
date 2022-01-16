package com.sachet.post_review_microservice.error_handler

import org.springframework.http.HttpStatus

class ErrorResponse(
    val message: String?,
    val errorCode: HttpStatus
) {
    override fun toString(): String {
        return "{message='$message', errorCode=$errorCode}"
    }
}