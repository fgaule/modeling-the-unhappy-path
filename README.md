# Modeling the unhappy path

Sample project for blog post ["Modeling the unhappy path"](https://medium.com/@federicogaule/error-handling-modeling-the-unhappy-path-675c850c54d8?sk=ce9685431399a2c386f1e1d4475052cc). 

* Kotlin 1.3.72
* Maven
* Arrow-kt 0.10.5
* Spring-boot 2.1.5.RELEASE
* Kotest 4.0.5

# A bit of context
There is an external supplier of tourism packages (like a cruise + car + activities ) that have a lot of bookings. We were told that all bookings are in "Confirmed" status (there are other possible status like "Canceled", "On Hold" or an unknown value) and have an agency reference where the purchase has been done.
A list of bookings has been provided to us and we have to import them to a in-house app to post-process them. 

## How to run
Running TravelAgentApplication.kt binds REST endpoint http://localhost:9290/travel-agent/{id} and starts up a Wiremock server to responde to a few ids in order to test multiple paths:
* id: LB57ZQ
    * Booking is OK
    * Response: 200
* id: AB12SY
    * Fails validation "Booking must be in Confirmed status"
    * Response: 500
* id: AB13ZX
    * Fails 2 valudations "Booking must be in Confirmed status" and "AgencyRef cannot be empty"
    * Response: 500
* id: NON_EXISTENT
    * Booking does not exist in remote server
    * Response: 404
    
