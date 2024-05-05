package com.example.gym_app.screens.trainer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.common.extractDateAndTime
import com.example.gym_app.viewModels.GymClassViewModel
import com.example.gym_app.viewModels.SharedViewModel
import java.time.LocalDateTime
import java.util.Calendar

@Composable
fun CreateClassScreen(navHostController: NavHostController, sharedViewModel: SharedViewModel) {
  val viewModel: GymClassViewModel = viewModel()
  val request = viewModel.createGymClassRequest.observeAsState()
  val gymUser = sharedViewModel.selectedGymUser.observeAsState()
  val isRecurring = remember { mutableStateOf(false) }
  val selectedDays = remember { mutableStateListOf<Int>() }
  val recurringRequest = viewModel.createRecurringGymClassRequest.observeAsState()

  var errorMessage by remember { mutableStateOf("") }

  val context = LocalContext.current

  CustomBackground(title = "Create a class") {
    Scaffold(
      modifier =
        Modifier.padding(top = 10.dp).clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp))
    ) { innerPadding ->
      Column(
        modifier =
          Modifier.fillMaxHeight()
            .fillMaxWidth()
            .padding(top = innerPadding.calculateTopPadding(), start = 56.dp, end = 56.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        OutlinedTextField(
          modifier = Modifier,
          value = request.value?.name ?: "",
          label = { Text(text = "Name") },
          onValueChange = { viewModel.updateRequest { copy(name = it) } },
        )
        OutlinedTextField(
          modifier = Modifier,
          value = request.value?.description ?: "",
          label = { Text(text = "Description") },
          onValueChange = { viewModel.updateRequest { copy(description = it) } },
        )
        OutlinedTextField(
          modifier = Modifier,
          value = request.value?.duration ?: "",
          label = { Text(text = "Duration in Minutes") },
          onValueChange = { viewModel.updateRequest { copy(duration = it) } },
        )
        OutlinedTextField(
          modifier = Modifier,
          value =
            if (request.value?.maxParticipants == null) ""
            else request.value?.maxParticipants!!.toString(),
          label = { Text(text = "Max participants") },
          onValueChange = { viewModel.updateRequest { copy(maxParticipants = it.toInt()) } },
        )
        ShowDatePickerCreate(viewModel)
        ShowTimePickerCreate(viewModel)

        Row(verticalAlignment = Alignment.CenterVertically) {
          Checkbox(checked = isRecurring.value, onCheckedChange = { isRecurring.value = it })
          Text("Is Recurring Class")
        }

        if (isRecurring.value) {
          Text("Select Days of the Week")
          val dateTime = LocalDateTime.parse(request.value?.dateTime)
          val dayOfWeek = dateTime.dayOfWeek.value - 1 % 7
          selectedDays.find { it == dayOfWeek } ?: selectedDays.add(dayOfWeek)
          WeekDaysSelector(selectedDays = selectedDays, dateTimeStr = request.value?.dateTime ?: "")

          OutlinedTextField(
            value = recurringRequest.value?.maxNumOfOccurrences?.toString() ?: "",
            onValueChange = {
              viewModel.updateRecurringClassRequest {
                copy(maxNumOfOccurrences = Integer.parseInt(it))
              }
            },
            label = { Text("Max Number of Occurrences") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          )
        }

        if (errorMessage.isNotEmpty()) {
          Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 16.dp),
          )
        }
        Button(
          onClick = {
            if (!isRecurring.value) {
              viewModel.createGymClass(
                context,
                gymUser.value?.gym?.id ?: "",
                onSuccess = {
                  Toast.makeText(context, "Successfully created a class!", Toast.LENGTH_LONG).show()
                  navHostController.popBackStack()
                },
                onError = { msg -> errorMessage = msg },
              )
            } else {
              viewModel.updateRecurringClassRequest { copy(daysOfWeek = selectedDays) }
              viewModel.createRecurringGymClass(
                context,
                gymUser.value?.gym?.id ?: "",
                onSuccess = {
                  Toast.makeText(context, "Successfully created a class!", Toast.LENGTH_LONG).show()
                  navHostController.popBackStack()
                },
                onError = { msg -> errorMessage = msg },
              )
            }
          },
          modifier = Modifier.padding(top = 16.dp),
        ) {
          Text(text = "Create")
        }
      }
    }
  }
}

