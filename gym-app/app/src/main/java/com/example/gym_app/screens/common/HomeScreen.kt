package com.example.gym_app.screens.common

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.common.base64StringToImageBitmap
import com.example.gym_app.viewModels.HomeViewModel
import com.example.gym_app.viewModels.SharedViewModel
import org.gymapp.library.response.GymUserDto

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
  navController: NavController,
  modifier: Modifier = Modifier,
  onAddGymClicked: () -> Unit,
  homeViewModel: HomeViewModel
) {

  val sharedViewModel: SharedViewModel = viewModel()

  val gymUserDtos by homeViewModel.gymUserDtos.collectAsState()
  val currentUser by homeViewModel.currentUser.collectAsState()

  Column(
    modifier =
      modifier
        .fillMaxSize()
        .background(
          brush = Brush.horizontalGradient(colors = listOf(Color(0xFF00d4ff), Color(0xFF0051bf)))
        ),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Box(
      modifier =
        Modifier.fillMaxWidth().fillMaxWidth().padding(end = 10.dp, start = 10.dp, top = 10.dp)
    ) {
      Text(
        text = "Your gyms",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.align(Alignment.Center),
        textAlign = TextAlign.Center,
        lineHeight = 30.sp,
      )
      val painter =
        rememberAsyncImagePainter(
          ImageRequest.Builder(LocalContext.current)
            .data(data = currentUser?.profilePicUrl ?: "")
            .apply(
              block =
                fun ImageRequest.Builder.() {
                  crossfade(true)
                }
            )
            .build()
        )

      Image(
        painter = painter,
        modifier = Modifier.align(Alignment.CenterEnd).clip(CircleShape).size(50.dp),
        contentDescription = "Profile image",
      )
    }
    val listState = rememberLazyListState()
    val expandedFab by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    Scaffold(
      modifier =
        modifier.padding(top = 10.dp).clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp)),
      floatingActionButton = {
        ExtendedFloatingActionButton(
          onClick = onAddGymClicked,
          expanded = expandedFab,
          icon = { Icon(Icons.Filled.Add, "Add a gym button") },
          text = { Text(text = "Add a gym") },
        )
      },
      floatingActionButtonPosition = FabPosition.End,
    ) {
      LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        items(gymUserDtos) { gymUserDto ->
          GymItem(gymUserDto) {
            sharedViewModel.selectGym(gymUserDto)
            navController.navigate(AppRoutes.GYM_HOME_SCREEN)
          }
        }
      }
    }
  }
}

@Composable
fun GymItem(gymUserDto: GymUserDto, onClick: () -> Unit) {
  TextButton(onClick = onClick) {
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
        val imageBitmap = base64StringToImageBitmap(gymUserDto.gym?.picture ?: default)
        Image(
          bitmap = imageBitmap,
          contentDescription = "Gym image",
          modifier = Modifier.size(120.dp),
        )
      }
      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
          modifier = Modifier.padding(start = 16.dp),
          text = gymUserDto.gym?.name ?: "Unknown",
          color = MaterialTheme.colorScheme.primary,
          fontSize = 35.sp,
        )
        Column(
          modifier = Modifier.fillMaxSize().fillMaxHeight(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          gymUserDto.roles?.forEach { role -> Text(text = role.split("_")[1]) }
        }
      }
    }
  }
}

val default = "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"