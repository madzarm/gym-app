package com.example.gym_app.screens.common

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.screens.trainer.CustomBackground
import com.example.gym_app.viewModels.HomeViewModel

@Composable
fun ProfileSetupScreen(navHostController: NavHostController, viewModel: HomeViewModel) {

    val updatableUser = viewModel.updatableUser.observeAsState()
    val context = LocalContext.current

  CustomBackground(title = "Setup profile") {
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
          value = updatableUser.value?.firstName ?: "",
          label = { Text(text = "First name") },
          onValueChange = { viewModel.updateUpdatableUser { copy(firstName = it) } },
        )

        OutlinedTextField(
          modifier = Modifier,
          value = updatableUser.value?.lastName ?: "",
          label = { Text(text = "Last name") },
          onValueChange = { viewModel.updateUpdatableUser { copy(lastName = it) } },
        )

          Button(
              onClick = {
                  viewModel.setupProfile(
                      context = context,
                      onSuccess = {
                          Toast.makeText(context, "Successful setup!", Toast.LENGTH_SHORT).show()
                          navHostController.popBackStack()
                          navHostController.navigate(AppRoutes.HOME)
                      },
                      onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() },
                  )
              }
          ) {
              Text(text = "Next")
          }
      }
    }
  }
}
