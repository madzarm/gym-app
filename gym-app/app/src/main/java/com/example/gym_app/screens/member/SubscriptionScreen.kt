package com.example.gym_app.screens.member

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.viewModels.SharedViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

@Composable
fun SubscriptionScreen(viewModel: SharedViewModel, navController: NavController) {
    val context = LocalContext.current

    val setupPaymentSheetResponse = viewModel.setupPaymentSheet.observeAsState()
    val paymentSheetResponse = viewModel.paymentSheet.observeAsState()

    val setupPaymentSheet = rememberPaymentSheet { result ->
        onSetupPaymentSheetResult(result, viewModel, context, navController)
    }
    val paymentSheet = rememberPaymentSheet { result ->
        onPaymentSheetResult(result, navController)
    }

    var customerConfig by remember { mutableStateOf<PaymentSheet.CustomerConfiguration?>(null) }
    var setupIntentClientSecret by remember { mutableStateOf<String?>(null) }
    var paymentIntentClientSecret by remember { mutableStateOf<String?>(null) }
    val isSetupCompleted = viewModel.isSetupCompleted.observeAsState().value

    // Trigger setup intent
    LaunchedEffect(Unit) {
        viewModel.getSetupIntent(context)
    }

    // Handle setup intent response
    LaunchedEffect(setupPaymentSheetResponse.value) {
        setupPaymentSheetResponse.value?.let { response ->
            setupIntentClientSecret = response.paymentIntent
            customerConfig =
                PaymentSheet.CustomerConfiguration(response.customer ?: "", response.ephemeralKey ?: "")
            val publishableKey = response.publishableKey
            println("StripeAcc: ${response.stripeAccountId}")
            PaymentConfiguration.init(context, publishableKey ?: "", response.stripeAccountId ?: "")

            if (customerConfig != null && setupIntentClientSecret != null) {
                presentSetupPaymentSheet(setupPaymentSheet, customerConfig!!, setupIntentClientSecret!!)
            }
        }
    }

    // Trigger payment intent after setup is completed
    LaunchedEffect(isSetupCompleted) {
        println("isSetupCompleted: $isSetupCompleted")
        if (isSetupCompleted == true) {
            viewModel.getPaymentSheet(context)
        }
    }

    // Handle payment intent response
    LaunchedEffect(paymentSheetResponse.value) {
        paymentSheetResponse.value?.let { response ->
            paymentIntentClientSecret = response.paymentIntent
            customerConfig =
                PaymentSheet.CustomerConfiguration(response.customer ?: "", response.ephemeralKey ?: "")
            val publishableKey = response.publishableKey
            println("StripeAcc: ${response.stripeAccountId}")
            PaymentConfiguration.init(context, publishableKey ?: "", response.stripeAccountId ?: "")

            if (customerConfig != null && paymentIntentClientSecret != null) {
                presentPaymentSheet(paymentSheet, customerConfig!!, paymentIntentClientSecret!!)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

private fun presentPaymentSheet(
    paymentSheet: PaymentSheet,
    customerConfig: PaymentSheet.CustomerConfiguration,
    paymentIntentClientSecret: String,
) {
    paymentSheet.presentWithPaymentIntent(
        paymentIntentClientSecret,
        PaymentSheet.Configuration(
            merchantDisplayName = "My merchant name",
            customer = customerConfig,
            allowsDelayedPaymentMethods = false,
        ),
    )
}

private fun presentSetupPaymentSheet(
    paymentSheet: PaymentSheet,
    customerConfig: PaymentSheet.CustomerConfiguration,
    setupIntentClientSecret: String,
) {
    paymentSheet.presentWithSetupIntent(
        setupIntentClientSecret,
        PaymentSheet.Configuration(
            merchantDisplayName = "My merchant name",
            customer = customerConfig,
            allowsDelayedPaymentMethods = false,
        ),
    )
}

private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult, navController: NavController) {
    when (paymentSheetResult) {
        is PaymentSheetResult.Canceled -> {
            println("Payment Canceled")
        }
        is PaymentSheetResult.Failed -> {
            println("Payment Error: ${paymentSheetResult.error}")
        }
        is PaymentSheetResult.Completed -> {
            println("Payment Completed")
            navController.navigate(AppRoutes.LIVE_STATUS_SCREEN)
        }
    }
}

private fun onSetupPaymentSheetResult(
    paymentSheetResult: PaymentSheetResult,
    viewModel: SharedViewModel,
    context: Context,
    navController: NavController
) {
    when (paymentSheetResult) {
        is PaymentSheetResult.Canceled -> {
            println("Setup Canceled")
        }
        is PaymentSheetResult.Failed -> {
            println("Setup Error: ${paymentSheetResult.error}")
        }
        is PaymentSheetResult.Completed -> {
            println("Setup Completed")
            confirmSetupIntent(viewModel, context, navController)
        }
    }
}

private fun confirmSetupIntent(viewModel: SharedViewModel, context: Context, navController: NavController) {
    viewModel.confirmSetupIntent(context)
}
