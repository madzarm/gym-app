package com.example.gym_app.screens.owner

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Layout
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.gym_app.R
import com.example.gym_app.common.getCurrentDay
import com.example.gym_app.common.getCurrentHour
import com.example.gym_app.common.localDateTimeFromString
import com.example.gym_app.viewModels.SharedViewModel
import com.example.gym_app.viewModels.StatisticsViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.zoom.rememberVicoZoomState
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
import com.patrykandpatrick.vico.core.zoom.Zoom
import java.time.format.DateTimeFormatter
import org.gymapp.library.response.GymClassDto
import org.gymapp.library.response.GymClassReviewDto
import org.gymapp.library.response.GymClassWithReviewsDto
import org.gymapp.library.response.GymMemberDto
import org.gymapp.library.response.GymTrainerReviewDto
import org.gymapp.library.response.GymTrainerWithReviewsDto
import org.gymapp.library.response.UserDto
import org.gymapp.library.response.VisitCountByDay
import org.gymapp.library.response.VisitCountByHour
import kotlin.random.Random

private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd. MM. yyyy.")

val sampleTrainerWithReviewsDto =
  GymTrainerWithReviewsDto(
    user =
      UserDto(
        id = "",
        email = "Some email",
        firstName = "John",
        lastName = "Doe",
        profilePicUrl =
          "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png",
        createdAt = "",
        updatedAt = "",
        gymUsersIds = emptyList(),
      ),
    id = "a",
    reviews =
      listOf(
        GymTrainerReviewDto(
          id = "",
          review = "Some review",
          rating = 4,
          trainerId = "some id",
          member =
            GymMemberDto(
              id = "",
              firstName = "Marko",
              lastName = "Markic",
              gymClasses = emptyList(),
              inviteCode = "ABCDE1"
            ),
          date = "Some date",
        )
      ),
  )

val sampleGymClassWithReviewsDto =
  GymClassWithReviewsDto(
    gymClass =
      GymClassDto(
        id = "a",
        name = "Some class",
        description = "Some description",
        isRecurring = false,
        trainerId = "some id",
        instances = emptyList(),
        dateTime = "2021-10-10T10:00:00",
        duration = "60",
        isDeleted = false,
        maxParticipants = "5",
        recurringPattern = null,
      ),
    reviews =
      listOf(
        GymClassReviewDto(
          id = "",
          review = "Some review",
          rating = 4,
          member =
            GymMemberDto(
              id = "",
              firstName = "Marko",
              lastName = "Markic",
              gymClasses = emptyList(),
              inviteCode = "ABCD1"
            ),
          date = "2021-10-10T10:00:00",
          gymClassInstanceId = "Some id",
        )
      ),
  )



@Preview(showBackground = true)
@Composable
fun PreviewGymVisitHeatmap() {
  val randomData = List(7) { day ->
    VisitCountByDay(
      dayOfWeek = day + 1,  // 1-indexed day of the week
      hours = List(24) { hour ->
        VisitCountByHour(
          hour = hour,
          visitCount = Random.nextLong(0, 100)  // Random visit count for each hour
        )
      }
    )
  }

  GymVisitHeatmap(data = randomData)
}

