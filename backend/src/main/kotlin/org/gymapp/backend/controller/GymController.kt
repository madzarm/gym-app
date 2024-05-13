package org.gymapp.backend.controller

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
            publishableKey = "pk_test_51PExcdRqxY1WlypugPzrovZ9449kgR1egezShfTpaaPow4xCnrvbS9mECcjt9ndTyylrsPEpOkVvYcuWISfrMgTR00LMw0rIpe"
        )
    }


}