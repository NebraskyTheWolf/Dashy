package eu.fluffici.calendar.shared

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.JsonElement
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.EventEntity
import eu.fluffici.dashy.entities.Order
import eu.fluffici.data.network.model.AuditModel
import eu.fluffici.data.network.model.RoleModel
import eu.fluffici.data.network.model.UserModel
import eu.fluffici.data.network.model.hasRole
import kotlinx.android.parcel.Parcelize
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

data class Audit(
    val id: Int,
    val name: String,
    val type: String,
    val slug: String,
    val entry: AuditLogEntry
) {
    data class AuditLogEntry(val id: Int, val user: String, val action: String, val timestamp: String, val iconResourceId: Int, val maxPages: Int = 1)
}

@Parcelize
data class User(
    val id: Int,
    val name: String?,
    val email: String?,
    val avatar: Int,
    val avatarId: String?,
    var iconBadges: List<Int>?,
    var maxPages: Int?,
    var bio: String?,
    var pronouns: String?,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        listOf(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(this.id)
        parcel.writeString(this.name)
        parcel.writeString(this.email)
        parcel.writeInt(this.avatar)
        parcel.writeString(this.avatarId)
        parcel.writeInt(this.maxPages!!)
        parcel.writeString(this.bio)
        parcel.writeString(this.pronouns)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
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
suspend fun generateAudit(page: Int = 1): List<Audit.AuditLogEntry> = withContext(Dispatchers.IO) {
    val result = mutableListOf<Audit.AuditLogEntry>()

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/audit?page=$page")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        val element = Gson().fromJson(response.body?.string(), JsonElement::class.java)
        val events: List<AuditModel> = Json.decodeFromString(element.asJsonObject.get("data").asJsonArray.toString())

        events.forEach {
            result.add(
                Audit.AuditLogEntry(
                    it.id,
                    it.name,
                    it.type,
                    it.created_at,
                    R.drawable.baseline_density_large_svg,
                    element.asJsonObject.get("last_page").asInt
                )
            )
        }

    } else {
        Log.d("AuditLog", "Unable to fetch data from the remote server.")
    }

    return@withContext result
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun generateUserAudit(target: String = ""): List<Audit.AuditLogEntry> = withContext(Dispatchers.IO) {
    val result = mutableListOf<Audit.AuditLogEntry>()

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/user/audit?username=$target")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        val element = Gson().fromJson(response.body?.string(), JsonElement::class.java)
        val events: List<AuditModel> = Json.decodeFromString(element.asJsonObject.get("data").asJsonArray.toString())

        events.forEach {
            result.add(
                Audit.AuditLogEntry(
                    it.id,
                    it.name,
                    it.type,
                    it.created_at,
                    R.drawable.baseline_density_large_svg,
                    1
                )
            )
        }

    } else {
        Log.d("AuditLog", "Unable to fetch data from the remote server.")
    }

    return@withContext result
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun generateUsers(page: Int = 1): List<User> = withContext(Dispatchers.IO) {
    val result = mutableListOf<User>()

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/users?page=$page")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        val element = Gson().fromJson(response.body?.string(), JsonElement::class.java)
        val events: List<UserModel> = Json.decodeFromString(element.asJsonObject.get("data").asJsonArray.toString())

        events.forEach {
            result.add(
                User(
                    it.id,
                    it.name,
                    it.email,
                    it.avatar,
                    it.avatar_id,
                    determinesBadges(it),
                    element.asJsonObject.get("last_page").asInt,
                    it.bio,
                    it.pronouns
                )
            )
        }

    } else {
        Log.d("UsersManager", "Unable to fetch data from the remote server.")
    }

    return@withContext result
}

fun determinesBadges(user: UserModel): List<Int> {
    val result = mutableListOf<Int>()

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/users/roles?id=${user.id}")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        val element = Gson().fromJson(response.body?.string(), JsonElement::class.java)
        val usero: RoleModel = Json.decodeFromString(element.asJsonObject.get("data").toString())

        if (usero.terminated) {
            result.add(R.drawable.alert_hexagon_svg)
        } else {
            if (usero.roles.isEmpty()) {
                result.add(R.drawable.question_mark_svg)
            } else {
                if (hasRole(usero.roles, "admin")) {
                    result.add(R.drawable.shield_check_filled_svg)
                }

                if (hasRole(usero.roles, "dev")) {
                    result.add(R.drawable.code_svg)
                }

                if (hasRole(usero.roles, "accountant")) {
                    result.add(R.drawable.calculator_filled_svg)
                }

                if (hasRole(usero.roles, "members")) {
                    result.add(R.drawable.antenna_bars_1_svg)
                }

                if (hasRole(usero.roles, "mod")) {
                    result.add(R.drawable.hammer_svg)
                }

                if (hasRole(usero.roles, "comm")) {
                    result.add(R.drawable.message_chatbot_svg)
                }

                if (hasRole(usero.roles, "shop_manager")) {
                    result.add(R.drawable.shopping_bag_edit_svg)
                }
            }
        }
    } else {
        if (user.deleted_at != null) {
            result.add(R.drawable.lock_check_svg)
        } else {
            if (user.email_verified_at != null) {
                result.add(R.drawable.at_svg)
            } else {
                result.add(R.drawable.at_off_svg)
            }
            if (user.is_fcm) {
                result.add(R.drawable.brand_android_svg)
            }
        }
    }

    return result
}

@RequiresApi(Build.VERSION_CODES.O)
val akceDateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEE'\n'dd MMM'\n'HH:mm")