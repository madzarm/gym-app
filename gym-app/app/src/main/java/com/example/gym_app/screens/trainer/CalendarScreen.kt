package com.example.gym_app.screens.trainer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gym_app.common.AppRoutes
import com.example.gym_app.viewModels.GymClassViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt
import kotlinx.datetime.DayOfWeek
import org.gymapp.library.response.GymClassDto

private val EventTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
private val DayFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")
private val HourFormatter = DateTimeFormatter.ofPattern("h a")

private var eventPositions = mapOf<Event, Pair<Int, Int>>()

@Composable
fun CalendarScreen(
  navHostController: NavHostController,
  viewModel: GymClassViewModel,
  onClassClicked: () -> Unit = {
    navHostController.navigate(AppRoutes.GYM_CLASS_INSTANCE_SCREEN)
  },
) {
  val gymClass = viewModel.selectedGymClass.observeAsState().value
  var events = remember(gymClass) { mutableStateListOf<Event>() }

  var currentWeekStart by remember {
    mutableStateOf(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
  }

  // When gymClass changes, update events for the calendar
  LaunchedEffect(gymClass) {
    if (gymClass != null && gymClass.isRecurring) {
      events.clear()
      events.addAll(mapGymClassToEvents(gymClass))
      eventPositions = calculateEventPositions(events)
    }
  }

  Surface {
    Column {
      WeekNavigationRow(currentWeekStart = currentWeekStart) { newWeekStart ->
        currentWeekStart = newWeekStart
      }
      Schedule(
        events =
          events.filter { event ->
            val eventDate = event.start.toLocalDate()
            eventDate >= currentWeekStart && eventDate <= currentWeekStart.plusDays(6)
          },
        minDate = currentWeekStart,
        maxDate = currentWeekStart.plusDays(6),
        eventContent = { event ->
          BasicEvent(event = event) { onEventClicked(navHostController, viewModel, event, onClassClicked) }
        },
      )
    }
  }
}

fun onEventClicked(
  navHostController: NavHostController,
  viewModel: GymClassViewModel,
  event: Event,
  onClassClicked: () -> Unit,
) {
  viewModel.updateInstance {
    copy(
      name = event.name,
      classId = event.classId ?: "",
      description = event.description ?: "",
      originalDateTime = event.originalDateTime.toString(),
      dateTime = event.start.toString(),
      duration = event.duration ?: "0",
      maxParticipants = event.maxParticipants?.toString() ?: "0",
      participantsIds = event.participantsIds,
      trainerId = event.trainerId,
      isCanceled = false,
    )
  }

  viewModel.updateInstanceDto {
    copy(
      name = event.name,
      classId = event.classId ?: "",
      description = event.description ?: "",
      dateTime = event.start.toString(),
      duration = event.duration ?: "0",
      maxParticipants = event.maxParticipants?.toString() ?: "0",
      participantsIds = event.participantsIds,
      trainerId = event.trainerId,
    )
  }
  onClassClicked()
}

fun mapGymClassToEvents(gymClass: GymClassDto): List<Event> {
  val events = mutableListOf<Event>()
  if (gymClass.isRecurring && gymClass.recurringPattern != null) {
    val dateTime = LocalDateTime.parse(gymClass.dateTime)
    val startDate = dateTime.toLocalDate()
    val dayOfWeeks = gymClass.recurringPattern!!.dayOfWeeks.map { DayOfWeek.of(it + 1) }
    for (i in 0 until (gymClass.recurringPattern!!.maxNumOfOccurrences ?: 52)) {
      dayOfWeeks.forEach { dayOfWeek ->
        val eventDate =
          startDate.plusWeeks(i.toLong()).with(TemporalAdjusters.nextOrSame(dayOfWeek))

        if (!gymClass.instances.any {
            LocalDateTime.parse(it.dateTime).toLocalDate().isEqual(eventDate)
          }) {
          events.add(
            Event(
              classId = gymClass.id,
              name = gymClass.name ?: "Class",
              description = gymClass.description ?: "",
              start =
                LocalDateTime.of(eventDate, LocalDateTime.parse(gymClass.dateTime).toLocalTime()),
              end =
                LocalDateTime.of(
                  eventDate,
                  LocalDateTime.parse(gymClass.dateTime)
                    .toLocalTime()
                    .plusMinutes(gymClass.duration?.toLong() ?: 60),
                ),
              color = Color(0xFF1B998B),
              originalDateTime =
                LocalDateTime.of(eventDate, LocalDateTime.parse(gymClass.dateTime).toLocalTime()),
              duration = gymClass.duration,
              maxParticipants = gymClass.maxParticipants?.let { Integer.parseInt(it) } ?: 0,
              trainerId = gymClass.trainerId ?: "",
            )
          )
        }
      }
    }
  }
  gymClass.instances.forEach { instance ->
    if (instance.gymClassModifiedInstance?.isCanceled != true) {
      val modifiedInstance = instance.gymClassModifiedInstance
      val startDateTime = LocalDateTime.parse(modifiedInstance?.dateTime ?: instance.dateTime)
      val duration = modifiedInstance?.duration ?: instance.duration
      val endDateTime = startDateTime.plusMinutes(duration.toLong())
      val instanceDescription = modifiedInstance?.description ?: instance.description
      val instanceName = instance.name
      val trainerId = modifiedInstance?.trainerId ?: instance.trainerId
      val maxParticipants = Integer.parseInt(modifiedInstance?.maxParticipants ?: instance.maxParticipants)

      events.add(
        Event(
          classId = gymClass.id,
          name = instanceName,
          description = instanceDescription,
          start = startDateTime,
          end = endDateTime,
          color = if (modifiedInstance != null) Color(0xFFF4BFDB) else Color(0xFFAFBBF2), // Different color if modified
          originalDateTime = LocalDateTime.parse(instance.dateTime),
          duration = duration,
          maxParticipants = maxParticipants,
          trainerId = trainerId,
          participantsIds = instance.participantsIds
        )
      )
    }
  }
  return events
}

@Composable
fun WeekNavigationRow(currentWeekStart: LocalDate, onWeekChanged: (LocalDate) -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    IconButton(onClick = { onWeekChanged(currentWeekStart.minusWeeks(1)) }) {
      Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Week")
    }
    Text(
      currentWeekStart.format(DateTimeFormatter.ofPattern("MMM dd")) +
        " - " +
        currentWeekStart.plusDays(6).format(DateTimeFormatter.ofPattern("MMM dd")),
      textAlign = TextAlign.Center,
    )
    IconButton(onClick = { onWeekChanged(currentWeekStart.plusWeeks(1)) }) {
      Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Week")
    }
  }
}

