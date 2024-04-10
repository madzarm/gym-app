package com.example.gym_app.screens.owner

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gym_app.viewModels.SharedViewModel
import com.example.gym_app.viewModels.StatisticsViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.component.fixed
import com.patrykandpatrick.vico.compose.component.rememberLayeredComponent
import com.patrykandpatrick.vico.compose.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.component.shape.dashedShape
import com.patrykandpatrick.vico.compose.component.shape.markerCorneredShape
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.dimensions.HorizontalDimensions
import com.patrykandpatrick.vico.core.chart.insets.Insets
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.cornered.Corner
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.context.MeasureContext
import com.patrykandpatrick.vico.core.extension.copyColor
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.ExtraStore
import com.patrykandpatrick.vico.core.model.lineSeries

@Composable
fun StatisticsScreen(sharedViewModel: SharedViewModel) {
  val modelProducerPerHour = remember { CartesianChartModelProducer.build() }
  val modelProducerPerDay = remember { CartesianChartModelProducer.build() }

  val statisticsViewModel: StatisticsViewModel = viewModel()

  val context = LocalContext.current
  val gymId = sharedViewModel.selectedGymUser.value?.gym?.id ?: ""

  LaunchedEffect(Unit) {
    statisticsViewModel.getGymVisits(context, gymId)

    val (xAxisPerHour, yAxisPerHour) = statisticsViewModel.prepareGraphData()
    val (xAxisPerDay, yAxisPerDay) = statisticsViewModel.prepareGraphDataPerDay()

    modelProducerPerHour.tryRunTransaction {
      lineSeries {
        series(y = yAxisPerHour, x = xAxisPerHour)
      } }
    modelProducerPerDay.tryRunTransaction {
      lineSeries {
        series(y = yAxisPerDay, x = xAxisPerDay)
      }
    }

  }

  Column {
    Column {
      Text(
        text = "Gym Visit Trends by Hour",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(16.dp),
      )
      val marker = rememberMarker()
      CartesianChartHost(
        rememberCartesianChart(
          rememberLineCartesianLayer(
            listOf(
              rememberLineSpec(shader = DynamicShaders.color(MaterialTheme.colorScheme.primary)),
            )
          ),
          startAxis = rememberStartAxis(guideline = null),
          bottomAxis = rememberBottomAxis(),
          persistentMarkers = mapOf(getCurrentHour() to marker),
        ),
        modelProducerPerHour,
        marker = marker,
      )
    }

    Column {
      Text(
        text = "Gym Visit Trends by Day",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(16.dp),
      )
      val marker = rememberMarker()
      CartesianChartHost(
        rememberCartesianChart(
          rememberLineCartesianLayer(
            listOf(
              rememberLineSpec(shader = DynamicShaders.color(MaterialTheme.colorScheme.primary)),
            )
          ),
          startAxis = rememberStartAxis(guideline = null),
          bottomAxis = rememberBottomAxis(
            valueFormatter = { value, _, _ ->
              val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
              daysOfWeek.getOrNull(value.toInt() - 1) ?: ""
            },
            ),
          persistentMarkers = mapOf(getCurrentDay() to marker),
        ),
        modelProducerPerDay,
        marker = marker,
      )
    }
  }
}

fun getCurrentHour(): Float {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    java.time.LocalDateTime.now().hour.toFloat()
  } else {
    TODO("VERSION.SDK_INT < O")
  }
}

fun getCurrentDay(): Float {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    java.time.LocalDate.now().dayOfWeek.value.toFloat()
  } else {
    TODO("VERSION.SDK_INT < O")
  }
}

@Composable
internal fun rememberMarker(
  labelPosition: MarkerComponent.LabelPosition = MarkerComponent.LabelPosition.Top
): Marker {
  val labelBackgroundShape = Shapes.markerCorneredShape(Corner.FullyRounded)
  val labelBackground =
    rememberShapeComponent(labelBackgroundShape, MaterialTheme.colorScheme.surface)
      .setShadow(
        radius = LABEL_BACKGROUND_SHADOW_RADIUS_DP,
        dy = LABEL_BACKGROUND_SHADOW_DY_DP,
        applyElevationOverlay = true,
      )
  val label =
    rememberTextComponent(
      color = MaterialTheme.colorScheme.onSurface,
      background = labelBackground,
      padding = dimensionsOf(8.dp, 4.dp),
      typeface = Typeface.MONOSPACE,
      textAlignment = Layout.Alignment.ALIGN_CENTER,
      minWidth = TextComponent.MinWidth.fixed(40.dp),
    )
  val indicatorFrontComponent =
    rememberShapeComponent(Shapes.pillShape, MaterialTheme.colorScheme.surface)
  val indicatorCenterComponent = rememberShapeComponent(Shapes.pillShape)
  val indicatorRearComponent = rememberShapeComponent(Shapes.pillShape)
  val indicator =
    rememberLayeredComponent(
      rear = indicatorRearComponent,
      front =
        rememberLayeredComponent(
          rear = indicatorCenterComponent,
          front = indicatorFrontComponent,
          padding = dimensionsOf(5.dp),
        ),
      padding = dimensionsOf(10.dp),
    )
  val guideline =
    rememberLineComponent(
      color = MaterialTheme.colorScheme.onSurface.copy(.2f),
      thickness = 2.dp,
      shape = Shapes.dashedShape(shape = Shapes.pillShape, dashLength = 8.dp, gapLength = 4.dp),
    )
  return remember(label, labelPosition, indicator, guideline) {
    @SuppressLint("RestrictedApi")
    object : MarkerComponent(label, labelPosition, indicator, guideline) {
      init {
        indicatorSizeDp = 36f
        onApplyEntryColor = { entryColor ->
          indicatorRearComponent.color = entryColor.copyColor(alpha = .15f)
          with(indicatorCenterComponent) {
            color = entryColor
            setShadow(radius = 12f, color = entryColor)
          }
        }
      }

      override fun getInsets(
        context: MeasureContext,
        outInsets: Insets,
        horizontalDimensions: HorizontalDimensions,
      ) {
        with(context) {
          outInsets.top =
            (CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER * LABEL_BACKGROUND_SHADOW_RADIUS_DP -
                LABEL_BACKGROUND_SHADOW_DY_DP)
              .pixels
          if (labelPosition == LabelPosition.AroundPoint) return
          outInsets.top += label.getHeight(context) + labelBackgroundShape.tickSizeDp.pixels
        }
      }
    }
  }
}

private const val LABEL_BACKGROUND_SHADOW_RADIUS_DP = 4f
private const val LABEL_BACKGROUND_SHADOW_DY_DP = 2f
private const val CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER = 1.4f
