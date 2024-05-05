package com.example.gym_app.screens.common

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.common.Role
import com.example.gym_app.screens.member.GroupTrainingsScreen
import com.example.gym_app.screens.member.GymClassDetailsScreen
import com.example.gym_app.screens.member.LiveStatusScreen
import com.example.gym_app.screens.member.QeCodeScreen
import com.example.gym_app.screens.member.ReviewGymClassScreen
import com.example.gym_app.screens.owner.AccessCodeScreen
import com.example.gym_app.screens.owner.StatisticsScreen
import com.example.gym_app.screens.trainer.CalendarScreen
import com.example.gym_app.screens.trainer.CreateClassScreen
import com.example.gym_app.screens.trainer.ManageClassesScreen
import com.example.gym_app.screens.trainer.TrainerGymClassScreen
import com.example.gym_app.viewModels.GymClassViewModel
import com.example.gym_app.viewModels.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedGetBackStackEntry", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GymHomeScreen(navController: NavController) {
  val oldNavBackStackEntry =
    remember(navController) { navController.getBackStackEntry(AppRoutes.HOME_SCREEN) }
  val viewModel: SharedViewModel = viewModel(oldNavBackStackEntry)
  val gymClassViewModel: GymClassViewModel = viewModel()
  val navHostController: NavHostController = rememberNavController()
  val (selectedTabIndex, setSelectedTabIndex) = remember { mutableStateOf(0) }
  Scaffold(
    topBar = {
      PrimaryScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 0.dp,
        contentColor = contentColorFor(MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.height(48.dp),
      ) {
        gymScreens
          .filter { viewModel.selectedGymUser.value?.roles?.contains(it.role.name) ?: false }
          .forEachIndexed { index, screen ->
            Tab(
              text = { Text(screen.label) },
              selected = selectedTabIndex == index,
              onClick = {
                setSelectedTabIndex(index)
                navHostController.navigate(screen.route)
              },
            )
          }
      }
    }
  ) { innerPadding ->
    val roles = viewModel.selectedGymUser.value?.roles ?: emptyList()
    NavHost(
      navController = navHostController,
      startDestination = AppRoutes.LIVE_STATUS_SCREEN,
      modifier = Modifier.padding(innerPadding),
    ) {
      composable(AppRoutes.GROUP_TRAININGS_SCREEN) {
        GroupTrainingsScreen(
          sharedViewModel = viewModel,
          onGymClassClick = {
            gymClassViewModel.setSelectedGymClass(it)
            navHostController.navigate(AppRoutes.GYM_CLASS_DETAILS_SCREEN)
          },
        ) {
          gymClassViewModel.setSelectedGymClass(it)
          navHostController.navigate(AppRoutes.REVIEW_GYM_CLASS_SCREEN)
        }
      }
      composable(AppRoutes.LIVE_STATUS_SCREEN) { LiveStatusScreen(viewModel) }
      composable(AppRoutes.MANAGE_CLASSES_SCREEN) {
        ManageClassesScreen(
          navHostController,
          viewModel,
          { navHostController.navigate(AppRoutes.CREATE_CLASS_SCREEN) },
        ) {
          gymClassViewModel.setSelectedGymClass(it)
          navHostController.navigate(AppRoutes.TRAINER_GYM_CLASS_SCREEN)
        }
      }
      composable(AppRoutes.TRAINER_GYM_CLASS_SCREEN) {
        TrainerGymClassScreen(navHostController, gymClassViewModel)
      }
      composable(AppRoutes.CALENDAR_SCREEN) { CalendarScreen(viewModel = gymClassViewModel) }
      composable(AppRoutes.REVIEW_GYM_CLASS_SCREEN) {
        ReviewGymClassScreen(gymClassViewModel, viewModel, navHostController)
      }
      composable(AppRoutes.CREATE_CLASS_SCREEN) { CreateClassScreen(navHostController, viewModel) }
      composable(AppRoutes.ACCESS_CODE_SCREEN) { AccessCodeScreen(navHostController, viewModel) }
      composable(AppRoutes.GYM_CLASS_DETAILS_SCREEN) {
        GymClassDetailsScreen(navHostController, gymClassViewModel, viewModel)
      }
      composable(AppRoutes.QR_CODE_SCREEN) { QeCodeScreen(sharedViewModel = viewModel) }
      composable(AppRoutes.STATISTICS_SCREEN) { StatisticsScreen(viewModel) }
    }
  }
}

sealed class GymScreen(val route: String, val label: String, val role: Role) {
  object GroupTrainings :
    GymScreen(AppRoutes.GROUP_TRAININGS_SCREEN, "Group Trainings", Role.ROLE_MEMBER)

  object LiveStatus : GymScreen(AppRoutes.LIVE_STATUS_SCREEN, "Live status", Role.ROLE_MEMBER)

  object ManageClasses :
    GymScreen(AppRoutes.MANAGE_CLASSES_SCREEN, "Manage classes", Role.ROLE_TRAINER)

  object AccessCode : GymScreen(AppRoutes.ACCESS_CODE_SCREEN, "Access code", Role.ROLE_ADMIN)

  object QrCode : GymScreen(AppRoutes.QR_CODE_SCREEN, "Qr code", Role.ROLE_MEMBER)

  object Statistics : GymScreen(AppRoutes.STATISTICS_SCREEN, "Statistics", Role.ROLE_ADMIN)
}

val gymScreens =
  listOf(
    GymScreen.ManageClasses,
    GymScreen.LiveStatus,
    GymScreen.GroupTrainings,
    GymScreen.AccessCode,
    GymScreen.QrCode,
    GymScreen.Statistics,
  )
