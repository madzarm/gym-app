package com.example.gym_app.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import com.example.gym_app.viewModels.GymClassViewModel

@Composable
fun TrainerGymClassScreen(navHostController: NavHostController, viewModel: GymClassViewModel) {
    val gymClass = viewModel.selectedGymClass.observeAsState()
  CustomBackground(title = gymClass.value?.name ?: "Unknown class") {

 }
}
