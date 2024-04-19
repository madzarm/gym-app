package com.example.gym_app.screens.owner

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.gym_app.R
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
import com.patrykandpatrick.vico.core.model.lineSeries
import org.gymapp.library.response.GymTrainerWithReviewsDto


@Composable
fun StatisticsScreen(sharedViewModel: SharedViewModel) {
  val modelProducerPerHour = remember { CartesianChartModelProducer.build() }
  val modelProducerPerDay = remember { CartesianChartModelProducer.build() }

  val statisticsViewModel: StatisticsViewModel = viewModel()

  val context = LocalContext.current
  val gymId = sharedViewModel.selectedGymUser.value?.gym?.id ?: ""
  val trainers = statisticsViewModel.trainersWithReviews.observeAsState()

  LaunchedEffect(Unit) {
    statisticsViewModel.getTrainersWithReviews(context, gymId)
    statisticsViewModel.getGymVisits(context, gymId)

    val (xAxisPerHour, yAxisPerHour) = statisticsViewModel.prepareGraphData()
    val (xAxisPerDay, yAxisPerDay) = statisticsViewModel.prepareGraphDataPerDay()

    modelProducerPerHour.tryRunTransaction {
      lineSeries { series(y = yAxisPerHour, x = xAxisPerHour) }
    }
    modelProducerPerDay.tryRunTransaction {
      lineSeries { series(y = yAxisPerDay, x = xAxisPerDay) }
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
              rememberLineSpec(shader = DynamicShaders.color(MaterialTheme.colorScheme.primary))
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
              rememberLineSpec(shader = DynamicShaders.color(MaterialTheme.colorScheme.primary))
            )
          ),
          startAxis = rememberStartAxis(guideline = null),
          bottomAxis =
            rememberBottomAxis(
              valueFormatter = { value, _, _ ->
                val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                daysOfWeek.getOrNull(value.toInt() - 1) ?: ""
              }
            ),
          persistentMarkers = mapOf(getCurrentDay() to marker),
        ),
        modelProducerPerDay,
        marker = marker,
      )
    }

    Spacer(modifier = Modifier
      .fillMaxWidth()
      .height(40.dp))

    Text(
      text = "Trainers ratings",
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(16.dp),
    )

    val listState = rememberLazyListState()
    LazyColumn(state = listState) {
      val trainersWithReviews = trainers.value ?: emptyList()
      items(trainersWithReviews) { trainer ->
        val averageRating = trainer.reviews.map { it.rating }.average()

        TrainerItem(trainer = trainer, averageRating = averageRating.toFloat())
      }
    }
  }
}

@Composable
fun TrainerItem(trainer: GymTrainerWithReviewsDto, averageRating: Float) {
  Row (
    modifier = Modifier.padding(16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Start,
  ) {
    TrainerProfileImage(trainer.user.profilePicUrl)

    Text(modifier = Modifier.padding(start = 16.dp), text = "${trainer.user.firstName} ${trainer.user.lastName}")

    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
      Text(text = String.format("Rating:  %.1f", averageRating))
      RatingBar(rating = averageRating)
    }
  }
}

@Composable
fun TrainerProfileImage(profilePicUrl: String?) {
  val painter =
    rememberAsyncImagePainter(
      ImageRequest.Builder(LocalContext.current)
        .data(data = profilePicUrl ?: "")
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
    modifier = Modifier
      .clip(CircleShape)
      .size(50.dp),
    contentDescription = "Profile image",
  )
}

@Composable
private fun RatingBar(
  modifier: Modifier = Modifier,
  rating: Float,
  spaceBetween: Dp = 0.dp
) {

  val image = getImageBitmapFromVector(LocalContext.current, R.drawable.star_empty)!!
  val imageFull = getImageBitmapFromVector(LocalContext.current, R.drawable.star_full)!!

  val totalCount = 5

  val height = LocalDensity.current.run { image.height.toDp() }
  val width = LocalDensity.current.run { image.width.toDp() }
  val space = LocalDensity.current.run { spaceBetween.toPx() }
  val totalWidth = width * totalCount + spaceBetween * (totalCount - 1)


  Box(
    modifier
      .width(totalWidth)
      .height(height)
      .drawBehind {
        drawRating(rating, image, imageFull, space)
      })
}

private fun DrawScope.drawRating(
  rating: Float,
  image: ImageBitmap,
  imageFull: ImageBitmap,
  space: Float
) {

  val totalCount = 5

  val imageWidth = image.width.toFloat()
  val imageHeight = size.height

  val reminder = rating - rating.toInt()
  val ratingInt = (rating - reminder).toInt()

  for (i in 0 until totalCount) {

    val start = imageWidth * i + space * i

    drawImage(
      image = image,
      topLeft = Offset(start, 0f)
    )
  }

  drawWithLayer {
    for (i in 0 until totalCount) {
      val start = imageWidth * i + space * i
      drawImage(
        image = imageFull,
        topLeft = Offset(start, 0f)
      )
    }

    val end = imageWidth * totalCount + space * (totalCount - 1)
    val start = rating * imageWidth + ratingInt * space
    val size = end - start

    drawRect(
      Color.Transparent,
      topLeft = Offset(start, 0f),
      size = Size(size, height = imageHeight),
      blendMode = BlendMode.SrcIn
    )
  }
}

private fun DrawScope.drawWithLayer(block: DrawScope.() -> Unit) {
  with(drawContext.canvas.nativeCanvas) {
    val checkPoint = saveLayer(null, null)
    block()
    restoreToCount(checkPoint)
  }
}

fun getBitmapFromVector(
  drawable: Drawable
): Bitmap {
  val bitmap: Bitmap

  val size = 70
  bitmap = Bitmap.createBitmap(
    size,
    size,
    Bitmap.Config.ARGB_8888
  )

  val canvas = android.graphics.Canvas(bitmap)
  drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
  drawable.draw(canvas)
  return bitmap
}

fun getImageBitmapFromVector(context: Context, drawableId: Int): ImageBitmap? {
  val drawable = ContextCompat.getDrawable(context, drawableId)
  return drawable?.let {
    getBitmapFromVector(it).asImageBitmap()
  }
}

fun getCurrentHour(): Float {
  return java.time.LocalDateTime.now().hour.toFloat()
}

fun getCurrentDay(): Float {
  return java.time.LocalDate.now().dayOfWeek.value.toFloat()
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
