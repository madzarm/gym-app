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

    fun createStripeCustomer(email: String, connectedAccountId: String): Customer {
        val customerParams = CustomerCreateParams.builder()
            .setEmail(email)
            .build()

        val requestOptions = RequestOptions.builder()
            .setStripeAccount(connectedAccountId)
            .build()
        return Customer.create(customerParams, requestOptions)
    }

    fun hasValidSubscription(customerId: String, accountId: String): Boolean {
        val params = SubscriptionListParams.builder()
            .setCustomer(customerId)
            .setStatus(SubscriptionListParams.Status.ACTIVE)
            .build()

        val requestOptions = RequestOptions.builder()
            .setStripeAccount(accountId)
            .build()

        val subscriptions = Subscription.list(params, requestOptions).data
        return subscriptions.isNotEmpty()
    }

    fun createEphemeralKey(customerId: String, connectedAccountId: String): EphemeralKey {
        val ephemeralKeyParams = EphemeralKeyCreateParams.builder()
            .setCustomer(customerId)
            .setStripeVersion("2024-04-10")
            .build()

        val requestOptions = RequestOptions.builder()
            .setStripeAccount(connectedAccountId)
            .build()

        return EphemeralKey.create(ephemeralKeyParams, requestOptions)
    }

    fun createPaymentIntent(customerId: String, gymId: String): PaymentIntent {
        val gym = gymRepository.findById(gymId).orElseThrow { throw Exception("Gym not found")}
        val amount = gym.subscriptionFee
        val paymentIntentParams = PaymentIntentCreateParams.builder()
            .setAmount(amount*100)
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

    fun createSetupIntent(customerId: String, gymId: String): SetupIntent {
        val gym = gymRepository.findById(gymId).orElseThrow { throw Exception("Gym not found")}
        val params = SetupIntentCreateParams.builder()
            .setCustomer(customerId)
            .setAutomaticPaymentMethods(SetupIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
            .setUsage(SetupIntentCreateParams.Usage.OFF_SESSION) // Indicates intended usage
            .build()

        val connectedAccountId = gym.stripeAccountId
        val requestOptions = RequestOptions.builder()
            .setStripeAccount(connectedAccountId)
            .build()

        return SetupIntent.create(params, requestOptions)
    }

    fun getCustomerId(user: User, gymId: String): String {
        val gymMember = user.getMember(gymId)

        val customerId = gymMember.customerId
        return customerId
    }

    fun getPaymentMethod(setupIntentId: String, connectedAccountId: String): PaymentMethod {
        val actualSetupIntentId = setupIntentId.split("_secret_").first()
        // Create request options with the connected account ID
        val requestOptions = RequestOptions.builder()
            .setStripeAccount(connectedAccountId)
            .build()

        // Retrieve the SetupIntent object
        val setupIntent = SetupIntent.retrieve(actualSetupIntentId, requestOptions)

        println(setupIntent)
        // Extract the payment method ID
        val paymentMethodId = setupIntent.paymentMethod
            ?: throw IllegalArgumentException("Payment method not found in SetupIntent")

        println(requestOptions)
        // Retrieve the PaymentMethod object
        return PaymentMethod.retrieve(paymentMethodId, requestOptions)
    }

    fun attachPaymentMethod(paymentMethodId: String, customerId: String, connectedAccountId: String) {
        val requestOptions = RequestOptions.builder()
            .setStripeAccount(connectedAccountId)
            .build()

        val paymentMethod = PaymentMethod.retrieve(paymentMethodId, requestOptions)

        val params = PaymentMethodAttachParams.builder()
            .setCustomer(customerId)
            .build()


        // Attach the payment method to the customer
        paymentMethod.attach(params, requestOptions)

        val customerUpdateParams = CustomerUpdateParams.builder()
            .setInvoiceSettings(CustomerUpdateParams.InvoiceSettings.builder()
                .setDefaultPaymentMethod(paymentMethodId)
                .build())
            .build()

        val customer = Customer.retrieve(customerId, requestOptions)
        customer.update(customerUpdateParams, requestOptions)
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
        val stripeAccountId = gym.stripeAccountId

        val subscriptionParams = SubscriptionCreateParams.builder()
            .setCustomer(customerId)
            .addItem(SubscriptionCreateParams.Item.builder()
                .setPrice(priceId)
                .build())
            .setPaymentSettings(SubscriptionCreateParams.PaymentSettings.builder()
                .addPaymentMethodType(SubscriptionCreateParams.PaymentSettings.PaymentMethodType.CARD)
                .build())
            .build()

        val requestOptions = RequestOptions.builder()
            .setStripeAccount(stripeAccountId)
            .build()

        return Subscription.create(subscriptionParams, requestOptions)
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
            .setUnitAmount(subscriptionFee * 100)  // Subscription fee in cents
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