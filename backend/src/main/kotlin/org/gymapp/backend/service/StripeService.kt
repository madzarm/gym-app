package org.gymapp.backend.service

import com.stripe.Stripe
import com.stripe.model.*
import com.stripe.net.RequestOptions
import com.stripe.param.*
import jakarta.annotation.PostConstruct
import org.gymapp.backend.model.User
import org.gymapp.backend.repository.GymRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.ErrorResponse


@Service
class StripeService (
    @Autowired private val gymRepository: GymRepository,
) {

    @Value("\${stripe.api_key}")
    private lateinit var apiKey: String

    @PostConstruct
    fun init() {
        Stripe.apiKey = apiKey
    }

    fun createStripeCustomer(): Customer {
        val customerParams = CustomerCreateParams.builder()
            .build()
        return Customer.create(customerParams)
    }

    fun createEphemeralKey(customerId: String): EphemeralKey {
        val ephemeralKeyParams = EphemeralKeyCreateParams.builder()
            .setCustomer(customerId)
            .setStripeVersion("2024-04-10")
            .build()
        return EphemeralKey.create(ephemeralKeyParams)
    }

    fun createPaymentIntent(customerId: String, gymId: String): PaymentIntent {
        val gym = gymRepository.findById(gymId).orElseThrow { throw Exception("Gym not found")}
        val amount = gym.subscriptionFee
        val paymentIntentParams = PaymentIntentCreateParams.builder()
            .setAmount(amount)
            .setCurrency("eur")
            .setCustomer(customerId)
            .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
            .build()

        val connectedAccountId = gym.stripeAccountId
        val requestOptions = RequestOptions.builder()
            .setStripeAccount(connectedAccountId)
            .build()

        return PaymentIntent.create(paymentIntentParams, requestOptions)
    }

    fun getOrCreateCustomerId(user: User, gymId: String): String {
        val gymMember = user.getMember(gymId)

        val customerId = gymMember.customerId
        return customerId
    }

    fun getActiveSubscription(customerId: String, gymId: String): Subscription? {
        val params = SubscriptionListParams.builder()
            .setCustomer(customerId)
            .setStatus(SubscriptionListParams.Status.ACTIVE)
            .build()

        return Subscription.list(params).data.find { it.metadata["gymId"] == gymId }
    }

    fun createSubscription(customerId: String, gymId: String): Subscription {
        val gym = gymRepository.findById(gymId).orElseThrow { NoSuchElementException("Gym not found") }
        val priceId = gym.subscriptionPriceId

        val subscriptionParams = SubscriptionCreateParams.builder()
            .setCustomer(customerId)
            .addItem(SubscriptionCreateParams.Item.builder()
                .setPrice(priceId)
                .build())
            .build()

        return Subscription.create(subscriptionParams)
    }

    fun hasActiveSubscription(customerId: String): Boolean {
        val params = SubscriptionListParams.builder()
            .setCustomer(customerId)
            .setStatus(SubscriptionListParams.Status.ACTIVE)
            .setLimit(1L)
            .build()

        val subscriptions: SubscriptionCollection = Subscription.list(params)

        return subscriptions.data.isNotEmpty()
    }

    fun retrieveAccount(user: User, gymId: String): Account? {
        val gym = gymRepository.findById(gymId).orElseThrow { throw Exception("Gym not found")}
        val connectedAccountId = gym.stripeAccountId

        return Account.retrieve(connectedAccountId)
    }

    fun createStripeProductAndPrice(gymName: String, subscriptionFee: Long, connectedAccountId: String): String {
        // Create Product for Gym under the connected account
        val productParams = ProductCreateParams.builder()
            .setName("$gymName Membership")
            .build()

        val requestOptions = RequestOptions.builder()
            .setStripeAccount(connectedAccountId)  // Specify the connected account ID here
            .build()

        val product = Product.create(productParams, requestOptions)

        // Create Price for the Product under the connected account
        val priceParams = PriceCreateParams.builder()
            .setUnitAmount(subscriptionFee)  // Subscription fee in cents
            .setCurrency("eur")  // or any other currency
            .setRecurring(PriceCreateParams.Recurring.builder()
                .setInterval(PriceCreateParams.Recurring.Interval.MONTH)  // Monthly billing
                .build())
            .setProduct(product.id)
            .build()

        val price = Price.create(priceParams, requestOptions)

        return price.id  // This is the Price ID to be used when creating subscriptions
    }


    fun createStripeConnectedAccount(ownerEmail: String): Account {
        val accountParams = AccountCreateParams.builder()
            .setType(AccountCreateParams.Type.STANDARD)  // Or CUSTOM based on your need
            .setEmail(ownerEmail)
            .build()

        return Account.create(accountParams)
    }

    fun getConnectedAccountId(gymId: String): String {
        val gym = gymRepository.findById(gymId).orElseThrow { throw Exception("Gym not found")}
        val connectedAccountId = gym.stripeAccountId
        return connectedAccountId
    }

    fun createAccountLink(accountId: String, returnUrl: String, refreshUrl: String): String {
        val accountLinkParams = AccountLinkCreateParams.builder()
            .setAccount(accountId)
            .setRefreshUrl("http://192.168.40.195:8080/gyms/stripe-refresh")
            .setReturnUrl("http://192.168.40.195:8080/gyms/stripe-return")
            .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
            .build()

        val accountLink = AccountLink.create(accountLinkParams)
        return accountLink.url
    }
}