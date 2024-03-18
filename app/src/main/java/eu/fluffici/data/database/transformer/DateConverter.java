package eu.fluffici.data.database.transformer;

import android.os.Build;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateConverter {

    @TypeConverter
    public static Instant toDate(Long timestamp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return timestamp == null ? null : ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).toInstant();
        }

        return null;
    }

    @TypeConverter
    public static Long toTimestamp(ZonedDateTime date) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return date == null ? null : date.toEpochSecond();
        }

        return System.currentTimeMillis();
    }
}