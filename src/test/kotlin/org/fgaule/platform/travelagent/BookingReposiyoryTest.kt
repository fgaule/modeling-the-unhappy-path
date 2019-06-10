package org.fgaule.platform.travelagent

import io.kotest.assertions.arrow.either.shouldBeLeftOfType
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.spring.SpringListener
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.web.client.RestTemplate


@AutoConfigureWireMock(port = 9290, stubs = ["/stubs"], files = ["/stubs"])
class BookingReposiyoryTest : StringSpec() {

    override fun listeners() = listOf(SpringListener)

    init {

        val http = RestTemplate()
        val client = BookingRepository("http://localhost:9290/bookings", http)

        "get existent confirmed booking" {
            val booking = client.get("LB57ZQ")
            booking.shouldBeRight()
        }

        "get existent canceled booking" {
            val booking = client.get("AB12SY")
            booking.shouldBeRight()
        }

        "get non existent booking" {
            val booking = client.get("NONEXISTENT")
            booking.shouldBeLeftOfType<BookingRetrievalError.BookingNotFound>()
        }
    }
}