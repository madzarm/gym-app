package com.example.gym_app.screens.member

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gym_app.common.extractDateAndTime
import com.example.gym_app.common.formatDuration
import com.example.gym_app.common.localDateTimeFromString
import com.example.gym_app.screens.trainer.CustomBackground
import com.example.gym_app.screens.trainer.GymClass
import com.example.gym_app.viewModels.SharedViewModel
import kotlinx.datetime.DayOfWeek
import java.time.LocalDateTime
import org.gymapp.library.response.GymClassDto
import org.gymapp.library.response.GymClassInstanceDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

sealed class ListItem {
  data class GymClassItem(val gymClassDto: GymClassInstanceDto) : ListItem()

  data class GymClassReviewItem(val gymClassDto: GymClassInstanceDto) : ListItem()
}

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun GroupTrainingsScreen(sharedViewModel: SharedViewModel, onGymClassClick: (GymClassInstanceDto, GymClassDto) -> Unit, onGymClassReviewClick: (GymClassInstanceDto) -> Unit) {
  val context = LocalContext.current

  val selectedGymUser = sharedViewModel.selectedGymUser.value
  val gymClasses = sharedViewModel.gymClasses.observeAsState()
  val gymClassesForReview = sharedViewModel.gymClassesForReview.observeAsState()

  LaunchedEffect(true) {
    sharedViewModel.getGymClasses(context, selectedGymUser?.gym?.id ?: "")
    sharedViewModel.getGymClassesForReview(context, selectedGymUser?.gym?.id ?: "")
  }

  CustomBackground(title = "All classes") {
    val listState = rememberLazyListState()

    Scaffold(
      modifier =
        Modifier.padding(top = 10.dp).clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp))
    ) { padding ->
      LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        val combinedList =
          combineLists(
            filterOldClasses(gymClasses.value ?: emptyList()),
            gymClassesForReview.value ?: emptyList(),
          )

        items(combinedList) { item ->
          when (item) {
            is ListItem.GymClassItem -> GymClassInstance(item.gymClassDto,gymClasses.value?.find { it.id == item.gymClassDto.classId }!!, onGymClassClick, )
            is ListItem.GymClassReviewItem -> GymClassReview(item.gymClassDto, onGymClassReviewClick)
          }
        }
      }
    }
  }
}

fun combineLists(
  gymClasses: List<GymClassInstanceDto>,
  gymClassReviews: List<GymClassInstanceDto>,
): List<ListItem> {
  val combinedList = mutableListOf<ListItem>()
  combinedList.addAll(gymClasses.map { ListItem.GymClassItem(it) })
  combinedList.addAll(gymClassReviews.map { ListItem.GymClassReviewItem(it) })
  return combinedList
}

fun filterOldClasses(gymClasses: List<GymClassDto>): List<GymClassInstanceDto> {
  return gymClasses.flatMap { gymClass ->
    val instances = mutableListOf<GymClassInstanceDto>()
    if (gymClass.isRecurring && gymClass.recurringPattern != null) {

      // Calculate the next occurrence
      val nextClassDateTime = calculateNextOccurrenceDateTime(gymClass)

      // Check if there's already an existing instance for the next occurrence
      val existingInstance = gymClass.instances.find {
        LocalDateTime.parse(it.dateTime).toLocalDate() == nextClassDateTime.toLocalDate()
      }
      if (existingInstance != null) {
        instances.add(existingInstance)
      } else if (nextClassDateTime.isAfter(LocalDateTime.now())) {

        // If no existing instance, create a new one for future occurrences
        instances.add(createInstanceFromGymClass(gymClass, nextClassDateTime))
      }
    } else {

      // Handle non-recurring classes
      val dateTime = LocalDateTime.parse(gymClass.dateTime)
      if (dateTime.isAfter(LocalDateTime.now())) {
        instances.add(createInstanceFromGymClass(gymClass, dateTime))
      }
    }
    instances
  }
}

fun calculateNextOccurrenceDateTime(gymClass: GymClassDto): LocalDateTime {
  val now = LocalDateTime.now()
  var nextDateTime = LocalDateTime.parse(gymClass.dateTime)
  val daysOfWeek = gymClass.recurringPattern!!.dayOfWeeks.map { DayOfWeek.of(it + 1) }

  // Ensure the next occurrence is actually in the future
  if (nextDateTime.isBefore(now)) {
    while (nextDateTime.isBefore(now)) {
      for (dayOfWeek in daysOfWeek) {
        nextDateTime = nextDateTime.with(TemporalAdjusters.next(dayOfWeek))
        if (nextDateTime.isAfter(now)) {
          return nextDateTime
        }
      }
    }
  }
  return nextDateTime
}

fun createInstanceFromGymClass(gymClass: GymClassDto, dateTime: LocalDateTime): GymClassInstanceDto {
  return GymClassInstanceDto(
    id = "",
    classId = gymClass.id ?: "",
    name = gymClass.name ?: "",
    description = gymClass.description ?: "",
    dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    duration = gymClass.duration ?: "0",
    maxParticipants = gymClass.maxParticipants ?: "0",
    participantsIds = listOf(),
    trainerId = gymClass.trainerId,
    gymClassModifiedInstance = null
  )
}

@Composable
fun GymClassInstance(gymClass: GymClassInstanceDto, gymClassDto: GymClassDto, onClick: (GymClassInstanceDto, GymClassDto) -> Unit) {
  TextButton(
    onClick = { onClick(gymClass, gymClassDto) },
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
      Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        val (date, time) = extractDateAndTime(gymClass.dateTime)
        val duration = formatDuration(gymClass.duration)
        Text(
          fontSize = 32.sp,
          modifier = Modifier.padding(bottom = 8.dp),
          fontWeight = FontWeight.Bold,
          color = Color.White,
          text = gymClass.name ?: "Unknown",
          lineHeight = 30.sp,
        )
        Text(color = Color.White, text = "Date: ${date ?: "Unknown"}")
        Text(color = Color.White, text = "Time: ${time ?: "Unknown"}")
        Text(color = Color.White, text = "Duration ${duration ?: "Unknown"}")
      }
    }
  }
}

@Composable
fun GymClassReview(gymClass: GymClassInstanceDto, onClick: (GymClassInstanceDto) -> Unit) {
  TextButton(
    onClick = {
      onClick(gymClass)
              },
    modifier = Modifier.fillMaxWidth(0.84F).padding(top = 18.dp),
    shape = RoundedCornerShape(20.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier =
        Modifier.clip(RoundedCornerShape(20.dp))
          .background(
            brush = Brush.horizontalGradient(colors = listOf(Color(0xFFd9dbdb), Color(0xFFc7c9c9)))
          )
          .fillMaxWidth()
          .fillMaxSize(),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        val (date, time) = extractDateAndTime(gymClass.dateTime)
        Text(
          fontSize = 32.sp,
          modifier = Modifier.padding(bottom = 8.dp),
          fontWeight = FontWeight.Bold,
          color = Color(0xFF666666),
          text = gymClass.name ?: "Unknown",
          lineHeight = 30.sp,
        )
        Text(color = Color(0xFF666666), text = "Date: ${date ?: "Unknown"}")
        Text(color = Color(0xFF666666), text = "Time: ${time ?: "Unknown"}")
        Text(color = Color(0xFFffbb00), fontSize = 20.sp, text = "Click to leave a review! ★")
      }
    }
  }
}
