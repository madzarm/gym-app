package com.example.gym_app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.common.TokenManager
import com.example.gym_app.screens.CreateAccountScreen
import com.example.gym_app.screens.SignupRoleSelectionScreen
import com.example.gym_app.screens.WelcomeScreen
import com.example.gym_app.viewModels.AuthViewModel

@Composable
fun GymApp(
    navController: NavHostController = rememberNavController(),
    viewModel: AuthViewModel,
    onLoginWithAuthClicked: () -> Unit
) {
  val viewModelStoreOwner =
      checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
      }
  NavHost(
      navController = navController,
      startDestination =
          if (TokenManager.isTokenActive(LocalContext.current)) {
            AppRoutes.CREATE_ACCOUNT_SCREEN
          } else {
            AppRoutes.WELCOME_SCREEN
          }) {
        composable(AppRoutes.WELCOME_SCREEN) {
          CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
            WelcomeScreen(
                navController = navController, onLoginWithAuthClicked = onLoginWithAuthClicked)
          }
        }

        composable(AppRoutes.SIGNUP_ROLE_SELECTION_SCREEN) {
          SignupRoleSelectionScreen(navController = navController)
        }
        composable(AppRoutes.CREATE_ACCOUNT_SCREEN) {
          CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
            CreateAccountScreen(navController = navController)
          }
        }
      }
}
