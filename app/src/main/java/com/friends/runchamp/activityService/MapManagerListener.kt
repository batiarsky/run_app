package com.friends.runchamp.activityService

import com.friends.runchamp.entity.RunningData

interface MapManagerListener {
    fun onActivityData(runningData: RunningData)
}