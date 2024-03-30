package com.example.gym_app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gym_app.viewModels.SharedViewModel

@Composable
fun AccessCodeScreen(navController: NavController, viewModel: SharedViewModel) {

  val context = LocalContext.current
  val accessCode = viewModel.accessCode.observeAsState()

  Column(
    modifier = Modifier.padding(16.dp).fillMaxHeight().fillMaxWidth(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    if (accessCode.value != null) {
      Text(text = "Access Code: ${accessCode.value?.code}")
    }

    Button(onClick = {
        viewModel.generateAccessCode(context)
    }) { Text(text = "Generate Access Code") }
  }
}
