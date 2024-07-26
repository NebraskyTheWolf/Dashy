package eu.fluffici.dashy.events.module

import eu.fluffici.dashy.entities.Order

data class OrderPaymentStatusEvent(val order: String, val status: String)