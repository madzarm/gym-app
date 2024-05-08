package com.example.gym_app.screens.common

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gym_app.screens.trainer.CustomBackground
import com.example.gym_app.viewModels.HomeViewModel

@Composable
fun ProfileScreen(
  navHostController: NavHostController,
  viewModel: HomeViewModel,
  onLogout: () -> Unit,
) {

  val context = LocalContext.current
  val user = viewModel.currentUser.collectAsState()

  CustomBackground(title = "Upcoming classes") {
    Scaffold(
      modifier =
        Modifier.padding(top = 10.dp).clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp))
    ) { padding ->
      Column(
        modifier =
          Modifier.fillMaxHeight()
            .fillMaxWidth()
            .padding(top = padding.calculateTopPadding(), start = 56.dp, end = 56.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        OutlinedTextField(
          modifier = Modifier,
          value = user.value?.firstName ?: "",
          label = { Text(text = "First name") },
          onValueChange = { viewModel.updateUserDto { copy(firstName = it) } },
        )

        OutlinedTextField(
          modifier = Modifier,
          value = user.value?.lastName ?: "",
          label = { Text(text = "First name") },
          onValueChange = { viewModel.updateUserDto { copy(firstName = it) } },
        )

        Row {
          Button(onClick = {
            viewModel.updateUser(
              context = context,
              onSuccess = {
                Toast.makeText(context, "Successfully updated!", Toast.LENGTH_SHORT).show()
                navHostController.popBackStack() },
              onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show()}
            )
          }) {
            Text(text = "Update")
          }
          Button(onClick = { onLogout() }) { Text(text = "Logout") }
        }
      }
    }
  }
}
