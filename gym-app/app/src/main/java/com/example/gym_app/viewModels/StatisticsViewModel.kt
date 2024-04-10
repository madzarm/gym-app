package com.example.gym_app.viewModels

import android.content.Context
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gym_app.api.ApiClient
import com.example.gym_app.common.TokenManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.gymapp.library.response.GymVisitDto
import java.time.LocalDate
import kotlin.math.roundToInt

class StatisticsViewModel : ViewModel() {

  private val _gymVisits = MutableLiveData<List<GymVisitDto>>()
  val gymVisits: MutableLiveData<List<GymVisitDto>> = _gymVisits

  suspend fun getGymVisits(context: Context, gymId: String) {
      try {
        val gymVisitDtos =
          ApiClient.apiService.getGymVisits(
            ("Bearer " + TokenManager.getAccessToken(context)) ?: "",
            gymId,
          )
        _gymVisits.value = gymVisitDtos
      } catch (e: Exception) {
        e.printStackTrace()
      }
  }


  fun prepareGraphData(): Pair<List<Int>, List<Int>> {
    val visitCountsPerHourPerDay = mutableMapOf<Int, MutableList<Int>>()

    (7..22).forEach { hour -> visitCountsPerHourPerDay[hour] = mutableListOf() }

    _gymVisits.value?.forEach { visitDto ->
      val visitDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.parse(visitDto.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
      } else {
        TODO("VERSION.SDK_INT < O")
      }
      val hour = visitDateTime.hour
      val day = visitDateTime.toLocalDate()

      if (hour in 7..22) {
        visitCountsPerHourPerDay[hour]?.add(day.hashCode())
      }
    }

    val averagesPerHour = visitCountsPerHourPerDay.mapValues { (_, visits) ->
      if (visits.isNotEmpty()) {
        (visits.size.toDouble() / visits.distinct().size).roundToInt()
      } else {
        0
      }
    }

    val hours = averagesPerHour.keys.toList()
    val averages = averagesPerHour.values.toList()

    return Pair(hours, averages)
  }

  fun prepareGraphDataPerDay(): Pair<List<Int>, List<Int>> {
    val visitCountsPerDay = (1..7).associateWith { 0 }.toMutableMap()

    _gymVisits.value
      ?.map { visitDto ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          LocalDate.parse(visitDto.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME).dayOfWeek
        } else {
          TODO("VERSION.SDK_INT < O")
        }
      }
      ?.groupBy { it }
      ?.forEach { (dayOfWeek, visits) ->
        visitCountsPerDay[dayOfWeek.value] = visits.size
      }

    val days = visitCountsPerDay.keys.toList()
    val counts = visitCountsPerDay.values.toList()

    return Pair(days, counts)
  }
}
