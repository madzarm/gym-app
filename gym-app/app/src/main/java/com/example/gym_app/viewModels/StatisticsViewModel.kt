package com.example.gym_app.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_app.api.ApiClient
import com.example.gym_app.common.TokenManager
import kotlinx.coroutines.launch
import org.gymapp.library.response.GymClassWithReviewsDto
import org.gymapp.library.response.GymTrainerWithReviewsDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.gymapp.library.response.GymVisitDto
import java.time.LocalDate
import kotlin.math.roundToInt

class StatisticsViewModel : ViewModel() {

  private val _gymVisits = MutableLiveData<List<GymVisitDto>>()
  val gymVisits: MutableLiveData<List<GymVisitDto>> = _gymVisits

  private val _trainersWithReviews = MutableLiveData<List<GymTrainerWithReviewsDto>>()
  val trainersWithReviews: MutableLiveData<List<GymTrainerWithReviewsDto>> = _trainersWithReviews

  private val _gymClassesWithReviews = MutableLiveData<List<GymClassWithReviewsDto>>()
  val gymClassesWithReviews: MutableLiveData<List<GymClassWithReviewsDto>> = _gymClassesWithReviews

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

  suspend fun fetchGymClassesWithReviews(context: Context, gymId: String) {
    try {
      val gymClassesWithReviewsDto =
        ApiClient.apiService.getGymClassesWithReviews(
          ("Bearer " + TokenManager.getAccessToken(context)) ?: "",
          gymId,
        )
      _gymClassesWithReviews.value = gymClassesWithReviewsDto
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun fetchTrainersWithReviews(context: Context, gymId: String) =
    viewModelScope.launch {
        try {
            val trainersWithReviewsDto =
            ApiClient.apiService.getTrainersWithReviews(
                ("Bearer " + TokenManager.getAccessToken(context)) ?: "",
                gymId,
            )
            _trainersWithReviews.value = trainersWithReviewsDto
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


  fun prepareGraphData(): Pair<List<Int>, List<Int>> {
    val visitCountsPerHourPerDay = mutableMapOf<Int, MutableList<Int>>()

    (7..22).forEach { hour -> visitCountsPerHourPerDay[hour] = mutableListOf() }

    _gymVisits.value?.forEach { visitDto ->
      val visitDateTime =
        LocalDateTime.parse(visitDto.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
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
        LocalDate.parse(visitDto.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME).dayOfWeek
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
