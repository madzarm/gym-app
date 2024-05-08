package com.example.gym_app.screens.trainer

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.gym_app.viewModels.GymClassViewModel
import org.gymapp.library.response.GymMemberDtoFull

@Composable
fun TrainerGymClassInstanceScreen(
  navHostController: NavHostController,
  viewModel: GymClassViewModel,
) {
  val context = LocalContext.current
  val instance = viewModel.selectedInstance.observeAsState()
  var showConfirmationDialog by remember { mutableStateOf(false) }
  var participants = viewModel.classParticipants.observeAsState()

  LaunchedEffect(true) { viewModel.fetchClassParticipants(context) }

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
            .padding(top = innerPadding.calculateTopPadding(), start = 56.dp, end = 56.dp)
            .verticalScroll(rememberScrollState()),
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

        HorizontalDivider(
          modifier = Modifier.padding(vertical = 8.dp),
          thickness = 1.dp,
          color = Color.Gray,
        )
        Text(
          text = "Participants: ${participantsCount}/${instance.value?.maxParticipants ?: 0}",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.align(Alignment.Start),
        )

        ParticipantsList(
          modifier = Modifier.align(Alignment.Start).padding(top = 4.dp),
          participants = participants.value ?: emptyList(),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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
                    viewModel.cancelGymClass(
                      context = context,
                      onSuccess = {
                        Toast.makeText(context, "Gym class canceled", Toast.LENGTH_LONG).show()
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
                viewModel.cancelGymClass(
                  context = context,
                  onSuccess = {
                    Toast.makeText(context, "Gym class canceled", Toast.LENGTH_LONG).show()
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
fun ParticipantsList(modifier: Modifier, participants: List<GymMemberDtoFull>) {
  val listState = rememberLazyListState()
  LazyColumn(modifier = modifier.heightIn(max = 300.dp), state = listState) {
    items(participants) { participant ->
      val firstName = participant.user?.firstName
      val lastName = participant.user?.lastName
      val pictureUrl = participant.user?.profilePicUrl
      ParticipantListItem(firstName ?: "", lastName ?: "", pictureUrl ?: "")
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}

@Composable
fun ParticipantListItem(firstName: String, lastName: String, picUrl: String) {
  Row {
    ImageComponent(picUrl)
    Column(
      modifier = Modifier.padding(start = 8.dp).fillMaxSize(),
      verticalArrangement = Arrangement.Center,
    ) {
      Text(text = firstName, style = MaterialTheme.typography.titleSmall)
      Text(text = lastName, style = MaterialTheme.typography.bodyMedium)
    }
  }
}

@Composable
fun ImageComponent(picUrl: String) {
  val painter =
    rememberAsyncImagePainter(
      ImageRequest.Builder(LocalContext.current)
        .data(data = picUrl)
        .apply(
          block =
            fun ImageRequest.Builder.() {
              crossfade(true)
            }
        )
        .build()
    )

  Image(
    painter = painter,
    modifier = Modifier.clip(CircleShape).size(50.dp),
    contentDescription = "Profile image",
  )
}