@Composable
fun Schedule(
  events: List<Event>,
  modifier: Modifier = Modifier,
  eventContent: @Composable (event: Event) -> Unit = { BasicEvent(event = it, onClick = {}) },
  minDate: LocalDate = events.minByOrNull(Event::start)!!.start.toLocalDate(),
  maxDate: LocalDate = events.maxByOrNull(Event::end)!!.end.toLocalDate(),
) {
  val dayWidth = 256.dp
  val hourHeight = 64.dp
  val verticalScrollState = rememberScrollState()
  val horizontalScrollState = rememberScrollState()
  var sidebarWidth by remember { mutableStateOf(0) }

  Column(modifier = modifier) {
    ScheduleHeader(
      minDate = minDate,
      maxDate = maxDate,
      dayWidth = dayWidth,
      modifier =
        Modifier.padding(start = with(LocalDensity.current) { sidebarWidth.toDp() })
          .horizontalScroll(horizontalScrollState),
    )
    Row(modifier = Modifier.weight(1f)) {
      ScheduleSidebar(
        hourHeight = hourHeight,
        modifier =
          Modifier.verticalScroll(verticalScrollState).onGloballyPositioned {
            sidebarWidth = it.size.width
          },
      )
      BasicSchedule(
        events = events,
        eventContent = eventContent,
        minDate = minDate,
        maxDate = maxDate,
        dayWidth = dayWidth,
        hourHeight = hourHeight,
        modifier =
          Modifier.weight(1f)
            .verticalScroll(verticalScrollState)
            .horizontalScroll(horizontalScrollState),
      )
    }
  }
}

