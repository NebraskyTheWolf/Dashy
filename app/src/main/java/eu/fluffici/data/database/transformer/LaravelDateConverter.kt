package eu.fluffici.data.database.transformer

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
@RequiresApi(Build.VERSION_CODES.O)
class LaravelDateConverter {
    companion object {
        fun convertToTimestamp(laravelDateString: String): Long {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateTime = LocalDateTime.parse(laravelDateString, formatter)
            return dateTime.toEpochSecond(OffsetDateTime.now().offset)
        }
    }
}
