package org.fgaule.platform.travelagent

import arrow.core.*
import arrow.core.extensions.fx
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicativeError.applicativeError

import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class TravelAgentService(val repository: BookingRepository) {

    fun get(pnr: String): Either<BookingError, TravelPackage> {

        return Either.fx {
            val travelPackage = repository.get(pnr).bind()
            val validTravelPackage = BookingValidator.validate(travelPackage).toEither().bind()
            doYourLogic(validTravelPackage)
        }
    }

    private fun doYourLogic(validTravelPackage: Booking): TravelPackage {
        val handlingFee = validTravelPackage.products.filter { product -> product.type == "JHF" }
                .fold(Money(BigDecimal.ZERO, "UNK")) { base, handlingFee -> Money(base.amount.plus(handlingFee.basePrice), handlingFee.currency) }

        return TravelPackage("nombre del paquete", LocalDate.now(), LocalDate.now(), Price(handlingFee.currency, handlingFee.amount))
    }


}

object BookingValidator {

    fun validate(tp: Booking): Validated<BookingError, Booking> {
        val agency = tp.agencyIsPresent()
        val email = tp.validStatus()

        return Validated.applicativeError(NonEmptyList.semigroup<BookingFormatError>())
                .mapN(agency, email) { tp }
                .fix()
                .fold({ BookingFormatError.InvalidBooking(it).invalid() }, { it.valid() })
    }

    private fun Booking.validStatus(): ValidatedNel<BookingFormatError, Booking> =
            if (status == "Confirmed") this.valid()
            else BookingFormatError.InvalidStatus(status).nel().invalid()

    private fun Booking.agencyIsPresent(): ValidatedNel<BookingFormatError, Booking> =
            if (agency.isNotEmpty()) this.valid()
            else BookingFormatError.InvalidState("Agency is not present").nel().invalid()

}


sealed class BookingFormatError(override val message: String) : BookingError {
    data class InvalidStatus(val status: String) : BookingFormatError("Booking has status '$status' but only 'Con' status is supported")
    data class InvalidState(override val message: String) : BookingFormatError(message)
    data class InvalidBooking(val reasons: Nel<BookingFormatError>) : BookingFormatError("Not a valid booking: ${reasons.all.joinToString(", ") { it.message }}")
}

object TravelPackageTranslator {
    fun translate(source: TravelPackage): TravelPackageDTO {
        return source.run {
            TravelPackageDTO(name = name,
                    startTravelDate = startTravelDate,
                    endTravelDate = endTravelDate,
                    price = PriceDTO(currency = price.currency, fee = price.fee))
        }
    }
}
