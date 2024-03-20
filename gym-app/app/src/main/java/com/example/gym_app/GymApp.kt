package com.example.gym_app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.common.TokenManager
import com.example.gym_app.screens.CreateAccountScreen
import com.example.gym_app.screens.CreateGymScreen
import com.example.gym_app.screens.EnterGymCodeScreen
import com.example.gym_app.screens.EnterTrainerAccessCodeScreen
import com.example.gym_app.screens.GymHomeScreen
import com.example.gym_app.screens.HomeScreen
import com.example.gym_app.screens.RoleSelectionScreen
import com.example.gym_app.screens.SignupRoleSelectionScreen
import com.example.gym_app.screens.WelcomeScreen
import com.example.gym_app.viewModels.AuthViewModel

@Composable
fun GymApp(
  navController: NavHostController = rememberNavController(),
  viewModel: AuthViewModel,
  onLoginWithAuthClicked: () -> Unit,
) {
  val viewModelStoreOwner =
    checkNotNull(LocalViewModelStoreOwner.current) {
      "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
  NavHost(
    navController = navController,
    startDestination =
      if (TokenManager.isTokenActive(LocalContext.current)) {
        AppRoutes.HOME
      } else {
        TokenManager.removeToken(LocalContext.current)
        AppRoutes.WELCOME_SCREEN
      },
  ) {
    composable(AppRoutes.WELCOME_SCREEN) {
      CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
        WelcomeScreen(
          navController = navController,
          onLoginWithAuthClicked = onLoginWithAuthClicked,
        )
      }
    }
    navigation(startDestination = AppRoutes.HOME_SCREEN, route = AppRoutes.HOME) {
      composable(AppRoutes.HOME_SCREEN) {
        HomeScreen(
          navController = navController,
          onAddGymClicked = { navController.navigate(AppRoutes.ROLE_SELECTION_SCREEN) },
        )
      }
      composable(AppRoutes.ROLE_SELECTION_SCREEN) {
        RoleSelectionScreen(
          onMemberSelection = { navController.navigate(AppRoutes.ENTER_GYM_CODE_SCREEN) },
          onTrainerSelection = {
            navController.navigate(AppRoutes.ENTER_TRAINER_ACCESS_CODE_SCREEN)
          },
          onOwnerSelection = { navController.navigate(AppRoutes.CREATE_GYM_SCREEN) },
        )
      }
      composable(AppRoutes.ENTER_GYM_CODE_SCREEN) {
        EnterGymCodeScreen(
          navController = navController,
          onSubmit = {
            navController.navigate(AppRoutes.HOME_SCREEN) {
              popUpTo(AppRoutes.HOME_SCREEN) { inclusive = true }
            }
          },
        )
      }
      composable(AppRoutes.ENTER_TRAINER_ACCESS_CODE_SCREEN) {
        EnterTrainerAccessCodeScreen(
          navController = navController,
          onSubmit = {
            navController.navigate(AppRoutes.HOME_SCREEN) {
              popUpTo(AppRoutes.HOME_SCREEN) { inclusive = true }
            }
          },
        )
      }
      composable(AppRoutes.CREATE_GYM_SCREEN) {
        CreateGymScreen(
          navController = navController,
          onSubmit = {
            navController.navigate(AppRoutes.HOME_SCREEN) {
              popUpTo(AppRoutes.HOME_SCREEN) { inclusive = true }
            }
          },
        )
      }
     composable(AppRoutes.GYM_HOME_SCREEN) { GymHomeScreen(navController = navController) }
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
