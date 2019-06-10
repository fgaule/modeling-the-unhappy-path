package org.fgaule.platform.travelagent

import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.nel
import io.kotest.assertions.arrow.validation.shouldBeInvalid
import io.kotest.assertions.arrow.validation.shouldBeValid
import io.kotest.core.spec.style.StringSpec
import org.fgaule.platform.travelagent.BookingFormatError.InvalidBooking
import org.fgaule.platform.travelagent.BookingFormatError.InvalidStatus
import java.time.LocalDateTime

class BookingValidatorTest : StringSpec({

    "validate a confirmed booking" {
        val booking = Booking(status = "Confirmed", agency = "1", date = LocalDateTime.now(), products = listOf())

        val validate: Validated<BookingError, Booking> = BookingValidator.validate(booking)
        validate.shouldBeValid()
    }

    "validate a canceled booking" {
        val booking = Booking(status = "Canceled", agency = "1", date = LocalDateTime.now(), products = listOf())

        val validate: Validated<BookingError, Booking> = BookingValidator.validate(booking)
        validate shouldBeInvalid InvalidBooking(reasons = InvalidStatus("Canceled").nel())
    }

    "validate a no-agency booking" {
        val booking = Booking(status = "Confirmed", agency = "", date = LocalDateTime.now(), products = listOf())

        val validate: Validated<BookingError, Booking> = BookingValidator.validate(booking)
        validate shouldBeInvalid InvalidBooking(reasons = BookingFormatError.InvalidState("Agency is not present").nel())
    }

    "validate a no-agency & booking" {
        val booking = Booking(status = "Canceled", agency = "", date = LocalDateTime.now(), products = listOf())

        val validate: Validated<BookingError, Booking> = BookingValidator.validate(booking)
        validate shouldBeInvalid InvalidBooking(reasons = NonEmptyList.of(
                BookingFormatError.InvalidState("Agency is not present"),
                InvalidStatus("Canceled")))
    }

})