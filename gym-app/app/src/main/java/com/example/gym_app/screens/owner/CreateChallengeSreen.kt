package com.example.gym_app.screens.owner

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import com.example.gym_app.common.extractDateAndTime
import com.example.gym_app.screens.trainer.CustomBackground
import com.example.gym_app.viewModels.SharedViewModel
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar
import org.gymapp.library.response.ChallengeType

@Composable
fun CreateChallengeScreen(navHostController: NavHostController, viewModel: SharedViewModel) {

  val challenge = viewModel.updatableChallenge.observeAsState()
  val context = LocalContext.current

  CustomBackground(title = "Create a challenge!") {
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

        DatePicker(viewModel)

        ChallengeTypePicker(viewModel)

        if (challenge.value?.type == ChallengeType.FREQUENCY_BASED) {

          OutlinedTextField(
            modifier = Modifier,
            value = challenge.value?.frequencyCount ?: "",
            label = { Text(text = "Frequency count") },
            onValueChange = { viewModel.updateSelectedChallenge { copy(frequencyCount = it) } },
          )
        } else if (challenge.value?.type == ChallengeType.TIMED_VISIT_BASED) {

          TimePicker(
            label = "Start time",
            onTimeSelected = {
              viewModel.updateSelectedChallenge { copy(startTimeCriteria = LocalTime.parse(it)) }
            },
            timeSupplier = { challenge.value?.startTimeCriteria?.toString() ?: "" },
          )

          TimePicker(
            label = "End time",
            onTimeSelected = {
              viewModel.updateSelectedChallenge { copy(endTimeCriteria = LocalTime.parse(it)) }
            },
            timeSupplier = { challenge.value?.endTimeCriteria?.toString() ?: "" },
          )
        }

        Row (horizontalArrangement = Arrangement.spacedBy(16.dp)){
            Button(
                onClick = {
                    viewModel.createChallenge(
                        context = context,
                        onSuccess = {
                            Toast.makeText(context, "Successfully created a challenge!", Toast.LENGTH_LONG)
                                .show()
                            navHostController.popBackStack()
                        },
                        onError = {
                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            navHostController.popBackStack()
                        },
                    )
                }
            ) {
                Text(text = "Create")
            }
            Button(onClick = { navHostController.popBackStack() }) { Text(text = "Cancel") }
        }

      }
    }
  }
}

@Composable
fun ChallengeTypePicker(viewModel: SharedViewModel) {
  var selectedChallengeType by remember { mutableStateOf<ChallengeType?>(null) }
  val options = ChallengeType.entries.toTypedArray()

  var expanded by remember { mutableStateOf(false) }

  Column {
    OutlinedTextField(
      value = selectedChallengeType?.name ?: "",
      onValueChange = {
        selectedChallengeType = options.find { option -> option.name == it }
        viewModel.updateSelectedChallenge { copy(type = selectedChallengeType) }
      },
      label = { Text("Select Challenge Type") },
      readOnly = true,
      trailingIcon = {
        Icon(
          Icons.Default.ArrowDropDown,
          contentDescription = null,
          Modifier.clickable { expanded = !expanded },
        )
      },
      modifier = Modifier.fillMaxWidth(),
    )

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      modifier = Modifier.fillMaxWidth().padding(start = 56.dp, end = 56.dp),
    ) {
      options.forEach { option ->
        DropdownMenuItem(
          text = { Text(text = option.name) },
          onClick = {
            selectedChallengeType = option
            viewModel.updateSelectedChallenge { copy(type = selectedChallengeType) }
            expanded = false
          },
        )
      }
    }
  }
}

@Composable
fun DatePicker(viewModel: SharedViewModel) {
  val context = LocalContext.current
  val calendar = remember { Calendar.getInstance() }
  val year = calendar.get(Calendar.YEAR)
  val month = calendar.get(Calendar.MONTH)
  val day = calendar.get(Calendar.DAY_OF_MONTH)
  val challenge = viewModel.updatableChallenge.observeAsState()

  val (date, time) = extractDateAndTime(challenge.value?.expiryDate?.toString())
  OutlinedTextField(
    value = date ?: "",
    label = { Text(text = "Expiry date") },
    readOnly = true,
    onValueChange = {
      viewModel.updateSelectedChallenge {
        copy(expiryDate = LocalDateTime.parse("${date}T${time?:"00:00:00"}"))
      }
    },
    trailingIcon = {
      Button(
        onClick = {
          DatePickerDialog(
              context,
              { _, year, monthOfYear, dayOfMonth ->
                val month = monthOfYear + 1
                val selectedDate = String.format("%d-%02d-%02d", year, month, dayOfMonth)

                Toast.makeText(context, "Selected date: $selectedDate", Toast.LENGTH_LONG).show()
                viewModel.updateSelectedChallenge {
                  copy(expiryDate = LocalDateTime.parse("${selectedDate}T${time?:"00:00:00"}"))
                }
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
