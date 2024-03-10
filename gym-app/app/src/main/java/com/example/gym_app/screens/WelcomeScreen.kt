package com.example.gym_app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.R
import com.example.gym_app.ui.theme.GymappTheme
import com.example.gym_app.ui.theme.logam

@Composable
fun WelcomeScreen(navController: NavController, modifier: Modifier = Modifier) {
  Column(
      modifier = modifier.fillMaxSize().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Image(
            painter = painterResource(id = R.drawable.fitness_tracker),
            contentDescription = "Welcome Logo",
            modifier = modifier.padding(bottom = 32.dp))
        Text(
            text = "Welcome to",
            style =
                TextStyle(
                    fontSize = 10.em,
                    color = MaterialTheme.colorScheme.primary),
        )

        Text(
            text = "FITRA",
            fontFamily = logam,
            style = TextStyle(fontWeight = FontWeight.Bold),
            fontSize = 16.em,
            modifier = modifier.padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.primary)
        Button(
            onClick = { navController.navigate("signUp") },
            modifier = modifier.fillMaxWidth().padding(bottom = 8.dp),
        ) {
          Text(text = "Login", fontSize = TextUnit(4F, TextUnitType.Em))
        }
        OutlinedButton(
            onClick = { navController.navigate("signIn") },
            modifier = modifier.fillMaxWidth().padding(bottom = 32.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)) {
              Text(text = "Sign Up", fontSize = 4.em)
            }
        Text(
            text = "Sign up using",
            color = Color.Gray,
            fontSize = 3.em,
            modifier = modifier.padding(bottom = 10.dp))
        Row(
            modifier = modifier.fillMaxWidth(0.5f),
            horizontalArrangement = Arrangement.SpaceBetween) {
              Image(
                  painter = painterResource(id = R.drawable.google_icon),
                  contentDescription = "Google logo")
              Image(
                  painter = painterResource(id = R.drawable.meta_icon),
                  contentDescription = "Google logo")
              Image(
                  painter = painterResource(id = R.drawable.twitter_icon),
                  contentDescription = "Google logo")
            }
      }
}

@Composable
@Preview
fun WelcomeScreenPreview() {
  GymappTheme { Surface { WelcomeScreen(navController = rememberNavController()) } }
}
