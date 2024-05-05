package com.example.gym_app.screens.member

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.common.extractDateAndTime
import com.example.gym_app.common.formatDuration
import com.example.gym_app.screens.trainer.CustomBackground
import com.example.gym_app.viewModels.GymClassViewModel
import com.example.gym_app.viewModels.SharedViewModel
import com.google.android.material.R

@Composable
fun GymClassDetailsScreen(
  navHostController: NavHostController,
  gymClassViewModel: GymClassViewModel,
  viewModel: SharedViewModel,
) {
  // val gymClassDto = gymClassViewModel.selectedGymClass.observeAsState()
  val gymClassInstanceDto = gymClassViewModel.selectedInstanceDto.observeAsState()
  var message by remember { mutableStateOf("") }

  val context = LocalContext.current

  CustomBackground(title = gymClassInstanceDto.value?.name ?: "Unknown") {
    Scaffold(
      modifier =
        Modifier.padding(top = 10.dp).clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp))
    ) { innerPadding ->
      Column(
        modifier =
          Modifier.fillMaxHeight()
            .fillMaxWidth()
            .padding(top = innerPadding.calculateTopPadding(), start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        IconButton(onClick = { navHostController.navigate(AppRoutes.CALENDAR_SCREEN_MEMBER) }) {
          Icon(
            painter =
            painterResource(
              id = R.drawable.material_ic_calendar_black_24dp
            ),
            contentDescription = "View Calendar",
          )
        }
        Text(
          text = gymClassInstanceDto.value?.description ?: "Unknown",
          fontSize = 16.sp,
          modifier = Modifier.padding(top = 32.dp),
        )
        val (date, time) = extractDateAndTime(gymClassInstanceDto.value?.dateTime)
        Row(modifier = Modifier.fillMaxWidth()) {
          Column(
            modifier = Modifier.fillMaxWidth(0.5F),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            val duration = formatDuration(gymClassInstanceDto.value?.duration)
            Column() {
              Text(text = "Date: ${date ?: "Unknown"}", fontSize = 16.sp)
              Text(text = "Duration: $duration", fontSize = 16.sp)
            }
          }
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Column() {
              Text(text = "Time: ${time ?: "Unknown"}", fontSize = 16.sp)
              Text(
                text = "Max participants: ${gymClassInstanceDto.value?.maxParticipants ?: 0}",
                fontSize = 16.sp,
              )
            }
          }
        }

        if (message.isNotEmpty()) {
          Text(text = message, fontSize = 16.sp)
        }
        Button(
          onClick = {
            gymClassViewModel.joinGymClass(
              context = context,
              gymClassId = gymClassInstanceDto.value?.id ?: "",
              onSuccess = { message = "Nice" },
              onError = { msg -> message = msg },
            )
          }
        ) {
          Text(text = "Register!")
        }
      }
    }
  }
}

@Preview
@Composable
fun GymClassDetailsScreenPreview() {
  GymClassDetailsScreen(
    navHostController = rememberNavController(),
    gymClassViewModel = GymClassViewModel(),
    viewModel = SharedViewModel(),
  )
}
