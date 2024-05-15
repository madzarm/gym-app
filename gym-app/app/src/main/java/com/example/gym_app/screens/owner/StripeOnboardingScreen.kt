package com.example.gym_app.screens.owner

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.gym_app.api.ApiClient
import com.example.gym_app.common.TokenManager
import com.example.gym_app.viewModels.SharedViewModel
import kotlinx.coroutines.launch

@Composable
fun OnboardGymOwner(viewModel: SharedViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var accountLinkUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(true) {
        coroutineScope.launch {
            val auth = "Bearer ${TokenManager.getAccessToken(context)}"
            val response = ApiClient.apiService.createAccountLink(
                auth,
                viewModel.selectedGymUser.value?.gym?.id ?: "",
                "gym-app://stripe-return",  // Your app's return URL
                "gym-app://refresh"  // Your app's refresh URL
            )
            accountLinkUrl = response.accountLinkUrl
        }
    }

    accountLinkUrl?.let {
        StripeOnboardingScreen(it)
    }
}

@Composable
fun StripeOnboardingScreen(accountLinkUrl: String) {
    val context = LocalContext.current
    val customTabsIntent = remember { CustomTabsIntent.Builder().build() }

    LaunchedEffect(Unit) {
        customTabsIntent.launchUrl(context, Uri.parse(accountLinkUrl))
    }
}