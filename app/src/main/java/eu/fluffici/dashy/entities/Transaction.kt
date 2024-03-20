package eu.fluffici.dashy.entities

import kotlinx.serialization.Serializable
@Serializable
data class Transaction(
    val order_id: String?,
    val status: String?,
    val transaction_id: String?,
    val provider: String?,
    val price: Int?,
)

fun hasRefund(rx: List<Transaction>): Boolean {
    rx.forEach {
        if (it.status == "REFUNDED") {
            return true
        }
    }

    return false
}

fun hasPaid(rx: List<Transaction>): Boolean {
    rx.forEach {
        if (it.status == "PAID") {
            return true
        }
    }

    return false
}

fun hasDisputed(rx: List<Transaction>): Boolean {
    rx.forEach {
        if (it.status == "DISPUTED") {
            return true
        }
    }

    return false
}