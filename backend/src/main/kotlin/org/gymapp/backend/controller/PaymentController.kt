package org.gymapp.backend.controller

import jakarta.websocket.server.PathParam
import org.gymapp.backend.service.StripeService
import org.gymapp.library.response.PaymentSheetResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/payments")
class PaymentController (
    @Autowired private val stripeService: StripeService
){

    @GetMapping("/payment-sheet")
    fun createPaymentSheet(
        @PathParam("gymId") gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): PaymentSheetResponse {
        val customer = stripeService.createStripeCustomer()
        val ephemeralKey = stripeService.createEphemeralKey(customer.id)
        val paymentIntent = stripeService.createPaymentIntent(customer.id)

        return PaymentSheetResponse(
            paymentIntent = paymentIntent.clientSecret,
            ephemeralKey = ephemeralKey.secret,
            customer = customer.id,
            publishableKey = "pk_test_51K6tdwAXKEmEy22JkAD3kC4pGJ7K3jtBjwQARLgiQJljwH1VTcjB7WZj8Goo9aBmoJdOz7nNfE53pCdkKB1cpMv100vGlYZ1Dx"
        )
    }
}