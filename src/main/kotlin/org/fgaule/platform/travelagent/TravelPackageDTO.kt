package org.fgaule.platform.travelagent

import java.math.BigDecimal
import java.time.LocalDate

data class TravelPackageDTO(val name: String,
                            val startTravelDate: LocalDate,
                            val endTravelDate: LocalDate,
                            val price: PriceDTO)

data class PriceDTO(val currency: String, val fee: BigDecimal)


