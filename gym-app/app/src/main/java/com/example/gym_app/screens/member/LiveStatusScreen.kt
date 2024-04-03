package com.example.gym_app.screens.member

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.gym_app.viewModels.SharedViewModel

@Composable
fun LiveStatusScreen(viewModel: SharedViewModel) {

  val context = LocalContext.current
  LaunchedEffect(true) {
    viewModel.getLiveStatus(context)
  }

  val liveStatus = viewModel.liveStatus.value

  Text(text = liveStatus?.toString() ?: "Unknown")
}
