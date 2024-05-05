package com.example.gym_app.screens.trainer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.R
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.common.extractDateAndTime
import com.example.gym_app.viewModels.GymClassViewModel
import java.util.Calendar

@Composable
fun TrainerGymClassScreen(navHostController: NavHostController, viewModel: GymClassViewModel) {
  val context = LocalContext.current
  val gymClass = viewModel.selectedGymClass.observeAsState()
  var showConfirmationDialog by remember { mutableStateOf(false) }

  val participantsCount =
    if (gymClass.value!!.isRecurring) {
      0
    } else {
      gymClass.value?.instances!!.get(0).participantsIds.size
    }
  val gymClassUpdatable = viewModel.updatedGymClass.observeAsState()
  CustomBackground(title = gymClass.value?.name ?: "Unknown class") {
    Scaffold(
      modifier =
        Modifier.padding(top = 10.dp).clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp))
    ) { innerPadding ->
      Column(
        modifier =
          Modifier.fillMaxHeight()
            .fillMaxWidth()
            .padding(top = innerPadding.calculateTopPadding(), start = 56.dp, end = 56.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        IconButton(onClick = { navHostController.navigate(AppRoutes.CALENDAR_SCREEN) }) {
          Icon(
            painter =
              painterResource(
                id = com.google.android.material.R.drawable.material_ic_calendar_black_24dp
              ),
            contentDescription = "View Calendar",
          )
        }
        val gymClassValueUpdatable = gymClassUpdatable.value
        OutlinedTextField(
          modifier = Modifier,
          value = gymClassValueUpdatable?.name ?: "Unknown",
          label = { Text(text = "Name") },
          onValueChange = { viewModel.updateGymClass { copy(name = it) } },
        )
        OutlinedTextField(
          modifier = Modifier,
          value = gymClassValueUpdatable?.description ?: "Unknown",
          label = { Text(text = "Description") },
          onValueChange = { viewModel.updateGymClass { copy(description = it) } },
        )
        OutlinedTextField(
          modifier = Modifier,
          value = gymClassValueUpdatable?.duration ?: "Unknown",
          label = { Text(text = "Duration in Minutes") },
          onValueChange = { viewModel.updateGymClass { copy(duration = it) } },
        )
        OutlinedTextField(
          modifier = Modifier,
          value = gymClassValueUpdatable?.maxParticipants ?: "Unknown",
          label = { Text(text = "Max participants") },
          onValueChange = { viewModel.updateGymClass { copy(maxParticipants = it) } },
        )
        ShowDatePicker(viewModel, isInstanceDatePicker = false)
        ShowTimePicker(viewModel, isInstanceTimePicker = false)
        if (!gymClass.value!!.isRecurring) {
          Text(text = "Participants registered: ${participantsCount}")
        }
        Row {
          Button(
            onClick = {
              viewModel.updateGymClass(context)

              navHostController.popBackStack()
            },
            modifier = Modifier.padding(top = 16.dp),
          ) {
            Text(text = "Update")
          }
          if (showConfirmationDialog) {
            AlertDialog(
              onDismissRequest = { showConfirmationDialog = false },
              title = { Text("Confirm Deletion") },
              text = {
                if (gymClass.value!!.isRecurring) {
                  Text("Are you sure you want to delete a recurring class?")
                } else {
                  Text(
                    "Are you sure you want to delete a class that has $participantsCount participants? Participants will be notified!"
                  )
                }
              },
              confirmButton = {
                Button(
                  onClick = {
                    viewModel.deleteGymClass(
                      context = context,
                      onSuccess = {
                        Toast.makeText(context, "Gym class deleted", Toast.LENGTH_LONG).show()
                        navHostController.popBackStack()
                      },
                      onFailure = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() },
                    )
                    showConfirmationDialog = false
                  }
                ) {
                  Text("Delete")
                }
              },
              dismissButton = {
                Button(onClick = { showConfirmationDialog = false }) { Text("Cancel") }
              },
            )
          }

          Button(
            onClick = {
              if (gymClass.value!!.isRecurring || participantsCount > 0) {
                showConfirmationDialog = true
              } else {
                viewModel.deleteGymClass(
                  context = context,
                  onSuccess = {
                    Toast.makeText(context, "Gym class deleted", Toast.LENGTH_LONG).show()
                    navHostController.popBackStack()
                  },
                  onFailure = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() },
                )
              }
            },
            modifier = Modifier.padding(top = 16.dp),
          ) {
            Text(text = "Delete")
          }
        }
      }
    }
  }
}

@Composable
fun ShowTimePicker(
  viewModel: GymClassViewModel,
  isInstanceTimePicker: Boolean,
  onTimeSelected: (String) -> Unit = { viewModel.updateGymClass { copy(dateTime = it) } },
) {
  val context = LocalContext.current
  val calendar = remember { Calendar.getInstance() }
  val gymClass = viewModel.updatedGymClass.observeAsState()
  val instance = viewModel.selectedInstance.observeAsState()
  val gymClassDto = gymClass.value

  val (date, time) =
    if (isInstanceTimePicker) {
      extractDateAndTime(instance.value?.dateTime)
    } else {
      extractDateAndTime(gymClassDto?.dateTime)
    }
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
                val dateTime = "${date}T$selectedTime"
                onTimeSelected(dateTime)
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
fun ShowDatePicker(
  viewModel: GymClassViewModel,
  isInstanceDatePicker: Boolean,
  onDateSelected: (String) -> Unit = { viewModel.updateGymClass { copy(dateTime = it) } },
) {
  val context = LocalContext.current
  val calendar = remember { Calendar.getInstance() }
  val year = calendar.get(Calendar.YEAR)
  val month = calendar.get(Calendar.MONTH)
  val day = calendar.get(Calendar.DAY_OF_MONTH)
  val gymClass = viewModel.updatedGymClass.observeAsState()
  val gymClassInstance = viewModel.selectedInstance.observeAsState()
  val gymClassDto = gymClass.value

  val (date, time) =
    if (isInstanceDatePicker) {
      extractDateAndTime(gymClassInstance.value?.dateTime)
    } else {
      extractDateAndTime(gymClassDto?.dateTime)
    }
  OutlinedTextField(
    value = date!!,
    label = { Text(text = "Date") },
    readOnly = true,
    onValueChange = { viewModel.updateGymClass { copy(dateTime = "${date}T${time}") } },
    trailingIcon = {
      Button(
        onClick = {
          DatePickerDialog(
              context,
              { _, year, monthOfYear, dayOfMonth ->
                val month = monthOfYear + 1
                val selectedDate = String.format("%d-%02d-%02d", year, month, dayOfMonth)

                Toast.makeText(context, "Selected date: $selectedDate", Toast.LENGTH_LONG).show()

                val dateTime = "${selectedDate}T${time}"
                onDateSelected(dateTime)
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
fun TrainerGymClassScreenPreview() {
  TrainerGymClassScreen(
    navHostController = rememberNavController(),
    viewModel = GymClassViewModel(),
  )
}