@Composable
fun BasicSchedule(
  events: List<Event>,
  modifier: Modifier = Modifier,
  eventContent: @Composable (event: Event) -> Unit = { BasicEvent(event = it) {} },
  minDate: LocalDate = events.minByOrNull(Event::start)!!.start.toLocalDate(),
  maxDate: LocalDate = events.maxByOrNull(Event::end)!!.end.toLocalDate(),
  dayWidth: Dp,
  hourHeight: Dp,
) {
  val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1
  val dividerColor = Color.LightGray

  Layout(
    content = {
      events.sortedBy(Event::start).forEach { event ->
        Box(modifier = Modifier.eventData(event)) { eventContent(event) }
      }
    },
    modifier =
      modifier.drawBehind {
        repeat(23) {
          drawLine(
            dividerColor,
            start = Offset(0f, (it + 1) * hourHeight.toPx()),
            end = Offset(size.width, (it + 1) * hourHeight.toPx()),
            strokeWidth = 1.dp.toPx(),
          )
        }
        repeat(numDays - 1) {
          drawLine(
            dividerColor,
            start = Offset((it + 1) * dayWidth.toPx(), 0f),
            end = Offset((it + 1) * dayWidth.toPx(), size.height),
            strokeWidth = 1.dp.toPx(),
          )
        }
      },
  ) { measureables, constraints ->
    val height = hourHeight.roundToPx() * 24
    val width = dayWidth.roundToPx() * numDays
    val placeablesWithEvents =
      measureables.map { measurable ->
        val event = measurable.parentData as Event
        val eventDurationMinutes = ChronoUnit.MINUTES.between(event.start, event.end)
        val eventHeight = ((eventDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
        val slotInfo = eventPositions[event] ?: Pair<Int, Int>(0, 1)
        val placeable =
          measurable.measure(
            constraints.copy(
              minWidth = (dayWidth.roundToPx() / slotInfo.second),
              maxWidth = (dayWidth.roundToPx() / slotInfo.second),
              minHeight = eventHeight,
              maxHeight = eventHeight,
            )
          )
        Triple(placeable, event, slotInfo)
      }
    layout(width, height) {
      placeablesWithEvents.forEach { (placeable, event, slotInfo) ->
        val startY =
          ((event.start.toLocalTime().toSecondOfDay() / 3600f) * hourHeight.toPx()).roundToInt()
        val startX =
          dayWidth.roundToPx() *
            ChronoUnit.DAYS.between(minDate, event.start.toLocalDate()).toInt() +
            slotInfo.first * (dayWidth.roundToPx() / slotInfo.second)
        placeable.place(startX, startY)
      }
    }
  }
}

fun calculateEventPositions(events: List<Event>): Map<Event, Pair<Int, Int>> {
  val sortedEvents = events.sortedBy { it.start }
  val eventPositions = mutableMapOf<Event, Pair<Int, Int>>()
  val activeEvents = mutableListOf<Event>()

  for (currentEvent in sortedEvents) {
    // Remove events that have finished before the current event starts
    activeEvents.removeIf { it.end <= currentEvent.start }

    // Find the lowest slot number not occupied by overlapping events
    val occupiedSlots = activeEvents.mapNotNull { eventPositions[it]?.first }.toSet()
    var availableSlot = 0
    while (occupiedSlots.contains(availableSlot)) {
      availableSlot++
    }

    // Add the current event into the active events list
    activeEvents.add(currentEvent)
    // Assign the current event to the available slot
    eventPositions[currentEvent] = Pair(availableSlot, 0) // Temporarily set total slots as 0

    // Update the total slots count: all active events need to have the updated total
    val totalSlots = activeEvents.map { eventPositions[it]!!.first }.maxOrNull()!! + 1
    for (event in activeEvents) {
      val slot = eventPositions[event]!!.first
      eventPositions[event] = Pair(slot, totalSlots)
    }
  }

  return eventPositions
}

@Composable
fun ScheduleHeader(
  minDate: LocalDate,
  maxDate: LocalDate,
  dayWidth: Dp,
  modifier: Modifier = Modifier,
  dayHeader: @Composable (day: LocalDate) -> Unit = { BasicDayHeader(day = it) },
) {
  Row(modifier = modifier) {
    val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1
    repeat(numDays) { i ->
      Box(modifier = Modifier.width(dayWidth)) { dayHeader(minDate.plusDays(i.toLong())) }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun ScheduleHeaderPreview() {
  Surface {
    ScheduleHeader(
      minDate = LocalDate.now(),
      maxDate = LocalDate.now().plusDays(5),
      dayWidth = 256.dp,
    )
  }
}

@Composable
fun BasicDayHeader(day: LocalDate, modifier: Modifier = Modifier) {
  Text(
    text = day.format(DayFormatter),
    textAlign = TextAlign.Center,
    modifier = modifier.fillMaxWidth().padding(4.dp),
  )
}

@Preview(showBackground = true)
@Composable
fun BasicDayHeaderPreview() {
  Surface { BasicDayHeader(day = LocalDate.now()) }
}

@Composable
fun ScheduleSidebar(
  hourHeight: Dp,
  modifier: Modifier = Modifier,
  label: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) },
) {
  Column(modifier = modifier) {
    val startTime = LocalTime.MIN
    repeat(24) { i ->
      Box(modifier = Modifier.height(hourHeight)) { label(startTime.plusHours(i.toLong())) }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun ScheduleSidebarPreview() {
  Surface { ScheduleSidebar(hourHeight = 64.dp) }
}

@Composable
fun BasicSidebarLabel(time: LocalTime, modifier: Modifier = Modifier) {
  Text(text = time.format(HourFormatter), modifier = modifier.fillMaxHeight().padding(4.dp))
}

@Preview(showBackground = true)
@Composable
fun BasicSidebarLabelPreview() {
  Surface { BasicSidebarLabel(time = LocalTime.NOON, Modifier.sizeIn(maxHeight = 64.dp)) }
}

@Composable
fun BasicEvent(event: Event, modifier: Modifier = Modifier, onClick: () -> Unit) {
  Column(
    modifier =
      modifier
        .clickable(onClick = onClick)
        .fillMaxSize()
        .padding(end = 2.dp, bottom = 2.dp)
        .background(event.color, shape = RoundedCornerShape(4.dp))
        .padding(4.dp)
  ) {
    Text(
      text = "${event.start.format(EventTimeFormatter)} - ${event.end.format(EventTimeFormatter)}",
      style = MaterialTheme.typography.labelMedium,
    )

    Text(
      text = event.name,
      style = MaterialTheme.typography.bodyLarge,
      fontWeight = FontWeight.Bold,
    )

    if (event.description != null) {
      Text(
        text = event.description,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

private class EventDataModifier(val event: Event) : ParentDataModifier {
  override fun Density.modifyParentData(parentData: Any?) = event
}

private fun Modifier.eventData(event: Event) = this.then(EventDataModifier(event))

data class Event(
  val classId: String? = "",
  val name: String,
  val color: Color,
  val start: LocalDateTime,
  val end: LocalDateTime,
  val description: String? = null,
  val originalDateTime: LocalDateTime? = null,
  val duration: String? = "0",
  val maxParticipants: Int? = 0,
  val participantsIds: List<String> = emptyList(),
  val trainerId: String = "",
)
