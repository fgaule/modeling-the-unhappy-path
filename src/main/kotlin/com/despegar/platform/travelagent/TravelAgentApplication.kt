package com.despegar.platform.travelagent

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import org.springframework.web.client.RestTemplate
import org.springframework.ws.soap.SoapVersion
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory





@SpringBootApplication
class TravelAgentApplication

fun main(args: Array<String>) {
	runApplication<TravelAgentApplication>(*args)
}

@Configuration
class Configuracion {

	@Bean
	fun marshaller(): Jaxb2Marshaller {
		val marshaller = Jaxb2Marshaller()
		// this is the package name specified in the <generatePackage> specified in
		// pom.xml
		marshaller.setContextPath("com.example.howtodoinjava.schemas.school")
		return marshaller
	}

    @Bean
    fun messageFactory(): SaajSoapMessageFactory {
        val messageFactory = SaajSoapMessageFactory()
        messageFactory.setSoapVersion(SoapVersion.SOAP_12)
        return messageFactory
    }
/**
	@Bean
	fun soapConnector(marshaller: Jaxb2Marshaller): SOAPConnector {
		val client = SOAPConnector()
		client.setDefaultUri("http://localhost:8080/service/student-details")
		client.setMarshaller(marshaller)
		client.setUnmarshaller(marshaller)
		return client
	}
	**/
}