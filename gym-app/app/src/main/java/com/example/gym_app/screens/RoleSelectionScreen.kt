package com.example.gym_app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gym_app.R

@Composable
fun RoleSelectionScreen(
  onMemberSelection: () -> Unit,
  onTrainerSelection: () -> Unit,
  onOwnerSelection: () -> Unit,
) {
  Column(
    modifier =
      Modifier.fillMaxSize()
        .background(
          brush = Brush.horizontalGradient(colors = listOf(Color(0xFF00d4ff), Color(0xFF0051bf)))
        ),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = "Are you a gym owner, trainer or a member?",
      fontSize = 32.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White,
      textAlign = TextAlign.Center,
      modifier =
        Modifier.align(Alignment.CenterHorizontally)
          .padding(start = 20.dp, end = 20.dp, top = 80.dp),
      lineHeight = 30.sp,
    )
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .padding(top = 80.dp)
          .clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp))
          .background(MaterialTheme.colorScheme.background),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Spacer(modifier = Modifier.weight(1f))
      SelectionOption(
        painter = R.drawable.businessman,
        text = "Gym Owner",
        onClick = onOwnerSelection,
      )
      SelectionOption(
        painter = R.drawable.trainer,
        text = "Trainer",
        onClick = onTrainerSelection,
      )
      SelectionOption(
        painter = R.drawable.member,
        text = "Member",
        onClick = onMemberSelection,
      )
      Spacer(modifier = Modifier.weight(1f))
    }
  }
}

@Composable
private fun SelectionOption(painter: Int, text: String, onClick: () -> Unit) {
  TextButton(onClick = onClick, modifier = Modifier.background(Color.Transparent)) {
    Row(
      modifier = Modifier.fillMaxWidth(0.84F).padding(bottom = 18.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Box(
        modifier =
          Modifier.size(100.dp)
            .clip(CircleShape)
            .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary), CircleShape)
      ) {
        Image(
          painter = painterResource(id = painter),
          contentDescription = "$text icon",
          modifier = Modifier.padding(end = 10.dp).size(120.dp),
        )
      }
      Row(modifier = Modifier.fillMaxWidth()) {
        Text(
          modifier = Modifier.padding(start = 16.dp),
          text = text,
          color = MaterialTheme.colorScheme.primary,
          fontSize = 35.sp,
        )
      }
    }
  }
}
