package org.fgaule.platform.travelagent

import java.math.BigDecimal
import java.time.LocalDate

data class TravelPackage(val name: String,
                         val startTravelDate: LocalDate,
                         val endTravelDate: LocalDate,
                         val price: Price)

data class Price(val currency: String, val fee: BigDecimal)

data class Money(val amount: BigDecimal, val currency: String)