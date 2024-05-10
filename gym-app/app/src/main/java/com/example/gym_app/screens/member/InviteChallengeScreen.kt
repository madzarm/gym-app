package com.example.gym_app.screens.member

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gym_app.screens.trainer.CustomBackground
import com.example.gym_app.viewModels.SharedViewModel

@Composable
fun InviteChallengeScreen(navHostController: NavHostController, viewModel: SharedViewModel) {

  val challenge = viewModel.selectedChallenge.observeAsState()
  val member = viewModel.selectedGymUser.value?.gymMember
  val context = LocalContext.current

  CustomBackground(title = "Challenge details") {
    Scaffold(
      modifier =
        Modifier.padding(top = 10.dp).clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp))
    ) { innerPadding ->
      Column(
        modifier =
          Modifier.fillMaxHeight()
            .fillMaxWidth()
            .padding(top = innerPadding.calculateTopPadding() + 8.dp, start = 56.dp, end = 56.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = challenge.value?.name ?: "",
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.titleLarge,
          fontSize = 32.sp,
        )
        Text(
          text = challenge.value?.description ?: "",
          textAlign = TextAlign.Start,
          style = MaterialTheme.typography.bodyMedium,
          fontSize = 16.sp,
        )
        Text(
          modifier = Modifier.fillMaxWidth(),
          text = "Points: ${challenge.value?.pointsValue ?: "0"}",
          textAlign = TextAlign.Start,
          fontSize = 16.sp,
          style = MaterialTheme.typography.bodyMedium,
        )
        Text(
          modifier = Modifier.fillMaxWidth(),
          text = "Your invite code: ${member?.inviteCode ?: "0"}",
          textAlign = TextAlign.Start,
          fontSize = 16.sp,
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }
  }
}
