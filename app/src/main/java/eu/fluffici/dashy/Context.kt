package eu.fluffici.dashy

import android.content.Context
import eu.fluffici.security.DeviceInfo

fun Context.getDeviceInfo(): DeviceInfo {
    return DeviceInfo()
}

fun Context.isUPCAFormat(input: String): Boolean {
    if (input.length != 12)
        return false
    if (!input.all { it.isDigit() })
        return false

    val checkDigit = input.last().toString().toInt()
    val dataDigits = input.substring(0, 11).map { it.toString().toInt() }

    val sumOdd = dataDigits.filterIndexed { index, _ -> index % 2 == 0 }.sum()
    val sumEven = dataDigits.filterIndexed { index, _ -> index % 2 != 0 }.sum()

    val totalSum = (sumOdd * 3) + sumEven
    val calculatedCheckDigit = (10 - (totalSum % 10)) % 10

    return checkDigit == calculatedCheckDigit
}