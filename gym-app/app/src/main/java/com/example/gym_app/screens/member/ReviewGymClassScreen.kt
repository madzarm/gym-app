package com.example.gym_app.screens.member

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.screens.trainer.CustomBackground
import com.example.gym_app.viewModels.GymClassViewModel
import com.example.gym_app.viewModels.SharedViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ReviewGymClassScreen(
  viewModel: GymClassViewModel,
  sharedViewModel: SharedViewModel,
  navHostController: NavHostController,
) {

  val gymClass = viewModel.selectedGymClass.observeAsState()
  val gymClassReview = viewModel.gymClassReview.observeAsState()
  val trainerReview = viewModel.trainerReview.observeAsState()
  val user = sharedViewModel.selectedGymUser.value
  val context = LocalContext.current

  CustomBackground(title = "Reviewing class: ${gymClass.value?.name ?: "Unknown"}") {
    Scaffold(
      modifier =
        Modifier.padding(top = 10.dp).clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp))
    ) { padding ->
      Column(
        modifier =
          Modifier.padding(top = padding.calculateTopPadding() + 40.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Text(fontSize = 24.sp, fontWeight = FontWeight.Bold, text = "What was the class like?")

        StarRatingBar(
          maxStars = 5,
          rating = gymClassReview.value?.rating?.toFloat() ?: 1f,
          onRatingChanged = { viewModel.updateGymClassReview { copy(rating = it.toInt()) } },
        )

        TextField(
          modifier = Modifier.fillMaxWidth(0.7f),
          label = { Text("Tell us about it...") },
          value = gymClassReview.value?.review ?: "",
          onValueChange = { viewModel.updateGymClassReview { copy(review = it) } },
        )

        Spacer(modifier = Modifier.fillMaxWidth().height(40.dp))

        Text(fontSize = 24.sp, fontWeight = FontWeight.Bold, text = "What was the trainer like?")

        StarRatingBar(
          maxStars = 5,
          rating = trainerReview.value?.rating?.toFloat() ?: 1f,
          onRatingChanged = { viewModel.updateTrainerReview { copy(rating = it.toInt()) } },
        )

        TextField(
          modifier = Modifier.fillMaxWidth(0.7f),
          label = { Text("Tell us about it...") },
          value = trainerReview.value?.review ?: "",
          onValueChange = { viewModel.updateTrainerReview { copy(review = it) } },
        )

        Spacer(modifier = Modifier.fillMaxWidth().height(40.dp))

        Button(
          onClick = {
            viewModel.submitReview(
              context = context,
              memberId = user?.id ?: "",
              onSuccess = {
                Toast.makeText(context, "Review successfully submited!", Toast.LENGTH_LONG).show()
                navHostController.popBackStack()
              },
              onFailure = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() },
            )
          },
          modifier = Modifier.fillMaxWidth(0.6f),
        ) {
          Text(text = "Submit")
        }
      }
    }
  }
}

@Composable
fun StarRatingBar(maxStars: Int = 5, rating: Float, onRatingChanged: (Float) -> Unit) {
  val density = LocalDensity.current.density
  val starSize = (12f * density).dp
  val starSpacing = (0.5f * density).dp

  Row(
    modifier = Modifier.selectableGroup(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
  ) {
    for (i in 1..maxStars) {
      val isSelected = i <= rating
      val icon = if (isSelected) Icons.Filled.Star else Icons.Default.Star
      val iconTintColor = if (isSelected) Color(0xFFFFC700) else Color(0xFFd9dbdb)
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = iconTintColor,
        modifier =
          Modifier.selectable(selected = isSelected, onClick = { onRatingChanged(i.toFloat()) })
            .width(starSize)
            .height(starSize),
      )

      if (i < maxStars) {
        Spacer(modifier = Modifier.width(starSpacing))
      }
    }
  }
}

@Preview
@Composable
fun ReviewGymClassScreenPreview() {
  ReviewGymClassScreen(viewModel = viewModel(), sharedViewModel = viewModel(), navHostController = rememberNavController())
}
