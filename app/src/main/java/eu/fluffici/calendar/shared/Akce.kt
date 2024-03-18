package eu.fluffici.calendar.shared

import android.os.Build
import android.util.Log
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.JsonElement
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.EventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private typealias Info = Akce.Info

data class Akce(
    val time: LocalDateTime,
    val title: Info,
    val description: Info,
    @ColorRes val color: Int,
) {
    data class Info(val key: String, val value: String, val status: String)
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun generateFlights(): List<Akce> = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/events?format=flight")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    val result = mutableListOf<Akce>()

    if (response.isSuccessful) {
        val element = Gson().fromJson(response.body?.string(), JsonElement::class.java)
        val events: List<EventEntity> = Json.decodeFromString(element.asJsonObject.get("data").asJsonArray.toString())

        events.forEach {
            YearMonth.of(it.time.year, it.time.month).atDay(it.time.day).also { date ->
                when (it.details.status) {
                    "INCOMING", "STARTED" -> {
                        result.add(
                            Akce(
                                date.atTime(it.time.hours, it.time.minutes),
                                Info(it.details.eventName, it.details.city, it.details.status),
                                Info("Pending orders", "${it.details.orders}", it.details.status),
                                R.color.colorPrimary,
                            )
                        )
                    }
                    else -> {
                        result.add(
                            Akce(
                                date.atTime(it.time.hours, it.time.minutes),
                                Info(it.details.eventName, it.details.status, it.details.status),
                                Info("Pending orders", "${it.details.orders}", it.details.status),
                                R.color.colorPrimary,
                            )
                        )
                    }
                }
            }
        }
    } else {
        Log.d("AkceCalendar", "Unable to fetch data from the remote server.")
    }

    return@withContext result
}

@RequiresApi(Build.VERSION_CODES.O)
val akceDateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEE'\n'dd MMM'\n'HH:mm")