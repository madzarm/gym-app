package com.example.gym_app.screens

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.viewModels.SharedViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun LiveStatusScreen(navController: NavController, viewModel: SharedViewModel) {

  Text(text = viewModel.selectedGym.value?.gym?.code ?: "Unknown")
}
