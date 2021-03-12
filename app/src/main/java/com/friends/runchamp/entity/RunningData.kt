package com.friends.runchamp.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class RunningData(
    val distance: Float,
    val activityTime: Long,
    val paceString: String,
    val burnedCalories: Double
) : Parcelable