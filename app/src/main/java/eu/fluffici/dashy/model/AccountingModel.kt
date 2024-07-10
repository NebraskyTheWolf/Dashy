package eu.fluffici.dashy.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountingModel(
    @SerialName("outstanding_balance")
    var outstandingBalance: String,
    @SerialName("year_balance")
    var yearBalance: String,
    @SerialName("monthly_spending")
    var monthlySpending: String,
    @SerialName("monthly_income")
    var monthlyIncome: String,
)