@Composable
fun StatisticsScreen(sharedViewModel: SharedViewModel) {
  val modelProducerPerHour = remember { CartesianChartModelProducer.build() }
  val modelProducerPerDay = remember { CartesianChartModelProducer.build() }

  val statisticsViewModel: StatisticsViewModel = viewModel()

  val context = LocalContext.current
  val gymId = sharedViewModel.selectedGymUser.value?.gym?.id ?: ""
  val trainers = statisticsViewModel.trainersWithReviews.observeAsState()
  val classes = statisticsViewModel.gymClassesWithReviews.observeAsState()
  val heatmapData = statisticsViewModel.heatmapData.observeAsState()

  LaunchedEffect(Unit) {
    statisticsViewModel.fetchTrainersWithReviews(context, gymId)
    statisticsViewModel.fetchGymClassesWithReviews(context, gymId)
    statisticsViewModel.getGymVisits(context, gymId)
    statisticsViewModel.prepareHeatmapData(context, gymId)

    val (xAxisPerHour, yAxisPerHour) = statisticsViewModel.prepareGraphData()
    val (xAxisPerDay, yAxisPerDay) = statisticsViewModel.prepareGraphDataPerDay()

    modelProducerPerHour.tryRunTransaction {
      lineSeries { series(y = yAxisPerHour, x = xAxisPerHour) }
    }
    modelProducerPerDay.tryRunTransaction {
      lineSeries { series(y = yAxisPerDay, x = xAxisPerDay) }
    }
  }

  Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

    Text(
      text = "Gym Visit Trends by Day and Hour",
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(16.dp),
    )
    GymVisitHeatmap(heatmapData.value ?: emptyList())

    Column {
      Text(
        text = "Gym Visit Trends by Hour",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(16.dp),
      )
      val marker = rememberMarker()
      val zoomState = rememberVicoZoomState(
        initialZoom = Zoom.Content,
      )
      CartesianChartHost(
        zoomState = zoomState,
        chart = rememberCartesianChart(
          rememberLineCartesianLayer(
            listOf(
              rememberLineSpec(shader = DynamicShaders.color(MaterialTheme.colorScheme.primary))
            )
          ),
          startAxis = rememberStartAxis(guideline = null),
          bottomAxis = rememberBottomAxis(),
          persistentMarkers = mapOf(getCurrentHour() to marker),
        ),
        modelProducer = modelProducerPerHour,
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

    Spacer(modifier = Modifier.fillMaxWidth().height(40.dp))

    Text(
      text = "Trainers ratings",
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(16.dp),
    )

    TrainerReviewsColumn(trainers.value ?: emptyList())

    Text(
      text = "Classes ratings",
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(16.dp),
    )

    ClassReviewsColumn(classes.value ?: emptyList())
  }
}

@Composable
fun ClassReviewsColumn(classesWithReviews: List<GymClassWithReviewsDto>) {
  val listState = rememberLazyListState()
  LazyColumn(modifier = Modifier.height(300.dp), state = listState) {
    items(classesWithReviews) { gymClass ->
      val averageRating = gymClass.reviews?.map { it.rating }?.average()?.toFloat() ?: 0.0.toFloat()
      var showGymClassDialog by remember { mutableStateOf(false) }

      if (showGymClassDialog) {
        GymClassDetailDialog(
          gymClass = gymClass,
          averageRating = averageRating,
          onDismiss = { showGymClassDialog = false },
        )
      }

      GymClassItem(gymClass, averageRating) { showGymClassDialog = true }
    }
  }
}

@Composable
fun TrainerReviewsColumn(trainersWithReviews: List<GymTrainerWithReviewsDto>) {
  val listState = rememberLazyListState()
  LazyColumn(modifier = Modifier.heightIn(max = 300.dp), state = listState) {
    items(trainersWithReviews) { trainer ->
      val averageRating = trainer.reviews.map { it.rating }.average()
      var showDialog by remember { mutableStateOf(false) }

      if (showDialog) {
        TrainerDetailDialog(
          trainer = trainer,
          averageRating = averageRating.toFloat(),
          onDismiss = { showDialog = false },
        )
      }

      TrainerItem(trainer = trainer, averageRating = averageRating.toFloat()) { showDialog = true }
    }
  }
}

@Preview
@Composable
fun TrainerReviewsColumnPreview() {
  TrainerReviewsColumn(listOf(sampleTrainerWithReviewsDto))
}

@Composable
fun GymClassItem(gymClass: GymClassWithReviewsDto, averageRating: Float, onClick: () -> Unit) {
  Row(
    modifier = Modifier.padding(16.dp).fillMaxWidth().clickable(onClick = onClick),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
      modifier = Modifier.padding(start = 16.dp),
      text = gymClass.gymClass?.name ?: "Unknown class",
      style = MaterialTheme.typography.titleMedium,
    )
    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
      Column( horizontalAlignment = Alignment.CenterHorizontally) {
        if (gymClass.reviews?.isNotEmpty() == true) {
          Text(text = String.format("Rating:  %.1f", averageRating))
          RatingBar(rating = averageRating)
        } else {
          Text(text = "No reviews yet")
        }
      }
    }

  }
}

@Composable
@Preview
fun GymClassItemPreview() {
  GymClassItem(sampleGymClassWithReviewsDto, 4f) {}
}

@Composable
fun TrainerItem(trainer: GymTrainerWithReviewsDto, averageRating: Float, onClick: () -> Unit) {
  Row(
    modifier = Modifier.padding(16.dp).clickable(onClick = onClick),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Start,
  ) {
    TrainerProfileImage(trainer.user.profilePicUrl)
    Text(
      modifier = Modifier.padding(start = 16.dp),
      text = "${trainer.user.firstName} ${trainer.user.lastName}",
    )

    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
      Text(text = String.format("Rating:  %.1f", averageRating))
      RatingBar(rating = averageRating)
    }
  }
}

