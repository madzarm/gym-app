package com.example.gym_app

import android.content.Context
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.common.NotificationPermissionScreen
import com.example.gym_app.common.TokenManager
import com.example.gym_app.common.isNotificationPermissionGranted
import com.example.gym_app.screens.common.CreateAccountScreen
import com.example.gym_app.screens.common.GymHomeScreen
import com.example.gym_app.screens.common.HomeScreen
import com.example.gym_app.screens.common.ProfileScreen
import com.example.gym_app.screens.common.ProfileSetupScreen
import com.example.gym_app.screens.common.RoleSelectionScreen
import com.example.gym_app.screens.common.SignupRoleSelectionScreen
import com.example.gym_app.screens.common.WelcomeScreen
import com.example.gym_app.screens.member.EnterFriendCodeScreen
import com.example.gym_app.screens.member.EnterGymCodeScreen
import com.example.gym_app.screens.owner.CreateGymScreen
import com.example.gym_app.screens.trainer.EnterTrainerAccessCodeScreen
import com.example.gym_app.viewModels.AuthViewModel
import com.example.gym_app.viewModels.HomeViewModel
import com.example.gym_app.viewModels.HomeViewModelFactory
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.gymapp.library.response.UserDto

@Composable
fun GymApp(
  navController: NavHostController = rememberNavController(),
  viewModel: AuthViewModel,
  onLoginWithAuthClicked: () -> Unit,
) {

  val context = LocalContext.current
  val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(LocalContext.current))
  val currentUser = homeViewModel.currentUser.collectAsState()

  LaunchedEffect(Unit) { homeViewModel.loadItems(TokenManager.getAccessToken(context) ?: "") }

  val viewModelStoreOwner =
    checkNotNull(LocalViewModelStoreOwner.current) {
      "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

  FirebaseMessaging.getInstance()
    .token
    .addOnCompleteListener(
      OnCompleteListener { task ->

        // Get new FCM registration token
        val token = task.result
        println("Token is -> " + token)
        println("----------------------")
      }
    )

  val isLoggedIn = viewModel.isLoggedIn.observeAsState()
  val isLoading = homeViewModel.isLoading.observeAsState()
  if (isLoading.value ?: true) {
    CircularProgressIndicator()
  } else {
    // Navigation setup only when items are loaded
    NavHost(
      navController = navController,
      startDestination = determineStartDestination(context, isLoggedIn.value, currentUser.value),
    ) {
      composable(AppRoutes.WELCOME_SCREEN) {
        CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
          WelcomeScreen(
            navController = navController,
            onLoginWithAuthClicked = onLoginWithAuthClicked,
          )
        }
      }
      composable(AppRoutes.PROFILE_SETUP_SCREEN) {
        ProfileSetupScreen(navHostController = navController, viewModel = homeViewModel)
      }
      composable(AppRoutes.REQUEST_PERMISSION_SCREEN) { NotificationPermissionScreen() }
      navigation(startDestination = AppRoutes.HOME_SCREEN, route = AppRoutes.HOME) {
        composable(AppRoutes.HOME_SCREEN) {
          HomeScreen(
            navController = navController,
            onAddGymClicked = { navController.navigate(AppRoutes.ROLE_SELECTION_SCREEN) },
            homeViewModel = homeViewModel,
          )
        }

        composable(AppRoutes.PROFILE_SCREEN) {
          ProfileScreen(
            navHostController = navController,
            viewModel = homeViewModel,
            onLogout = {
              TokenManager.removeToken(context)
              viewModel.setLoggedIn(false)
              navController.navigate(AppRoutes.WELCOME_SCREEN) {
                popUpTo(AppRoutes.WELCOME_SCREEN) { inclusive = true }
              }
            },
          )
        }
        composable(AppRoutes.ROLE_SELECTION_SCREEN) {
          RoleSelectionScreen(
            onMemberSelection = { navController.navigate(AppRoutes.ENTER_GYM_CODE_SCREEN) },
            onTrainerSelection = {
              navController.navigate(AppRoutes.ENTER_TRAINER_ACCESS_CODE_SCREEN)
            },
            onOwnerSelection = { navController.navigate(AppRoutes.CREATE_GYM_SCREEN) },
            onFriendCodeSelection = { navController.navigate(AppRoutes.ENTER_FRIEND_CODE_SCREEN) },
          )
        }
        composable(AppRoutes.ENTER_GYM_CODE_SCREEN) {
          EnterGymCodeScreen(
            navController = navController,
            homeViewModel = homeViewModel,
            onSubmit = {
              navController.navigate(AppRoutes.HOME_SCREEN) {
                popUpTo(AppRoutes.HOME_SCREEN) { inclusive = true }
              }
            },
          )
        }
        composable(AppRoutes.ENTER_FRIEND_CODE_SCREEN) {
          EnterFriendCodeScreen(
            navController = navController,
            homeViewModel = homeViewModel,
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
            homeViewModel = homeViewModel,
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
            homeViewModel = homeViewModel,
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
}

@Composable
fun determineStartDestination(
  context: Context,
  isLoggedIn: Boolean?,
  currentUser: UserDto?,
): String {
  if (!isNotificationPermissionGranted(context)) {
    return AppRoutes.REQUEST_PERMISSION_SCREEN
  } else if (TokenManager.isTokenActive(context) || isLoggedIn == true) {
    return if (currentUser?.firstName.isNullOrEmpty()) AppRoutes.PROFILE_SETUP_SCREEN
    else AppRoutes.HOME
  } else {
    return AppRoutes.WELCOME_SCREEN
  }
}
