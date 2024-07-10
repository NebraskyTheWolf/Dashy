package eu.fluffici.calendar.shared

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import com.anggrayudi.storage.extension.toBoolean
import com.google.gson.Gson
import com.google.gson.JsonElement
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.EventEntity
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.model.AccountingModel
import eu.fluffici.dashy.model.AuditModel
import eu.fluffici.dashy.model.RoleModel
import eu.fluffici.dashy.model.UserModel
import eu.fluffici.dashy.model.hasRole
import eu.fluffici.dashy.ui.activities.experiment.IAuthentication
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
    var discordId: String?,
    var isDiscordLinked: Boolean,
    var username: String?
) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        listOf(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readBoolean(),
        parcel.readString()
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(this.id)
        parcel.writeString(this.name)
        parcel.writeString(this.email)
        parcel.writeInt(this.avatar)
        parcel.writeString(this.avatarId)
        parcel.writeInt(this.maxPages!!)
        parcel.writeString(this.bio)
        parcel.writeString(this.pronouns)
        parcel.writeString(this.discordId)
        this.isDiscordLinked?.let { parcel.writeBoolean(it) }
        parcel.writeString(this.username)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        @RequiresApi(Build.VERSION_CODES.Q)
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
fun declineOtp(requestId: String): Boolean {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/user/@me/otp-request/${requestId}/decline")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        return true
    } else {
        Log.d("OTP", "Unable to fetch data from the remote server.")
    }

    return false
}

@RequiresApi(Build.VERSION_CODES.O)
fun grantOtp(requestId: String): Boolean {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/user/@me/otp-request/${requestId}/grant")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        return true
    } else {
        Log.d("OTP", "Unable to fetch data from the remote server.")
    }

    return false
}

@RequiresApi(Build.VERSION_CODES.O)
fun getLatestPendingOTP(): IAuthentication? {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/user/@me/fetch-otp")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        val element = Gson().fromJson(response.body?.string(), JsonElement::class.java)

        return if (element.asJsonObject.has("data")) {
            Json.decodeFromString<IAuthentication>(element.asJsonObject.get("data").toString())
        } else {
            null
        }
    } else {
        Log.d("OTP", "Unable to fetch data from the remote server.")
    }

    return null
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun getPendingRequest(requestId: String): IAuthentication? = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/user/@me/otp-request/${requestId}/fetch")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        val element = Gson().fromJson(response.body?.string(), JsonElement::class.java)
        return@withContext Json.decodeFromString<IAuthentication>(element.asJsonObject.get("data").toString())
    } else {
        Log.d("UsersManager", "Unable to fetch data from the remote server.")
    }

    return@withContext null
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
            val roles: List<Int> = determinesBadges(it);

            result.add(
                User(
                    it.id,
                    it.name,
                    it.email,
                    it.avatar,
                    it.avatar_id,
                    roles,
                    element.asJsonObject.get("last_page").asInt,
                    it.bio,
                    it.pronouns,
                    it.discord_id,
                    it.discord_linked.toBoolean(),
                    it.username
                )
            )
        }

    } else {
        Log.d("UsersManager", "Unable to fetch data from the remote server.")
    }

    return@withContext result
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun fetchAccountingStats(): List<AccountingModel> = withContext(Dispatchers.IO) {
    val listModel = ArrayList<AccountingModel>()

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/accounting/statistics")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        val element = Gson().fromJson(response.body?.string(), JsonElement::class.java)

        listModel.add(Json.decodeFromString<AccountingModel>(
            element.asJsonObject.get("data").toString()
        ))
    } else {
        Log.d("UsersManager", "Unable to fetch data from the remote server.")
    }

    return@withContext listModel
}


@RequiresApi(Build.VERSION_CODES.O)
suspend fun generateOrders(page: Int = 1): List<Order> = withContext(Dispatchers.IO) {
    val result = mutableListOf<Order>()

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/order-list?page=$page")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        val element = Gson().fromJson(response.body?.string(), JsonElement::class.java)
        val orders: List<Order> = Json.decodeFromString(element.asJsonObject.get("data").asJsonArray.toString())

        orders.forEach {
            result.add(Order(
                    it.id,
                    it.order_id,
                    it.first_name,
                    it.last_name,
                    it.first_address,
                    it.second_address,
                    it.postal_code,
                    it.country,
                    it.email,
                    it.phone_number,
                    it.status,
                    it.customer_id,
                    it.created_at,
                    it.updated_at
            ))
        }

    } else {
        Log.d("OrdersManager", "Unable to fetch data from the remote server.")
    }

    return@withContext result
}

fun determinesBadges(userModel: UserModel): List<Int> {
    val result = mutableListOf<Int>()

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/users/roles?id=${userModel.id}")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        val element = Gson().fromJson(response.body?.string(), JsonElement::class.java)
        val user: RoleModel = Json.decodeFromString(element.asJsonObject.get("data").toString())

        if (user.roles.isEmpty()) {
            result.add(R.drawable.question_mark_svg)
        } else {
            if (hasRole(user.roles, "admin")) {
                result.add(R.drawable.shield_check_filled_svg)
            }

            if (hasRole(user.roles, "dev")) {
                result.add(R.drawable.code_svg)
            }

            if (hasRole(user.roles, "accountant")) {
                result.add(R.drawable.calculator_filled_svg)
            }

            if (hasRole(user.roles, "mod")) {
                result.add(R.drawable.hammer_svg)
            }

            if (hasRole(user.roles, "comm")) {
                result.add(R.drawable.message_chatbot_svg)
            }

            if (hasRole(user.roles, "shop_manager")) {
                result.add(R.drawable.shopping_bag_edit_svg)
            }

            if (hasRole(user.roles, "members")) {
                result.add(R.drawable.user)
            }
        }

        if (userModel.is_fcm) {
            result.add(R.drawable.badges_filled_svg)
        }
    }

    return result
}

@RequiresApi(Build.VERSION_CODES.O)
fun ping(): Boolean {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu")
        .get()
        .build()

    val response = client.newCall(request).execute()
    return !response.isSuccessful
}

@RequiresApi(Build.VERSION_CODES.O)
val akceDateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEE'\n'dd MMM'\n'HH:mm")