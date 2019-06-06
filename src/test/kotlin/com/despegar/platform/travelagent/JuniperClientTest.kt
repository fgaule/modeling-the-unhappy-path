package com.despegar.platform.travelagent

import io.kotlintest.specs.StringSpec
import org.junit.Test

import org.junit.Assert.*
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import org.springframework.ws.soap.SoapVersion
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory


class JuniperClientTest: StringSpec( {

    "length should return size of string" {
        val marshaller = Jaxb2Marshaller()
        // this package must match the package in the <generatePackage> specified in
        // pom.xml
        marshaller.contextPath = "falabella.wsdl"

        val messageFactory = SaajSoapMessageFactory()
        messageFactory.setSoapVersion(SoapVersion.SOAP_12)
        messageFactory.afterPropertiesSet()

        val client = JuniperClient()
        client.defaultUri = "https://oper.viajesfalabella.com/wsexportacion/wsBookings.asmx"
        client.marshaller = marshaller
        client.unmarshaller = marshaller
        client.messageFactory = messageFactory

        val message = client.get("1CMF42")
        message.getBookingsResult.content[0]
        println(message.getBookingsResult.content[0])
    }
})