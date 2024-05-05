package com.example.gym_app.screens.trainer

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
import com.example.gym_app.viewModels.GymClassViewModel

@Composable
fun TrainerGymClassInstanceScreen(
  navHostController: NavHostController,
  viewModel: GymClassViewModel,
) {
  val context = LocalContext.current
  val instance = viewModel.selectedInstance.observeAsState()
  var showConfirmationDialog by remember { mutableStateOf(false) }


  val participantsCount = instance.value?.participantsIds?.size ?: 0
  CustomBackground(title = instance.value?.name ?: "Unknown class") {
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
        val gymClassValueUpdatable = instance.value
        OutlinedTextField(
          modifier = Modifier,
          value = gymClassValueUpdatable?.name ?: "Unknown",
          label = { Text(text = "Name") },
          onValueChange = { viewModel.updateInstance { copy(name = it) } },
        )
        OutlinedTextField(
          modifier = Modifier,
          value = gymClassValueUpdatable?.description ?: "Unknown",
          label = { Text(text = "Description") },
          onValueChange = { viewModel.updateInstance { copy(description = it) } },
        )
        OutlinedTextField(
          modifier = Modifier,
          value = gymClassValueUpdatable?.duration ?: "Unknown",
          label = { Text(text = "Duration in Minutes") },
          onValueChange = { viewModel.updateInstance { copy(duration = it) } },
        )
        OutlinedTextField(
          modifier = Modifier,
          value = gymClassValueUpdatable?.maxParticipants ?: "Unknown",
          label = { Text(text = "Max participants") },
          onValueChange = { viewModel.updateInstance { copy(maxParticipants = it) } },
        )
        ShowDatePicker(viewModel, true) { viewModel.updateInstance { copy(dateTime = it) } }
        ShowTimePicker(viewModel, true) { viewModel.updateInstance { copy(dateTime = it) } }
        Text(text = "Participants registered: $participantsCount")

        Row {
          Button(
            onClick = {
              viewModel.updateGymClassInstance(
                context = context,
                onSuccess = {
                  Toast.makeText(context, "Gym class updated", Toast.LENGTH_LONG).show()
                  navHostController.popBackStack()
                },
                onFailure = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() },
              )
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
                Text(
                  "Are you sure you want to delete a class that has $participantsCount participants? Participants will be notified!"
                )
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
              if (participantsCount > 0) {
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
