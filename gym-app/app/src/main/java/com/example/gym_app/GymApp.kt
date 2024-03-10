package com.example.gym_app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.screens.WelcomeScreen


@Composable
fun GymApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "WelcomeScreen") {
        composable("WelcomeScreen") { WelcomeScreen(navController = navController) }
    }
}