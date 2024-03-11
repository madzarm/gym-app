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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.R
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.common.Role
import com.example.gym_app.ui.theme.GymappTheme
import com.example.gym_app.viewModels.UserViewModel

@Composable
fun SignupRoleSelectionScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = viewModel()
) {
  Column(
      modifier =
          modifier
              .fillMaxSize()
              .background(
                  brush =
                      Brush.horizontalGradient(
                          colors =
                              listOf(
                                  Color(0xFF00d4ff),
                                  Color(0xFF0051bf),
                              ))),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Text(
            text = "Are you a gym owner, trainer or a member?",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier =
                Modifier.align(Alignment.CenterHorizontally)
                    .padding(start = 20.dp, end = 20.dp, top = 80.dp),
            lineHeight = 30.sp)
        Column(
            modifier =
                modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp)
                    .clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp))
                    .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Spacer(modifier = Modifier.weight(1f))
              SelectionOption(
                  modifier = modifier,
                  painter = R.drawable.businessman,
                  text = "Gym Owner",
                  onClick = {
                    viewModel.updateUserState { copy(role = Role.OWNER) }
                    navController.navigate(AppRoutes.CREATE_ACCOUNT_SCREEN)
                  })
              SelectionOption(
                  modifier = modifier,
                  painter = R.drawable.trainer,
                  text = "Trainer",
                  onClick = {
                    viewModel.updateUserState { copy(role = Role.TRAINER) }
                    navController.navigate(AppRoutes.CREATE_ACCOUNT_SCREEN)
                  })
              SelectionOption(
                  modifier = modifier,
                  painter = R.drawable.member,
                  text = "Member",
                  onClick = {
                    viewModel.updateUserState { copy(role = Role.MEMBER) }
                    navController.navigate(AppRoutes.CREATE_ACCOUNT_SCREEN)
                  })
              Spacer(modifier = Modifier.weight(1f))
            }
      }
}

@Composable
private fun SelectionOption(modifier: Modifier, painter: Int, text: String, onClick: () -> Unit) {
  TextButton(onClick = onClick, modifier = modifier.background(Color.Transparent)) {
    Row(
        modifier = modifier.fillMaxWidth(0.84F).padding(bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Box(
          modifier =
              Modifier.size(100.dp)
                  .clip(CircleShape)
                  .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary), CircleShape),
      ) {
        Image(
            painter = painterResource(id = painter),
            contentDescription = "$text icon",
            modifier = Modifier.padding(end = 10.dp).size(120.dp))
      }
      Row(modifier = modifier.fillMaxWidth()) {
        Text(
            modifier = modifier.padding(start = 16.dp),
            text = text,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 35.sp,
        )
      }
    }
  }
}

@Composable
@Preview
fun SignupRoleSelectionScreenPreview() {
  GymappTheme { Surface { SignupRoleSelectionScreen(navController = rememberNavController()) } }
}
