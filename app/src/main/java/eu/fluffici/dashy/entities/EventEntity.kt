package eu.fluffici.dashy.entities

import kotlinx.serialization.Serializable

@Serializable
data class EventEntity(
    val eventId: Int,
    val time: Time,
    val details: Details
)

@Serializable
data class Time(
    val hours: Int,
    val minutes: Int,
    val day: Int,
    val month: Int,
    val year: Int
)

@Serializable
data class Details(
    val eventName: String,
    val city: String,
    val orders: Int
)