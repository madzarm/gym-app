package com.example.gym_app.viewModels

import android.content.Context
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_app.api.ApiClient
import com.example.gym_app.common.TokenManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import org.gymapp.library.response.GymVisitDto

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
    // Initialize the map to store counts of visits for each hour the gym is open
    val visitCountsPerHour = (7..22).associateWith { 0 }.toMutableMap()

    _gymVisits.value
      ?.groupBy { visitDto ->
        // Parse the date string to LocalDateTime and get the hour
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          LocalDateTime.parse(visitDto.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME).hour
        } else {
          TODO("VERSION.SDK_INT < O")
        }
      }
      ?.forEach { (hour, visits) ->
        if (hour in 7..22) {
          visitCountsPerHour[hour] = visits.size
        }
      }

    // Prepare lists for the x and y axis of the graph
    val hours = visitCountsPerHour.keys.toList()
    val counts = visitCountsPerHour.values.toList()

    return Pair(hours, counts)
  }
}
