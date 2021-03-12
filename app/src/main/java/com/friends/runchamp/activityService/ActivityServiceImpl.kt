package com.friends.runchamp.activityService

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.TaskStackBuilder
import com.friends.runchamp.R
import com.friends.runchamp.entity.RunningData
import com.friends.runchamp.repository.map.MapManager
import com.friends.runchamp.view.MainActivity
import com.friends.runchamp.view.start.StartFragment.Companion.ACTIVITY_DATA_ACTION
import org.koin.android.ext.android.inject


class ActivityServiceImpl : Service(), ActivityService, MapManagerListener {

    companion object {
        private const val NOTIFICATION_ID = 1000
        private const val CHANNEL_ID = "1"
        private const val DESCRIPTION = "description"
        const val ACTIVITY_DATA = "activity_data"
    }

    private val mapManager: MapManager by inject()
    private val binder = ActivityServiceBinder()
    private var mNotificationManager: NotificationManager? = null
    private var mRunningData: RunningData? = null
    private lateinit var mNotificationChannel: NotificationChannel

    override fun onCreate() {
        super.onCreate()
        mapManager.setMapManagerListener(this)
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel =
                NotificationChannel(CHANNEL_ID, DESCRIPTION, NotificationManager.IMPORTANCE_HIGH)
            mNotificationChannel.enableLights(true)
            mNotificationChannel.lightColor = Color.GREEN
            mNotificationChannel.enableVibration(false)
            mNotificationManager?.createNotificationChannel(mNotificationChannel)
        }
        startForeground(NOTIFICATION_ID, getNotification(RunningData(0f, 0L, "", 0.0)))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    inner class ActivityServiceBinder : Binder() {
        fun getService(): ActivityServiceImpl = this@ActivityServiceImpl
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private fun getNotification(runningData: RunningData): Notification {
        val activityProgress = resources.getString(R.string.start_fragment_activity_progress_text)
        val time = resources.getString(
            R.string.start_fragment_time_text,
            runningData.activityTime.toString()
        )
        val distance = resources.getString(
            R.string.start_fragment_distance_text,
            runningData.distance.toString()
        )
        val burnedCalories = resources.getString(
            R.string.start_fragment_burned_calories_text,
            runningData.burnedCalories.toString()
        )
        val paceString =
            resources.getString(R.string.start_fragment_pace_text, runningData.paceString)
        val message = "$distance, $time, $burnedCalories, $paceString"

        val mainActivityIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(mainActivityIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(activityProgress)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(resources.getColor(R.color.colorAccent, null))
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
                    .build()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                Notification.Builder(this)
                    .setContentTitle(activityProgress)
                    .setContentText(message)
                    .setColor(resources.getColor(R.color.colorAccent))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
                    .build()
            }
            else -> {
                Notification.Builder(this)
                    .setContentTitle(activityProgress)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
                    .build()
            }
        }
    }

    override fun onActivityData(runningData: RunningData) {
        mRunningData = runningData
        sendActivityDataBroadcast()
        mNotificationManager?.notify(NOTIFICATION_ID, getNotification(runningData))
    }

    override fun onStopActivityService() {
        stopForeground(true)
        stopSelf()
    }

    override fun getActivityData() {
        sendActivityDataBroadcast()
    }

    private fun sendActivityDataBroadcast() {
        sendBroadcast(Intent().apply {
            action = ACTIVITY_DATA_ACTION
            putExtra(ACTIVITY_DATA, mRunningData)
        })
    }
}