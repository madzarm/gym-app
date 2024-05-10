package com.example.gym_app.screens.member

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gym_app.common.getCurrentHour
import com.example.gym_app.screens.owner.rememberMarker
import com.example.gym_app.viewModels.SharedViewModel
import com.example.gym_app.viewModels.StatisticsViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.zoom.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries
import com.patrykandpatrick.vico.core.zoom.Zoom

@Composable
fun LiveStatusScreen(viewModel: SharedViewModel) {

  val statisticsViewModel: StatisticsViewModel = viewModel()
  val context = LocalContext.current
  val gymId = viewModel.selectedGymUser.value?.gym?.id ?: ""
  val liveStatus = viewModel.liveStatus.value
  val usualTraffic =
    statisticsViewModel.graphData.observeAsState().value?.second?.get(getCurrentHour().toInt() - 7)
      ?: 0

  LaunchedEffect(true) { viewModel.getLiveStatus(context) }

  Column(modifier = Modifier.fillMaxSize(1f), horizontalAlignment = Alignment.CenterHorizontally) {
    Column() {
      Text(
        text = "Usual Traffic",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(10.dp),
      )
      GymVisitsPerHourChart(statisticsViewModel, gymId)
    }
    liveStatusComponent(liveStatus.toString() ?: "?", usualTraffic)
  }
}

@Composable
fun liveStatusComponent(liveStatus: String, usualTraffic: Int) {
  Box(
    modifier =
      Modifier.padding(top = 30.dp)
        .clip(RoundedCornerShape(20.dp))
        .fillMaxSize(0.5f)
        .background(
          brush = Brush.horizontalGradient(colors = listOf(Color(0xFF00d4ff), Color(0xFF0051bf)))
        )
  ) {
    Text(
      modifier = Modifier.align(Alignment.TopCenter).padding(10.dp),
      text = "Live Traffic",
      fontSize = 30.sp,
      color = Color.White,
    )
    Text(
      modifier = Modifier.align(Alignment.Center),
      text = liveStatus,
      fontSize = 120.sp,
      color = Color.White,
    )
    val percentageDifference =
      if (usualTraffic != 0) {
        100 * (liveStatus.toInt() - usualTraffic) / usualTraffic.toFloat()
      } else {
        0f
      }

    val comparisonText =
      when {
        percentageDifference > 0 ->
          "${String.format("%.1f", percentageDifference)}% more than usual"
        percentageDifference < 0 ->
          "${String.format("%.1f", -percentageDifference)}% less than usual"
        else -> "The same as usual"
      }

    Text(
      modifier = Modifier.align(Alignment.BottomStart).padding(10.dp),
      text = comparisonText,
      color = Color.White,
      fontSize = 15.sp,
    )
  }
}

@Composable
fun GymVisitsPerHourChart(statisticsViewModel: StatisticsViewModel, gymId: String) {

  val modelProducerPerHour = remember { CartesianChartModelProducer.build() }
  val context = LocalContext.current

  LaunchedEffect(true) {
    statisticsViewModel.getGymVisits(context, gymId)

    val (xAxisPerHour, yAxisPerHour) = statisticsViewModel.prepareGraphData()
    modelProducerPerHour.tryRunTransaction {
      lineSeries { series(y = yAxisPerHour, x = xAxisPerHour) }
    }
  }
  val marker = rememberMarker()
  val zoomState = rememberVicoZoomState(initialZoom = Zoom.Content)
  CartesianChartHost(
    zoomState = zoomState,
    chart =
      rememberCartesianChart(
        rememberLineCartesianLayer(
          listOf(rememberLineSpec(shader = DynamicShaders.color(MaterialTheme.colorScheme.primary)))
        ),
        startAxis = rememberStartAxis(guideline = null),
        bottomAxis = rememberBottomAxis(),
        persistentMarkers = mapOf(getCurrentHour() to marker),
      ),
    modelProducer = modelProducerPerHour,
    marker = marker,
  )
}
