package com.example.gym_app.screens.common

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.gym_app.screens.member.InviteChallengeScreen
import com.example.gym_app.screens.member.LiveStatusScreen
import com.example.gym_app.screens.member.MemberChallengesScreen
import com.example.gym_app.screens.member.QeCodeScreen
import com.example.gym_app.screens.member.ReviewGymClassScreen
import com.example.gym_app.screens.member.SubscriptionScreen
import com.example.gym_app.screens.owner.AccessCodeScreen
import com.example.gym_app.screens.owner.ChallengeDetailsScreen
import com.example.gym_app.screens.owner.CreateChallengeScreen
import com.example.gym_app.screens.owner.ManageChallengesScreen
import com.example.gym_app.screens.owner.OnboardGymOwner
import com.example.gym_app.screens.owner.StatisticsScreen
import com.example.gym_app.screens.trainer.CalendarScreen
import com.example.gym_app.screens.trainer.CreateClassScreen
import com.example.gym_app.screens.trainer.ManageClassesScreen
import com.example.gym_app.screens.trainer.TrainerGymClassInstanceScreen
import com.example.gym_app.screens.trainer.TrainerGymClassScreen
import com.example.gym_app.viewModels.GymClassViewModel
import com.example.gym_app.viewModels.SharedViewModel
import kotlinx.coroutines.isActive
import org.gymapp.library.response.GymClassDto
import org.gymapp.library.response.GymClassInstanceDto

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
  val startDestination = determineStartDestination(viewModel)

  if (startDestination.isEmpty()) {
    CircularProgressIndicator()
  } else {
    println("Start dest -> $startDestination")
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

      NavHost(
        navController = navHostController,
        startDestination = startDestination,
        modifier = Modifier.padding(innerPadding),
      ) {
        composable(AppRoutes.STRIPE_ONBOARD_SCREEN) { OnboardGymOwner(viewModel) }
        composable(AppRoutes.GROUP_TRAININGS_SCREEN) {
          GroupTrainingsScreen(
            navHostController = navHostController,
            sharedViewModel = viewModel,
            onGymClassClick = { gymClassInstanceDto: GymClassInstanceDto, gymClassDto: GymClassDto ->
              gymClassViewModel.setSelectedGymClass(gymClassDto)
              gymClassViewModel.setSelectedInstanceDto(gymClassInstanceDto)
              navHostController.navigate(AppRoutes.GYM_CLASS_DETAILS_SCREEN)
            },
          ) { gymClassInstanceDto: GymClassInstanceDto ->
            gymClassViewModel.setSelectedInstanceDto(gymClassInstanceDto)
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
            if (!it.isRecurring) {
              val instance = it.instances?.firstOrNull()
              gymClassViewModel.updateInstance {
                copy(
                  name = instance?.name ?: "",
                  description = instance?.description ?: "",
                  duration = instance?.duration ?: "",
                  maxParticipants = instance?.maxParticipants ?: "",
                  dateTime = instance?.dateTime ?: "",
                  participantsIds = instance?.participantsIds ?: emptyList(),
                )
              }
              navHostController.navigate(AppRoutes.GYM_CLASS_INSTANCE_SCREEN)
            } else {
              navHostController.navigate(AppRoutes.TRAINER_GYM_CLASS_SCREEN)
            }
          }
        }
        composable(AppRoutes.TRAINER_GYM_CLASS_SCREEN) {
          TrainerGymClassScreen(navHostController, gymClassViewModel)
        }
        composable(AppRoutes.CALENDAR_SCREEN) {
          CalendarScreen(navHostController = navHostController, viewModel = gymClassViewModel)
        }
        composable(AppRoutes.CALENDAR_SCREEN_MEMBER) {
          CalendarScreen(navHostController = navHostController, viewModel = gymClassViewModel) {
            navHostController.navigate(AppRoutes.GYM_CLASS_DETAILS_SCREEN)
          }
        }
        composable(AppRoutes.CALENDAR_SCREEN_ALL_CLASSES_MEMBER) {
          CalendarScreen(
            navHostController = navHostController,
            viewModel = gymClassViewModel,
            sharedViewModel = viewModel,
            shouldUseAllData = true,
          ) {
            navHostController.navigate(AppRoutes.GYM_CLASS_DETAILS_SCREEN)
          }
        }
        composable(AppRoutes.CALENDAR_SCREEN_ALL_CLASSES_TRAINER) {
          CalendarScreen(
            navHostController = navHostController,
            viewModel = gymClassViewModel,
            sharedViewModel = viewModel,
            shouldUseAllData = true,
          ) {
            navHostController.navigate(AppRoutes.GYM_CLASS_INSTANCE_SCREEN)
          }
        }
        composable(AppRoutes.REVIEW_GYM_CLASS_SCREEN) {
          ReviewGymClassScreen(gymClassViewModel, viewModel, navHostController)
        }
        composable(AppRoutes.SUBSCRIPTION_SCREEN) {
          SubscriptionScreen(viewModel, navHostController)
        }
        composable(AppRoutes.CHALLENGE_DETAILS_SCREEN) {
          ChallengeDetailsScreen(navHostController, viewModel)
        }
        composable(AppRoutes.INVITE_CHALLENGE_SCREEN) {
          InviteChallengeScreen(navHostController, viewModel)
        }
        composable(AppRoutes.MEMBER_CHALLENGES_SCREEN) {
          MemberChallengesScreen(navHostController, viewModel)
        }
        composable(AppRoutes.CREATE_CHALLENGE_SCREEN) {
          CreateChallengeScreen(navHostController, viewModel)
        }
        composable(AppRoutes.MANAGE_CHALLENGES_SCREEN) {
          ManageChallengesScreen(navHostController, viewModel)
        }
        composable(AppRoutes.GYM_CLASS_INSTANCE_SCREEN) {
          TrainerGymClassInstanceScreen(
            navHostController = navHostController,
            viewModel = gymClassViewModel,
          )
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



}

sealed class GymScreen(val route: String, val label: String, val role: Role) {
  object GroupTrainings :
    GymScreen(AppRoutes.GROUP_TRAININGS_SCREEN, "Group Trainings", Role.ROLE_MEMBER)

  object LiveStatus : GymScreen(AppRoutes.LIVE_STATUS_SCREEN, "Live Traffic", Role.ROLE_MEMBER)

  object ManageClasses :
    GymScreen(AppRoutes.MANAGE_CLASSES_SCREEN, "Manage classes", Role.ROLE_TRAINER)

  object AccessCode : GymScreen(AppRoutes.ACCESS_CODE_SCREEN, "Access code", Role.ROLE_ADMIN)

  object QrCode : GymScreen(AppRoutes.QR_CODE_SCREEN, "Qr code", Role.ROLE_MEMBER)

  object Statistics : GymScreen(AppRoutes.STATISTICS_SCREEN, "Statistics", Role.ROLE_ADMIN)

  object ManageChallenges :
    GymScreen(AppRoutes.MANAGE_CHALLENGES_SCREEN, "Manage Challenges", Role.ROLE_ADMIN)

  object MemberChallenges :
    GymScreen(AppRoutes.MEMBER_CHALLENGES_SCREEN, "Challenges", Role.ROLE_MEMBER)
}

val gymScreens =
  listOf(
    GymScreen.ManageClasses,
    GymScreen.LiveStatus,
    GymScreen.GroupTrainings,
    GymScreen.MemberChallenges,
    GymScreen.AccessCode,
    GymScreen.QrCode,
    GymScreen.Statistics,
    GymScreen.ManageChallenges,
  )

@Composable
fun determineStartDestination(viewModel: SharedViewModel = viewModel()): String {
  val context = LocalContext.current
  var startDestination by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(true) }
  val subscriptionStatus by viewModel.subscriptionStatus.observeAsState()

  LaunchedEffect(subscriptionStatus) {
    val isAdmin = viewModel.selectedGymUser.value?.roles?.contains(Role.ROLE_ADMIN.name) ?: false
    val isMember = viewModel.selectedGymUser.value?.roles?.contains(Role.ROLE_MEMBER.name) ?: false
    val isTrainer = viewModel.selectedGymUser.value?.roles?.contains(Role.ROLE_TRAINER.name) ?: false
    var isCompleted = true;
    if (isAdmin) {
      isCompleted = viewModel.isStripeConnectAccountCompleted(context)
    }
    if (isMember) {
      viewModel.getSubscriptionStatus(context)
    }

    val loading = subscriptionStatus?.loading
    val isUserSubscribed = subscriptionStatus?.subscribed
    startDestination = when {
      !isCompleted -> AppRoutes.STRIPE_ONBOARD_SCREEN
      isMember && (isUserSubscribed == false) && (loading == false) -> AppRoutes.SUBSCRIPTION_SCREEN
      isMember -> AppRoutes.LIVE_STATUS_SCREEN
      isTrainer -> AppRoutes.MANAGE_CLASSES_SCREEN
      else -> AppRoutes.STATISTICS_SCREEN
    }
    isLoading = false
  }

  return if (isLoading) {
    ""
  } else {
    startDestination ?: ""
  }
}
