package com.example.gym_app.screens.trainer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gym_app.common.extractDateAndTime
import com.example.gym_app.viewModels.GymClassViewModel
import com.example.gym_app.viewModels.SharedViewModel
import java.util.Calendar

@Composable
fun CreateClassScreen(navHostController: NavHostController, sharedViewModel: SharedViewModel) {
  val viewModel: GymClassViewModel = viewModel()
  val request = viewModel.createGymClassRequest.observeAsState()
  val gymUser = sharedViewModel.selectedGymUser.observeAsState()

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
            .padding(top = innerPadding.calculateTopPadding(), start = 56.dp, end = 56.dp),
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
        if (errorMessage.isNotEmpty()) {
          Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 16.dp),
          )
        }
        Button(
          onClick = {
            viewModel.createGymClass(
              context,
              gymUser.value?.gym?.id ?: "",
              onSuccess = { navHostController.popBackStack() },
              onError = { msg -> errorMessage = msg },
            )
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
                viewModel.updateRequest { copy(dateTime = "${date?:"0000-00-00"}T${selectedTime?:"00:00:00"}") }
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
