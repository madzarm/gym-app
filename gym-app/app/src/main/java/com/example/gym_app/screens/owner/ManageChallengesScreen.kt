package com.example.gym_app.screens.owner

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.screens.trainer.CustomBackground
import com.example.gym_app.viewModels.SharedViewModel
import kotlinx.datetime.toLocalDateTime
import org.gymapp.library.response.ChallengeDto
import org.gymapp.library.response.ChallengeType
import org.gymapp.library.response.CriteriaDto
import org.gymapp.library.response.CriteriaType
import java.time.LocalDateTime
import java.time.LocalTime

private val sampleChallenges =
  listOf(
    ChallengeDto(
      id = "1",
      name = "20 Times a Month Challenge!",
      description = "Come to the gym more then 20 times in a month to earn points!",
      pointsValue = 100,
      type = "FREQUENCY_BASED",
      expiryDate = "2022-12-12T00:00:00",
      createdAt = "2022-12-12T00:00:00",
      isDeleted = false,
      gymId = "1",
      criteriaDto =
        CriteriaDto(
          frequencyCount = 5,
          criteriaId = "1",
          type = CriteriaType.FREQUENCY_BASED.name,
          startTimeCriteria = null,
          endTimeCriteria = null,
        ),
    )
  )

@Composable
fun ManageChallengesScreen(navHostController: NavHostController, viewModel: SharedViewModel) {

  val context = LocalContext.current

  LaunchedEffect(true) {
    val gymId = viewModel.selectedGymUser.value?.gym?.id ?: ""
    viewModel.fetchActiveChallenges(context, gymId, onSuccess = {}) {
      Toast.makeText(context, it, Toast.LENGTH_LONG).show()
    }
  }

  val challenges = viewModel.challengeDtos.observeAsState()

  val listState = rememberLazyListState()
  val expandedFab by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

  CustomBackground(title = "Manage Challenges") {
    Scaffold(
      modifier =
        Modifier.padding(top = 10.dp).clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp)),
      floatingActionButton = {
        ExtendedFloatingActionButton(
          onClick = {
            navHostController.navigate(AppRoutes.CREATE_CHALLENGE_SCREEN)
          },
          expanded = expandedFab,
          icon = { Icon(Icons.Filled.Add, "Create a challenge button") },
          text = { Text(text = "Create a challenge") },
        )
      },
      floatingActionButtonPosition = FabPosition.End,
    ) { padding ->
      LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        items(challenges.value ?: emptyList()) { challenge ->
          ChallengeItem(challenge = challenge, onClick = {
            viewModel.updateSelectedChallenge { copy(
                id = challenge.id,
                name = challenge.name,
                description = challenge.description,
                pointsValue = challenge.pointsValue.toString(),
                type = ChallengeType.valueOf(challenge.type),
                expiryDate = LocalDateTime.parse(challenge.expiryDate),
                frequencyCount = challenge.criteriaDto?.frequencyCount.toString(),
                startTimeCriteria = challenge.criteriaDto?.startTimeCriteria?.let { LocalTime.parse(it) },
                endTimeCriteria = challenge.criteriaDto?.endTimeCriteria?.let { LocalTime.parse(it) }
            ) }
            navHostController.navigate(AppRoutes.CHALLENGE_DETAILS_SCREEN)
          })
        }
      }
    }
  }
}

@Composable
fun ChallengeItem(challenge: ChallengeDto, onClick: (ChallengeDto) -> Unit) {
  TextButton(
    onClick = {
        onClick(challenge)
    },
    modifier = Modifier.fillMaxWidth(0.84F).padding(top = 18.dp),
    shape = RoundedCornerShape(20.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier =
        Modifier.clip(RoundedCornerShape(20.dp))
          .background(
            brush = Brush.horizontalGradient(colors = listOf(Color(0xFF00d4ff), Color(0xFF0051bf)))
          )
          .fillMaxWidth()
          .fillMaxSize(),
    ) {
      Box(
        modifier =
          Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(20.dp),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.Start,
        ) {
          Text(
            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            text = challenge.name ?: "Unknown",
            lineHeight = 30.sp,
          )

          Text(
            text = challenge.description,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
          )

          Spacer(modifier = Modifier.padding(8.dp))

          Text(
            text = "Expires on: ${challenge.expiryDate.toLocalDateTime().date}",
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
          )
        }
        Text(
          text = "${challenge.pointsValue} points",
          color = Color.White,
          modifier =
            Modifier.align(Alignment.TopEnd)
              .padding(end = 8.dp, top = 8.dp),
        )
      }
    }
  }
}

@Composable
@Preview
fun ManageChallengesScreenPreview() {
  ManageChallengesScreen(
    navHostController = NavHostController(LocalContext.current),
    viewModel = SharedViewModel(),
  )
}
