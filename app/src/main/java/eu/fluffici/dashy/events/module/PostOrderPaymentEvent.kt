package eu.fluffici.dashy.events.module

import eu.fluffici.dashy.entities.Order

data class PostOrderPaymentEvent(val order: Order, val type: String)