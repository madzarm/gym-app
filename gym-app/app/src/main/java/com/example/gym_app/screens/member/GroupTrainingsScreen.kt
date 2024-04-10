package com.example.gym_app.screens.member

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.common.localDateTimeFromString
import com.example.gym_app.screens.trainer.CustomBackground
import com.example.gym_app.screens.trainer.GymClass
import com.example.gym_app.viewModels.SharedViewModel
import org.gymapp.library.response.GymClassDto
import java.time.LocalDateTime

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun GroupTrainingsScreen(sharedViewModel: SharedViewModel, onClick: (GymClassDto) -> Unit) {
  val context = LocalContext.current

  val selectedGymUser = sharedViewModel.selectedGymUser.value
  val gymClasses = sharedViewModel.gymClasses.observeAsState()

  LaunchedEffect(true) { sharedViewModel.getGymClasses(context, selectedGymUser?.gym?.id ?: "") }

  CustomBackground(title = "All classes") {

    val listState = rememberLazyListState()

    Scaffold(
      modifier =
      Modifier.padding(top = 10.dp).clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp)),
    ) { padding ->
      LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        val gymClassesList: List<GymClassDto> = filterOldClasses(gymClasses.value ?: emptyList())
        items(gymClassesList) { gymClass -> GymClass(gymClass, onClick) }
      }
    }
  }
}

fun filterOldClasses(gymClasses: List<GymClassDto>): List<GymClassDto> {
  return gymClasses.filter { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    localDateTimeFromString(it.dateTime!!).isAfter(LocalDateTime.now())
  } else {
    TODO("VERSION.SDK_INT < O")
  }
  }
}

@Preview
@Composable
fun GroupTrainingsScreenPreview() {
  GroupTrainingsScreen(sharedViewModel = SharedViewModel(), {})
}
