package eu.fluffici.dashy.events.module

import eu.fluffici.dashy.entities.Order

data class OrderPaymentEvent(val order: Order)