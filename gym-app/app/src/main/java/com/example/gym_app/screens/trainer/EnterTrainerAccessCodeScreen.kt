package com.example.gym_app.screens.trainer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gym_app.viewModels.HomeViewModel

@Composable
fun EnterTrainerAccessCodeScreen(navController: NavHostController, onSubmit: () -> Unit, homeViewModel: HomeViewModel) {
    val context = LocalContext.current

    var errorMessage by remember { mutableStateOf("") }

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
            text = "Enter Access Code!",
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
            var code by remember { mutableStateOf("") }

            Spacer(modifier = Modifier.weight(1f))
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Code") },
                isError = errorMessage.isNotEmpty(),
                supportingText = {
                    if (errorMessage.isNotEmpty()) {
                        Text(errorMessage)
                    }
                },
            )
            Button(
                onClick = {
                    homeViewModel.joinGymAsTrainer(context, code, onSuccess = onSubmit, onError = { msg -> errorMessage = msg })
                }
            ) {
                Text(text = "Create", fontSize = 6.em)
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}