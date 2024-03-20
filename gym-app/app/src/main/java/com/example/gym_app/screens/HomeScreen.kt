package com.example.gym_app.screens

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.common.base64StringToImageBitmap
import com.example.gym_app.ui.theme.GymappTheme
import com.example.gym_app.viewModels.HomeViewModel
import com.example.gym_app.viewModels.HomeViewModelFactory
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
        val imageBitmap = base64StringToImageBitmap(gymUserDto.gym?.picture ?: "")
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