@Composable
fun GymClassDetailDialog(
  gymClass: GymClassWithReviewsDto,
  averageRating: Float,
  onDismiss: () -> Unit,
) {
  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier =
        Modifier.fillMaxWidth()
          .height(400.dp)
          .padding(horizontal = 32.dp)
          .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
          .verticalScroll(rememberScrollState())
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Class Details", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = gymClass.gymClass?.name ?: "Unknown class",
          style = MaterialTheme.typography.titleMedium,
        )

        Text(
          text = "Rating: %.1f".format(averageRating),
          style = MaterialTheme.typography.titleMedium,
        )

        RatingBar(rating = averageRating)

        Spacer(modifier = Modifier.height(16.dp))

        gymClass.reviews?.forEach { review ->
          HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = Color.Gray,
          )
          GymClassDialogReview(review = review)
          Spacer(modifier = Modifier.height(10.dp))
        }
      }
    }
  }
}

@Composable
fun TrainerDetailDialog(
  trainer: GymTrainerWithReviewsDto,
  averageRating: Float,
  onDismiss: () -> Unit,
) {
  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier =
        Modifier.fillMaxWidth()
          .height(400.dp)
          .padding(horizontal = 32.dp)
          .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
          .verticalScroll(rememberScrollState())
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Trainer Details", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "${trainer.user.firstName} ${trainer.user.lastName}",
          style = MaterialTheme.typography.titleMedium,
        )
        Text(
          text = "Rating: %.1f".format(averageRating),
          style = MaterialTheme.typography.titleMedium,
        )
        RatingBar(rating = averageRating)

        Spacer(modifier = Modifier.height(16.dp))

        trainer.reviews.forEach { review ->
          HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = Color.Gray,
          )
          TrainerDialogReview(review = review)
          Spacer(modifier = Modifier.height(10.dp))
        }
      }
    }
  }
}

@Composable
fun TrainerDialogReview(review: GymTrainerReviewDto) {
  Column {
    Text(
      style = MaterialTheme.typography.titleSmall,
      text = "${review.member.firstName} ${review.member.lastName}",
    )
    Spacer(modifier = Modifier.height(32.dp))
    Row {
      repeat(review.rating) {
        Icon(imageVector = Icons.Filled.Star, contentDescription = "Star", tint = Color.Black)
      }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = review.review, style = MaterialTheme.typography.bodyMedium)
    Spacer(modifier = Modifier.height(8.dp))

    val localDate = localDateTimeFromString(review.date)
    Text(text = localDate.format(dateTimeFormatter), style = MaterialTheme.typography.bodySmall)
  }
}

@Composable
fun GymClassDialogReview(review: GymClassReviewDto) {
  Column {
    Text(
      style = MaterialTheme.typography.titleSmall,
      text = "${review.member.firstName} ${review.member.lastName}",
    )
    Spacer(modifier = Modifier.height(32.dp))
    Row {
      repeat(review.rating) {
        Icon(imageVector = Icons.Filled.Star, contentDescription = "Star", tint = Color.Black)
      }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = review.review, style = MaterialTheme.typography.bodyMedium)
    Spacer(modifier = Modifier.height(8.dp))

    val localDate = localDateTimeFromString(review.date)
    Text(text = localDate.format(dateTimeFormatter), style = MaterialTheme.typography.bodySmall)
  }
}

