package com.example.gym_app.screens.member

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gym_app.screens.owner.ChallengeItem
import com.example.gym_app.screens.trainer.CustomBackground
import com.example.gym_app.viewModels.SharedViewModel
import kotlinx.coroutines.delay
import org.gymapp.library.response.ChallengeDto

sealed class ChallengeListItem {
  data class ActiveChallengeItem(val challenge: ChallengeDto) : ChallengeListItem()

  data class UnclaimedChallengeItem(val challenge: ChallengeDto) : ChallengeListItem()
}

@Composable
fun MemberChallengesScreen(navHostController: NavHostController, viewModel: SharedViewModel) {

  val context = LocalContext.current
  val activeChallenges = viewModel.challengeDtos.observeAsState()
  val unclaimedChallenges = viewModel.unclaimedChallenges.observeAsState()
  val totalPoints = viewModel.points.observeAsState()

  LaunchedEffect(true) {
    viewModel.fetchActiveChallenges(
      context = context,
      gymId = viewModel.selectedGymUser.value?.gym?.id ?: "",
      onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() },
    )
    viewModel.fetchUnclaimedChallenges(
      context = context,
      gymId = viewModel.selectedGymUser.value?.gym?.id ?: "",
      onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() },
    )
    viewModel.fetchPoints(
      context = context,
      gymId = viewModel.selectedGymUser.value?.gym?.id ?: "",
      onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() },
    )
  }

  CustomBackground(title = "Points: ${totalPoints.value ?: "0"}") {
    val listState = rememberLazyListState()

    Scaffold(
      modifier =
        Modifier.padding(top = 10.dp).clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp))
    ) { padding ->
      LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        val combinedList =
          combineLists(
            activeChallenges.value ?: emptyList(),
            unclaimedChallenges.value ?: emptyList(),
          )

        items(combinedList) { item ->
          when (item) {
            is ChallengeListItem.UnclaimedChallengeItem -> {
              UnclaimedChallengeItem(challenge = item.challenge, onClick = {
                viewModel.claimChallenge(context, item.challenge)
              })
            }
            is ChallengeListItem.ActiveChallengeItem -> {
              ChallengeItem(challenge = item.challenge, onClick = {})
            }
          }
        }
      }
    }
  }
}

@Composable
fun UnclaimedChallengeItem(challenge: ChallengeDto, onClick: (ChallengeDto) -> Unit) {
  var clicked by remember { mutableStateOf(false) }
  var startDecaying by remember { mutableStateOf(false) }
  var itemVisible by remember { mutableStateOf(true) }
  var popupVisible by remember { mutableStateOf(false) }

  val backgroundColors = listOf(Color(0xFF00d4ff), Color(0xFF0051bf))
  val animatedColor by
    animateColorAsState(
      targetValue = if (clicked) Color(0xFFFFD700) else backgroundColors.first(),
      animationSpec = tween(durationMillis = 500),
      label = "",
    )
  val scale =
    animateFloatAsState(
      targetValue = if (!startDecaying) 1f else 0.0f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
      label = "",
    )

  LaunchedEffect(clicked) {
    if (clicked) {
      delay(500)
      startDecaying = true
      itemVisible = false
      popupVisible = true
      delay(3000)
      popupVisible = false
    }
  }
    TextButton(
      onClick = {
        clicked = true
        onClick(challenge)
      },
      modifier = Modifier.fillMaxWidth(0.84F).padding(top = 18.dp).scale(scale.value),
      shape = RoundedCornerShape(20.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier =
          Modifier.clip(RoundedCornerShape(20.dp))
            .background(
              brush =
                Brush.horizontalGradient(
                  colors = if (clicked) listOf(animatedColor, animatedColor) else backgroundColors
                )
            )
            .fillMaxWidth()
            .fillMaxSize(),
      ) {
        Box(modifier = Modifier.fillMaxWidth()) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(
              modifier = Modifier.padding(bottom = 8.dp, top = 8.dp),
              style = MaterialTheme.typography.titleLarge,
              color = Color.White,
              text = challenge.name,
              lineHeight = 30.sp,
            )

            Text(
              text = "Click to Claim!",
              color = Color.White,
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.titleLarge,
              fontSize = 40.sp,
            )
          }
          Text(
            text = "${challenge.pointsValue} points",
            color = Color.White,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 8.dp, top = 8.dp),
          )
        }
      }
    }

  AnimatedVisibility(
    visible = popupVisible,
    enter =
      fadeIn(animationSpec = tween(durationMillis = 300)) + expandIn(expandFrom = Alignment.Center),
    exit = fadeOut(animationSpec = tween(durationMillis = 300)),
  ) {
    Text(
      "+${challenge.pointsValue}",
      modifier = Modifier.padding(16.dp),
      fontSize = 56.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary,
    )
  }
}

private fun combineLists(
  activeChallenges: List<ChallengeDto>,
  unclaimedChallenges: List<ChallengeDto>,
): List<ChallengeListItem> {
  val combinedList = mutableListOf<ChallengeListItem>()
  combinedList.addAll(unclaimedChallenges.map { ChallengeListItem.UnclaimedChallengeItem(it) })
  combinedList.addAll(activeChallenges.map { ChallengeListItem.ActiveChallengeItem(it) })
  return combinedList
}
