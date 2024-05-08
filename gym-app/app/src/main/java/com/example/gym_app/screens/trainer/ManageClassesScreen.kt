package com.example.gym_app.screens.trainer

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.common.extractDateAndTime
import com.example.gym_app.common.formatDuration
import com.example.gym_app.viewModels.SharedViewModel
import com.google.android.material.R
import org.gymapp.library.response.GymClassDto

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedContentLambdaTargetStateParameter")
@Composable
fun ManageClassesScreen(
  navController: NavController,
  viewModel: SharedViewModel,
  onCreateClick: () -> Unit,
  onClick: (GymClassDto) -> Unit,
) {

  val context = LocalContext.current
  LaunchedEffect(true) { viewModel.getTrainerGymClasses(context) }
  val gymClasses = viewModel.gymClasses.observeAsState()

  CustomBackground(title = "Your classes") {
    IconButton(
      onClick = { navController.navigate(AppRoutes.CALENDAR_SCREEN_ALL_CLASSES_TRAINER) }
    ) {
      Icon(
        painter = painterResource(id = R.drawable.material_ic_calendar_black_24dp),
        contentDescription = "View Calendar",
      )
    }

    val listState = rememberLazyListState()
    val expandedFab by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    Scaffold(
      modifier =
        Modifier.padding(top = 10.dp).clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp)),
      floatingActionButton = {
        ExtendedFloatingActionButton(
          onClick = onCreateClick,
          expanded = expandedFab,
          icon = { Icon(Icons.Filled.Add, "Create a class button") },
          text = { Text(text = "Create a class") },
        )
      },
      floatingActionButtonPosition = FabPosition.End,
    ) {
      LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        val gymClassesList: List<GymClassDto> = gymClasses.value ?: emptyList()
        items(gymClassesList) { gymClass -> GymClass(gymClass, onClick) }
      }
    }
  }
}

@Composable
fun GymClass(gymClass: GymClassDto, onClick: (GymClassDto) -> Unit) {
  TextButton(
    onClick = { onClick(gymClass) },
    modifier = Modifier.fillMaxWidth(0.84F).padding(top = 18.dp),
    shape = RoundedCornerShape(20.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier =
        Modifier.clip(RoundedCornerShape(20.dp))
          .background(
            brush = Brush.horizontalGradient(colors = listOf(Color(0xFF00d4ff), Color(0xFF0051bf)))
          )
          .fillMaxWidth()
          .fillMaxSize(),
    ) {
      Box(modifier = Modifier.fillMaxWidth()) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(20.dp),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          val (date, time) = extractDateAndTime(gymClass.dateTime)
          val duration = formatDuration(gymClass.duration)
          Text(
            textAlign = TextAlign.Center,
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp),
            fontWeight = FontWeight.Bold,
            color = Color.White,
            text = gymClass.name ?: "Unknown",
            lineHeight = 30.sp,
          )
          Text(color = Color.White, text = "Date: ${date ?: "Unknown"}")
          Text(color = Color.White, text = "Time: ${time ?: "Unknown"}")
          Text(color = Color.White, text = "Duration ${duration ?: "Unknown"}")
        }
        if (gymClass.isRecurring) {
          Text(
            text = "Recurring class",
            color = Color.White,
            modifier =
            Modifier.align(Alignment.TopEnd)
              .padding(end = 8.dp, top = 8.dp),
          )
        }

      }
    }
  }
}

@Composable
fun CustomBackground(title: String, content: @Composable () -> Unit) {
  Column(
    modifier =
      Modifier.fillMaxSize()
        .background(
          brush = Brush.horizontalGradient(colors = listOf(Color(0xFF00d4ff), Color(0xFF0051bf)))
        ),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Box(modifier = Modifier.fillMaxWidth().padding(end = 10.dp, start = 10.dp, top = 10.dp)) {
      Text(
        text = title,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.align(Alignment.Center),
        textAlign = TextAlign.Center,
        lineHeight = 30.sp,
      )
    }
    content()
  }
}