@Preview
@Composable
fun TrainerDetailsDialogPreview() {
  TrainerDetailDialog(sampleTrainerWithReviewsDto, 4f, {})
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
    modifier = Modifier.clip(CircleShape).size(50.dp),
    contentDescription = "Profile image",
  )
}
@Composable
fun GymVisitHeatmap(data: List<VisitCountByDay>) {
  val maxVisits = data.flatMap { it.hours.map { it.visitCount } }.maxOrNull() ?: 1
  val minVisits = data.flatMap { it.hours.map { it.visitCount } }.minOrNull() ?: 0

  val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
  val hoursOfDay = (7..22).map { hour -> "${if (hour < 10) "0$hour" else hour}:00" }


  val cellHeight = 40.dp
  val headerHeight = 30.dp
  val labelWidth = 50.dp


  val startColor = Color(0xFF92ECDD)
  val endColor = Color(0xFF691FB1)


  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val constraints = this.constraints
    val cellWidth = with(LocalDensity.current) {
      (((constraints.maxWidth.toDp() - labelWidth) - 8.dp) / 7)
    }

    Row(modifier = Modifier.align(Alignment.TopStart).offset(x = labelWidth)) {
      daysOfWeek.forEach { day ->
        Text(
          text = day,
          modifier = Modifier.width(cellWidth).height(headerHeight),
          textAlign = TextAlign.Center,
          color = Color.Black
        )
      }
    }

    val localDensity = LocalDensity.current

    Column(modifier = Modifier.align(Alignment.TopStart).padding(top = 7.dp)) {
      Spacer(modifier = Modifier.height(headerHeight))
      hoursOfDay.forEach { hour ->
        Text(
          text = hour,
          modifier = Modifier.width(labelWidth).height(cellHeight),
          textAlign = TextAlign.Center,
            color = Color.Black
        )
      }
    }

    Canvas(modifier = Modifier.offset(x = labelWidth, y = headerHeight).align(Alignment.TopStart)) {
      data.forEachIndexed { dayIndex, day ->
        day.hours.forEachIndexed { hourIndex, hour ->
          val fraction = if (maxVisits > minVisits) {
            (hour.visitCount - minVisits).toFloat() / (maxVisits - minVisits).toFloat()
          } else 0f
          val rectColor = interpolateColor(fraction, startColor, endColor)
          val rectTopLeft = Offset(x = dayIndex * cellWidth.toPx(), y = hourIndex * cellHeight.toPx())
          drawRect(
            color = rectColor,
            topLeft = rectTopLeft,
            size = Size(width = cellWidth.toPx(), height = cellHeight.toPx())
          )

          val textColor = Color.Black
          drawIntoCanvas { canvas ->
            val textPaint = Paint().apply {
              textSize = with(localDensity) { 14.sp.toPx() }
              color = textColor.toArgb()
              textAlign = android.graphics.Paint.Align.CENTER
            }
            canvas.nativeCanvas.drawText(
              hour.visitCount.toString(),
              rectTopLeft.x + cellWidth.toPx() / 2,
              rectTopLeft.y + cellHeight.toPx() / 2 - (textPaint.ascent() + textPaint.descent()) / 2,
              textPaint
            )
          }
        }
      }
    }
  }
}

private fun interpolateColor(fraction: Float, startValue: Color, endValue: Color): Color {
  val startA = startValue.alpha
  val startR = startValue.red
  val startG = startValue.green
  val startB = startValue.blue

  val endA = endValue.alpha
  val endR = endValue.red
  val endG = endValue.green
  val endB = endValue.blue

  return Color(
    alpha = lerp(startA, endA, fraction),
    red = lerp(startR, endR, fraction),
    green = lerp(startG, endG, fraction),
    blue = lerp(startB, endB, fraction)
  )
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
  return (1 - fraction) * start + fraction * stop
}

@Composable
private fun RatingBar(modifier: Modifier = Modifier, rating: Float, spaceBetween: Dp = 0.dp) {

  val image = getImageBitmapFromVector(LocalContext.current, R.drawable.star_empty)!!
  val imageFull = getImageBitmapFromVector(LocalContext.current, R.drawable.star_full)!!

  val totalCount = 5

  val height = LocalDensity.current.run { image.height.toDp() }
  val width = LocalDensity.current.run { image.width.toDp() }
  val space = LocalDensity.current.run { spaceBetween.toPx() }
  val totalWidth = width * totalCount + spaceBetween * (totalCount - 1)

  Box(
    modifier.width(totalWidth).height(height).drawBehind {
      drawRating(rating, image, imageFull, space)
    }
  )
}

private fun DrawScope.drawRating(
  rating: Float,
  image: ImageBitmap,
  imageFull: ImageBitmap,
  space: Float,
) {

  val totalCount = 5

  val imageWidth = image.width.toFloat()
  val imageHeight = size.height

  val reminder = rating - rating.toInt()
  val ratingInt = (rating - reminder).toInt()

  for (i in 0 until totalCount) {

    val start = imageWidth * i + space * i

    drawImage(image = image, topLeft = Offset(start, 0f))
  }

  drawWithLayer {
    for (i in 0 until totalCount) {
      val start = imageWidth * i + space * i
      drawImage(image = imageFull, topLeft = Offset(start, 0f))
    }

    val end = imageWidth * totalCount + space * (totalCount - 1)
    val start = rating * imageWidth + ratingInt * space
    val size = end - start

    drawRect(
      Color.Transparent,
      topLeft = Offset(start, 0f),
      size = Size(size, height = imageHeight),
      blendMode = BlendMode.SrcIn,
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

fun getBitmapFromVector(drawable: Drawable): Bitmap {
  val bitmap: Bitmap

  val size = 70
  bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

  val canvas = android.graphics.Canvas(bitmap)
  drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
  drawable.draw(canvas)
  return bitmap
}

fun getImageBitmapFromVector(context: Context, drawableId: Int): ImageBitmap? {
  val drawable = ContextCompat.getDrawable(context, drawableId)
  return drawable?.let { getBitmapFromVector(it).asImageBitmap() }
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
