package com.example.gym_app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.screens.CreateAccountScreen
import com.example.gym_app.screens.SignupRoleSelectionScreen
import com.example.gym_app.screens.WelcomeScreen

@Composable
fun GymApp() {
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = "WelcomeScreen") {
    composable(AppRoutes.WELCOME_SCREEN) { WelcomeScreen(navController = navController) }
    composable(AppRoutes.SIGNUP_ROLE_SELECTION_SCREEN) {
      SignupRoleSelectionScreen(navController = navController)
    }
    composable(AppRoutes.CREATE_ACCOUNT_SCREEN) { CreateAccountScreen(navController = navController) }
  }
}
