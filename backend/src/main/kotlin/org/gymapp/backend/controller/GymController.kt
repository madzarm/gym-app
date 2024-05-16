package org.gymapp.backend.controller

import jakarta.servlet.http.HttpServletResponse
import jakarta.websocket.server.PathParam
import org.gymapp.backend.common.Common
import org.gymapp.backend.service.GymService
import org.gymapp.backend.service.StripeService
import org.gymapp.library.request.CreateGymRequest
import org.gymapp.library.response.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/gyms")
class GymController(
    @Autowired private val gymService: GymService,
    @Autowired private val common: Common,
    @Autowired private val stripeService: StripeService
) {

    @PostMapping
    fun createGym(
        @RequestBody request: CreateGymRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<GymUserDto> {
        return ResponseEntity.status(201).body(gymService.createGym(request, common.getCurrentUser(jwt)))
    }

    @GetMapping
    fun findUserGyms(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<List<GymDto>> {
        return ResponseEntity.ok(gymService.findUserGyms(common.getCurrentUser(jwt)))
    }

    @GetMapping("/{gymId}/classes")
    fun getUpcomingGymClasses(
        @PathVariable gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<GymClassDto>> {
        return ResponseEntity.ok(gymService.getUpcomingGymClasses(common.getCurrentUser(jwt), gymId))
    }

    @GetMapping("/members/{memberId}")
    fun getGymMemberFull(
        @PathVariable memberId: String,
    ): ResponseEntity<GymMemberDtoFull> {
        return ResponseEntity.ok(gymService.getGymMemberFull(memberId))
    }

    @PostMapping("/members")
    fun getGymMembersFull(
        @RequestBody memberIds: List<String>
    ): ResponseEntity<List<GymMemberDtoFull>> {
        return ResponseEntity.ok(gymService.getGymMembersFull(memberIds))
    }

    @GetMapping("/{gymId}/live")
    fun getLiveStatus(
        @PathVariable gymId: String
    ): ResponseEntity<Int> {
        return ResponseEntity.ok(gymService.getLiveStatus(gymId))
    }

    @GetMapping("/{gymId}/gymVisits")
    fun getGymVisits(
        @PathVariable gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<GymVisitDto>> {
        return ResponseEntity.ok(gymService.getGymVisits(common.getCurrentUser(jwt), gymId))
    }

    @GetMapping("/{gymId}/gymVisits/heatMap")
    fun getGymVisitsHeatMap(
        @PathVariable gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<VisitCountByDay>> {
        return ResponseEntity.ok(gymService.getGymVisitsHeatMapData(common.getCurrentUser(jwt), gymId))
    }

    @GetMapping("/payment-sheet")
    fun createPaymentSheet (
        @PathParam("gymId") gymId: String,
        @AuthenticationPrincipal jwt: Jwt
    ): PaymentSheetResponse {
        val connectedAccountId = stripeService.getConnectedAccountId(gymId)
        val customerId = stripeService.getCustomerId(common.getCurrentUser(jwt), gymId)

        stripeService.createSubscription(customerId, gymId)

        val ephemeralKey = stripeService.createEphemeralKey(customerId, connectedAccountId)

        val paymentIntent = stripeService.createPaymentIntent(customerId, gymId)

        return PaymentSheetResponse(
            paymentIntent = paymentIntent.clientSecret,
            ephemeralKey = ephemeralKey.secret,
            customer = customerId,
            publishableKey = "pk_test_51PExcdRqxY1WlypugPzrovZ9449kgR1egezShfTpaaPow4xCnrvbS9mECcjt9ndTyylrsPEpOkVvYcuWISfrMgTR00LMw0rIpe",
            stripeAccountId = connectedAccountId
        )
    }

    @GetMapping("/setup-intent")
    fun createSetupIntent(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam gymId: String,
    ): PaymentSheetResponse {
        val connectedAccountId = stripeService.getConnectedAccountId(gymId)

        val customerId = stripeService.getCustomerId(common.getCurrentUser(jwt), gymId)

        val setupIntent = stripeService.createSetupIntent(customerId, gymId)

        val ephemeralKey = stripeService.createEphemeralKey(customerId, connectedAccountId)

        return PaymentSheetResponse(
            paymentIntent = setupIntent.clientSecret,
            ephemeralKey = ephemeralKey.secret,
            customer = customerId,
            publishableKey = "pk_test_51PExcdRqxY1WlypugPzrovZ9449kgR1egezShfTpaaPow4xCnrvbS9mECcjt9ndTyylrsPEpOkVvYcuWISfrMgTR00LMw0rIpe",
            stripeAccountId = connectedAccountId
        )
    }

    @GetMapping("/confirm-setup-intent")
    fun confirmSetupIntent(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam gymId: String,
        @RequestParam setupIntentId: String,
    ) {
        val connectedAccountId = stripeService.getConnectedAccountId(gymId)
        val customerId = stripeService.getCustomerId(common.getCurrentUser(jwt), gymId)
        val paymentMethod = stripeService.getPaymentMethod(setupIntentId, connectedAccountId)
        stripeService.attachPaymentMethod(paymentMethod.id, customerId, connectedAccountId)
    }

    @GetMapping("/subscription-status")
    fun getS(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam gymId: String
    ): ResponseEntity<SubscriptionStatusDto> {
        val customerId = stripeService.getCustomerId(common.getCurrentUser(jwt), gymId)
        val accountId = stripeService.getConnectedAccountId(gymId)
        val hasValidSubscription = stripeService.hasValidSubscription(customerId, accountId)
        return ResponseEntity.ok(SubscriptionStatusDto(hasValidSubscription))
    }

    @GetMapping("/stripe-connect-account-completed")
    fun isStripeConnectAccountCompleted(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam gymId: String
    ): ResponseEntity<AccountCompletedDto> {
        val acc = stripeService.retrieveAccount(common.getCurrentUser(jwt), gymId)
        val isCompleted = acc != null && acc.detailsSubmitted
        return ResponseEntity.ok(AccountCompletedDto(isCompleted))
    }


    @GetMapping("/create-account-link")
    fun createAccountLink(
        @RequestParam gymId: String,
        @RequestParam returnUrl: String,
        @RequestParam refreshUrl: String
    ): ResponseEntity<AccountLinkDto> {
        val connectedAccountId = stripeService.getConnectedAccountId(gymId)
        val accountLinkUrl = stripeService.createAccountLink(connectedAccountId, returnUrl, refreshUrl)
        return ResponseEntity.ok(AccountLinkDto(accountLinkUrl))
    }

    @GetMapping("/stripe-return")
    fun handleStripeReturn(response: HttpServletResponse) {
        response.sendRedirect("gym-app://stripe-return")
    }

    @GetMapping("/stripe-refresh")
    fun handleStripeRefresh(response: HttpServletResponse) {
        response.sendRedirect("gym-app://refresh")
    }



}