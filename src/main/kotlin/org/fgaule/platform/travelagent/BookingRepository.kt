package org.fgaule.platform.travelagent

import arrow.core.*
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Repository
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.time.LocalDateTime


@Repository
class BookingRepository(@Value("\${travelagent.repository.endpoint}") private val endpoint:String, private val http: RestTemplate) {

    fun get(pnr: String): Either<BookingRetrievalError, Booking> {


        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val map = LinkedMultiValueMap<String, String>().apply {
            add("BookingCode", pnr)
            add("channel", "site")
            add("ModuleType", "PROD")
        }

        val request = HttpEntity<MultiValueMap<String, String>>(map, headers)

        return Either.unsafeCatch { http.postForObject(endpoint, request, Container::class.java) }
                .mapLeft {
                    when (it) {
                        is HttpServerErrorException.InternalServerError -> BookingRetrievalError.SupplierFailure(it.responseBodyAsString)
                        else -> BookingRetrievalError.UnexpectedFailure(it)
                    }
                }
                .flatMap { response ->
                    Option.fromNullable(response)
                            .filter { it.bookings.isNotEmpty() }
                            .map { it.bookings.first() }
                            .toEither { BookingRetrievalError.BookingNotFound(pnr) }
                }

    }


}


interface BookingError : Failure

sealed class BookingRetrievalError(override val message: String) : BookingError {
    class BookingNotFound(pnr: String) : BookingRetrievalError("Booking with pnr '$pnr' not found"), NotFound
    class SupplierFailure(message: String) : BookingRetrievalError("Supplier failure: $message"), Failure
    class UnexpectedFailure(cause: Throwable) : BookingRetrievalError(cause.message ?: "No description"), Failure
}

@JacksonXmlRootElement(localName = "wsResult")
class Container(@JacksonXmlProperty(localName = "Bookings") val bookings: List<Booking> = emptyList())

data class Booking(@JacksonXmlProperty(isAttribute = true, localName = "Status") val status: String,
                   @JacksonXmlProperty(isAttribute = true, localName = "BookingDate") val date: LocalDateTime,
                   @JacksonXmlProperty(isAttribute = true, localName = "AgencyRef") val agency: String,
                   @JacksonXmlProperty(localName = "Lines") val products: List<Product>) {

    data class Product(@JacksonXmlProperty(localName = "ProductType") val type: String,
                       @JacksonXmlProperty(localName = "ServiceName") val name: String,
                       @JacksonXmlProperty(localName = "CostCurrency") val currency: String,
                       @JacksonXmlProperty(localName = "BasePrice") val basePrice: BigDecimal)
}

private fun <A> Either.Companion.unsafeCatch(f: () -> A) =
        try {
            f().right()
        } catch (t: Throwable) {
            t.left()
        }
