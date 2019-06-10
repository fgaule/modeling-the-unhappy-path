package org.fgaule.platform.travelagent

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.RestTemplate
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@SpringBootApplication

class TravelAgentApplication

fun main(args: Array<String>) {
    runApplication<TravelAgentApplication>(*args)
}

@Configuration
@AutoConfigureWireMock(port = 9291, stubs = ["classpath:/stubs\""])
class AppConfig {

    @Bean
    fun outgoingRestTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.messageConverters.add(MappingJackson2XmlHttpMessageConverter())
        return restTemplate
    }

}

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {



    @ExceptionHandler(value = [FailureException::class])
    protected fun handleEntityNotFound(ex: FailureException, request: WebRequest): ResponseEntity<Any> {

        logger.error("Se rompio todo", ex)

        return when (ex.failure) {
            is NotFound ->  ResponseEntity(ApiError(ex.failure.message), HttpStatus.NOT_FOUND)
            is SideEffectFailure -> ResponseEntity(ApiError(ex.failure.message, listOf(ex.failure.cause.message
                    ?: "Unknown")), HttpStatus.INTERNAL_SERVER_ERROR)
            else -> ResponseEntity(ApiError(ex.failure.message), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    data class ApiError(val message: String, val cause: List<String> = emptyList())
}