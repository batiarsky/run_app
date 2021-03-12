package com.friends.runchamp.utils.extantions

import kotlin.math.floor

fun Double.paceToString(): String {
    val paceMinutes = floor(this / 60).toInt()
    val paceSeconds = (this - paceMinutes * 60).toInt()

    return "$paceMinutes:$paceSeconds"
}

