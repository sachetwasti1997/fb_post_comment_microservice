package com.sachet.post_review_microservice.error_handler

import com.sachet.post_review_microservice.custom_exception.CommentNotFound
import com.sachet.post_review_microservice.custom_exception.PostDataException
import org.bson.json.JsonObject
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class GlobalErrorHandler: ErrorWebExceptionHandler {

    private fun createErrorObject(ex:Exception, status: HttpStatus): JsonObject {
        val errorResponse = "{\"message\":\"${ex.message}\",\"errorCode\":\"${status}\"}"
        return JsonObject(errorResponse)
    }

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val dataFactory = exchange.response.bufferFactory()
        var jsonResponse : JsonObject
        if (ex is CommentNotFound){
            exchange.response.statusCode = HttpStatus.NOT_FOUND
            jsonResponse = createErrorObject(ex, HttpStatus.NOT_FOUND)
        }
        else if (ex is PostDataException){
            exchange.response.statusCode = HttpStatus.BAD_REQUEST
            jsonResponse = createErrorObject(ex as Exception, HttpStatus.BAD_REQUEST)
        }
        else{
            exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
            jsonResponse = createErrorObject(ex as Exception, HttpStatus.INTERNAL_SERVER_ERROR)
        }
        val errorMessage = dataFactory.wrap(jsonResponse.toString().toByteArray())
        return exchange.response.writeWith (Mono.just(errorMessage))
    }

}