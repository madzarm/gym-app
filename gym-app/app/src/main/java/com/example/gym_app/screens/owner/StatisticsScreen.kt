package com.example.gym_app.screens.owner

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gym_app.viewModels.SharedViewModel
import com.example.gym_app.viewModels.StatisticsViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries

@Composable
fun StatisticsScreen(sharedViewModel: SharedViewModel) {
  val modelProducer = remember { CartesianChartModelProducer.build() }
  val statisticsViewModel: StatisticsViewModel = viewModel()

  val context = LocalContext.current
  val gymId = sharedViewModel.selectedGymUser.value?.gym?.id ?: ""

  LaunchedEffect(Unit) {
    statisticsViewModel.getGymVisits(context, gymId)
    val (xAxis, yAxis) = statisticsViewModel.prepareGraphData()
    modelProducer.tryRunTransaction { lineSeries { series(y = yAxis, x = xAxis) } }
  }

  CartesianChartHost(
    rememberCartesianChart(
      rememberLineCartesianLayer(
        listOf(rememberLineSpec(shader = DynamicShaders.color(MaterialTheme.colorScheme.primary)))
      ),
      startAxis = rememberStartAxis(),
      bottomAxis = rememberBottomAxis(),
    ),
    modelProducer,
  )
}
