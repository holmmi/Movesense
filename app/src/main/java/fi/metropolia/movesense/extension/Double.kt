package fi.metropolia.movesense.extension

import kotlin.math.round
//from https://discuss.kotlinlang.org/t/how-do-you-round-a-number-to-n-decimal-places/8843/2 by fvasco

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}