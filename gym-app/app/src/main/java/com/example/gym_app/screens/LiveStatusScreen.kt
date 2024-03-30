package com.example.gym_app.screens

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.gym_app.viewModels.SharedViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun LiveStatusScreen(navController: NavController, viewModel: SharedViewModel) {

  Text(text = viewModel.selectedGymUser.value?.gym?.code ?: "Unknown")
}
