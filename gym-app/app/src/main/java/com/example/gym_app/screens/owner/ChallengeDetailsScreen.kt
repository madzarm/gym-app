package com.example.gym_app.screens.owner

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.navigation.NavHostController
import com.example.gym_app.screens.trainer.CustomBackground
import com.example.gym_app.viewModels.SharedViewModel
import java.time.LocalTime
import java.util.Calendar
import org.gymapp.library.response.ChallengeType

@Composable
fun ChallengeDetailsScreen(navHostController: NavHostController, viewModel: SharedViewModel) {

  val challenge = viewModel.updatableChallenge.observeAsState()
  val context = LocalContext.current

  CustomBackground(title = "Challenge details") {
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
          value = challenge.value?.name ?: "",
          label = { Text(text = "Name") },
          onValueChange = { viewModel.updateSelectedChallenge { copy(name = it) } },
        )
        OutlinedTextField(
          modifier = Modifier,
          value = challenge.value?.description ?: "",
          label = { Text(text = "Description") },
          onValueChange = { viewModel.updateSelectedChallenge { copy(description = it) } },
        )
        OutlinedTextField(
          modifier = Modifier,
          value = challenge.value?.pointsValue ?: "",
          label = { Text(text = "Points value") },
          onValueChange = { viewModel.updateSelectedChallenge { copy(pointsValue = it) } },
        )

        if (challenge.value?.type == ChallengeType.FREQUENCY_BASED) {

          OutlinedTextField(
            modifier = Modifier,
            value = challenge.value?.frequencyCount ?: "",
            label = { Text(text = "Frequency count") },
            onValueChange = { viewModel.updateSelectedChallenge { copy(frequencyCount = it) } },
          )
        } else {

          TimePicker(
            label = "Start time",
            onTimeSelected = {
              viewModel.updateSelectedChallenge { copy(startTimeCriteria = LocalTime.parse(it)) }
            },
            timeSupplier = { challenge.value?.startTimeCriteria.toString() },
          )

          TimePicker(
            label = "End time",
            onTimeSelected = {
              viewModel.updateSelectedChallenge { copy(endTimeCriteria = LocalTime.parse(it)) }
            },
            timeSupplier = { challenge.value?.endTimeCriteria.toString() },
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp),) {
          Button(
            onClick = {
              viewModel.updateChallenge(
                context = context,
                onSuccess = {
                  Toast.makeText(context, "Challenge successfully updated!", Toast.LENGTH_LONG)
                    .show()
                  navHostController.popBackStack()
                },
                onError = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() },
              )
            }
          ) {
            Text("Update")
          }

          Button(
            onClick = {
              viewModel.deleteChallenge(
                context = context,
                onSuccess = {
                  Toast.makeText(context, "Challenge successfully deleted!", Toast.LENGTH_LONG)
                    .show()
                  navHostController.popBackStack()
                },
                onError = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() },
              )
            }
          ) {
            Text("Delete")
          }
        }
      }
    }
  }
}

@Composable
fun TimePicker(label: String, onTimeSelected: (String) -> Unit, timeSupplier: () -> String) {
  val context = LocalContext.current
  val calendar = remember { Calendar.getInstance() }

  var timeText by remember { mutableStateOf(timeSupplier()) }

  OutlinedTextField(
    value = timeText,
    onValueChange = {
      timeText = it
      onTimeSelected(it)
    },
    readOnly = true,
    label = { Text(label) },
    modifier = Modifier,
    trailingIcon = {
      Button(
        onClick = {
          TimePickerDialog(
              context,
              { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                timeText = selectedTime
                onTimeSelected(selectedTime)
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
