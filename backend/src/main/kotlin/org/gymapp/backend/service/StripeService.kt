package org.gymapp.backend.service

import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.EphemeralKey
import com.stripe.model.PaymentIntent
import com.stripe.param.CustomerCreateParams
import com.stripe.param.EphemeralKeyCreateParams
import com.stripe.param.PaymentIntentCreateParams
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class StripeService {

    @Value("\${stripe.api_key}")
    private lateinit var apiKey: String

    @PostConstruct
    fun init() {
        Stripe.apiKey = apiKey
    }

    fun createStripeCustomer(): Customer {
        val customerParams = CustomerCreateParams.builder().build()
        return Customer.create(customerParams)
    }

    fun createEphemeralKey(customerId: String): EphemeralKey {
        val ephemeralKeyParams = EphemeralKeyCreateParams.builder()
            .setCustomer(customerId)
            .setStripeVersion("2024-04-10")
            .build()
        return EphemeralKey.create(ephemeralKeyParams)
    }

    fun createPaymentIntent(customerId: String): PaymentIntent {
        val paymentIntentParams = PaymentIntentCreateParams.builder()
            .setAmount(60L)
            .setCurrency("eur")
            .setCustomer(customerId)
            .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
            .build()
        return PaymentIntent.create(paymentIntentParams)
    }
}