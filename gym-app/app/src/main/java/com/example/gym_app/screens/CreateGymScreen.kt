package com.example.gym_app.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.common.uriToBase64
import com.example.gym_app.viewModels.HomeViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun CreateGymScreen(
  navController: NavHostController,
  homeViewModel: HomeViewModel,
  onSubmit: () -> Unit,
) {
  var name by remember { mutableStateOf("") }
  var imageUri by remember { mutableStateOf<Uri?>(null) }
  var errorMessage by remember { mutableStateOf("") }
  val context = LocalContext.current

  val galleryLauncher =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri?
      ->
      imageUri = uri
    }

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
      text = "Create your gym!",
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
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Name") },
        isError = errorMessage.isNotEmpty(),
        supportingText = {
          if (errorMessage.isNotEmpty()) {
            Text(errorMessage)
          }
        },
      )
      Button(onClick = { galleryLauncher.launch("image/*") }) { Text("Pick Image") }
      Button(
        onClick = {
          val base64Image = imageUri?.let { uri -> uriToBase64(context, uri) } ?: ""
          homeViewModel.createGym(
            context = context,
            name = name,
            imageBase64 = base64Image,
            onSuccess = onSubmit,
            onError = { msg -> errorMessage = msg },
          )
        }
      ) {
        Text(text = "Create", fontSize = 6.em)
      }
      Spacer(modifier = Modifier.weight(1f))
    }
  }
}
