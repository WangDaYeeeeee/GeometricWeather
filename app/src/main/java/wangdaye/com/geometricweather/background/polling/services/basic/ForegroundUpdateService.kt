package wangdaye.com.geometricweather.background.polling.services.basic

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.Weather

abstract class ForegroundUpdateService : UpdateService() {

    private var finishedCount = 0

    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                GeometricWeather.NOTIFICATION_CHANNEL_ID_BACKGROUND,
                GeometricWeather.getNotificationChannelName(
                    this, GeometricWeather.NOTIFICATION_CHANNEL_ID_BACKGROUND
                ),
                NotificationManager.IMPORTANCE_MIN
            )
            channel.setShowBadge(false)
            channel.lightColor = ContextCompat.getColor(this, R.color.colorPrimary)
            NotificationManagerCompat.from(this).createNotificationChannel(channel)
        }

        finishedCount = 0
        startForeground(
            foregroundNotificationId,
            getForegroundNotification(0).build()
        )

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                GeometricWeather.NOTIFICATION_CHANNEL_ID_BACKGROUND,
                GeometricWeather.getNotificationChannelName(
                    this, GeometricWeather.NOTIFICATION_CHANNEL_ID_BACKGROUND
                ),
                NotificationManager.IMPORTANCE_MIN
            )
            channel.setShowBadge(false)
            channel.lightColor = ContextCompat.getColor(this, R.color.colorPrimary)
            NotificationManagerCompat.from(this).createNotificationChannel(channel)
        }

        startForeground(
            foregroundNotificationId,
            getForegroundNotification(0).build()
        )
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        NotificationManagerCompat.from(this).cancel(foregroundNotificationId)
    }

    override fun stopService(updateFailed: Boolean) {
        stopForeground(true)
        NotificationManagerCompat.from(this).cancel(foregroundNotificationId)
        super.stopService(updateFailed)
    }

    open fun getForegroundNotification(total: Int): NotificationCompat.Builder {
        return NotificationCompat.Builder(
            this,
            GeometricWeather.NOTIFICATION_CHANNEL_ID_BACKGROUND
        ).setSmallIcon(R.drawable.ic_running_in_background)
            .setContentTitle(getString(R.string.geometric_weather))
            .setContentText(
                getString(R.string.feedback_updating_weather_data) + if (total == 0) {
                    ""
                } else {
                    " (${finishedCount + 1}/$total)"
                }
            ).setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setProgress(0, 0, true)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setAutoCancel(false)
            .setOngoing(false)
    }

    abstract val foregroundNotificationId: Int

    override fun onUpdateCompleted(
        location: Location,
        old: Weather?,
        succeed: Boolean,
        index: Int,
        total: Int
    ) {
        super.onUpdateCompleted(location, old, succeed, index, total)
        finishedCount++
        if (finishedCount != total) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(this).notify(
                    foregroundNotificationId,
                    getForegroundNotification(total).build()
                )
            }
        }
    }
}