@Composable
fun WeekDaysSelector(selectedDays: SnapshotStateList<Int>, dateTimeStr: String) {
  val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
  val dateTime = LocalDateTime.parse(dateTimeStr)
  val dayOfWeek = dateTime.dayOfWeek.value - 1 % 7 // Adjust to match your day indexing

  Row(
    modifier = Modifier.horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.Start,
  ) {
    daysOfWeek.forEachIndexed { index, day ->
      DayOfWeekButton(day, index, selectedDays, disabled = index == dayOfWeek)
    }
  }
}

@Composable
fun DayOfWeekButton(
  day: String,
  index: Int,
  selectedDays: SnapshotStateList<Int>,
  disabled: Boolean,
) {
  val isSelected = remember { mutableStateOf(index in selectedDays) }

  Button(
    onClick = {
      if (!disabled) {
        if (index in selectedDays) selectedDays.remove(index) else selectedDays.add(index)
        isSelected.value = index in selectedDays
      }
    },
    enabled = !disabled,
    colors =
      ButtonDefaults.buttonColors(
        if (isSelected.value || disabled) MaterialTheme.colorScheme.primary else Color(0xFFbfbfbf),
        disabledContainerColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = Color.White,
        contentColor = Color.White,
      ),
    modifier =
      Modifier.border(width = Dp.Hairline, color = Color.Black, shape = RectangleShape)
        .background(
          if (isSelected.value || disabled) MaterialTheme.colorScheme.primary else Color(0xFFbfbfbf)
        )
        .padding(0.dp),
  ) {
    Text(day, color = if (disabled) Color.Gray else Color.White)
  }
}

@Composable
fun ShowTimePickerCreate(viewModel: GymClassViewModel) {
  val context = LocalContext.current
  val calendar = remember { Calendar.getInstance() }
  val gymClass = viewModel.createGymClassRequest.observeAsState()
  val gymClassDto = gymClass.value

  val (date, time) = extractDateAndTime(gymClassDto?.dateTime)
  var timeText by remember { mutableStateOf(time ?: "") }

  OutlinedTextField(
    value = timeText,
    onValueChange = {},
    readOnly = true,
    label = { Text("Time") },
    modifier = Modifier,
    trailingIcon = {
      Button(
        onClick = {
          TimePickerDialog(
              context,
              { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                timeText = selectedTime
                viewModel.updateRequest {
                  copy(dateTime = "${date?:"0000-00-00"}T${selectedTime?:"00:00:00"}")
                }
              },
              calendar.get(Calendar.HOUR_OF_DAY),
              calendar.get(Calendar.MINUTE),
              true,
            )
            .show()
        }
      ) {
        Text("Pick Time")
      }
    },
  )
}

@Composable
fun ShowDatePickerCreate(viewModel: GymClassViewModel) {
  val context = LocalContext.current
  val calendar = remember { Calendar.getInstance() }
  val year = calendar.get(Calendar.YEAR)
  val month = calendar.get(Calendar.MONTH)
  val day = calendar.get(Calendar.DAY_OF_MONTH)
  val gymClass = viewModel.createGymClassRequest.observeAsState()
  val gymClassDto = gymClass.value

  val (date, time) = extractDateAndTime(gymClassDto?.dateTime)
  OutlinedTextField(
    value = date ?: "",
    label = { Text(text = "Date") },
    readOnly = true,
    onValueChange = { viewModel.updateRequest { copy(dateTime = "${date}T${time?:"00:00:00"}") } },
    trailingIcon = {
      Button(
        onClick = {
          DatePickerDialog(
              context,
              { _, year, monthOfYear, dayOfMonth ->
                val month = monthOfYear + 1
                val selectedDate = String.format("%d-%02d-%02d", year, month, dayOfMonth)

                Toast.makeText(context, "Selected date: $selectedDate", Toast.LENGTH_LONG).show()
                viewModel.updateRequest { copy(dateTime = "${selectedDate}T${time?:"00:00:00"}") }
              },
              year,
              month,
              day,
            )
            .show()
        }
      ) {
        Text(text = "Pick Date")
      }
    },
  )
}

@Preview
@Composable
fun CreateClassScreenPreview() {
  CreateClassScreen(
    navHostController = rememberNavController(),
    sharedViewModel = SharedViewModel(),
  )
}
