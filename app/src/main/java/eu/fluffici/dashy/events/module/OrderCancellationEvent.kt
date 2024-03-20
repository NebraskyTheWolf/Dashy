package eu.fluffici.dashy.events.module

import eu.fluffici.dashy.entities.Order
data class OrderCancellationEvent(val order: Order)