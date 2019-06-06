package com.despegar.platform.travelagent

import falabella.wsdl.GetBookings
import falabella.wsdl.GetBookingsResponse
import org.springframework.stereotype.Component
import org.springframework.ws.client.core.support.WebServiceGatewaySupport


@Component
class JuniperClient : WebServiceGatewaySupport() {

    fun get(pnr: String): GetBookingsResponse {

        val request = GetBookings.builder()
                .withBookingCode(pnr)
                .withUser("SrvIntegracionVf")
                .withPassword("Srv_Integr@cion01")
                .build()

        logger.info { "lala"}

        val response = webServiceTemplate.marshalSendAndReceive("https://oper.viajesfalabella.com/wsexportacion/wsBookings.asmx", request)
        when(response) {
            is GetBookingsResponse -> return response
            else -> throw RuntimeException()
        }

    }
}