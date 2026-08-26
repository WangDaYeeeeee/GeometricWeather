package wangdaye.com.geometricweather.background.polling.services.permanent.observer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R

class FakeForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                GeometricWeather.NOTIFICATION_CHANNEL_ID_BACKGROUND,
                GeometricWeather.getNotificationChannelName(
                    this, GeometricWeather.NOTIFICATION_CHANNEL_ID_BACKGROUND
                ),
                NotificationManager.IMPORTANCE_LOW
            )
            channel.setShowBadge(false)
            channel.lightColor = ContextCompat.getColor(this, R.color.colorPrimary)

            NotificationManagerCompat.from(this).createNotificationChannel(channel)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            startForeground(
                GeometricWeather.NOTIFICATION_ID_RUNNING_IN_BACKGROUND,
                TimeObserverService.getForegroundNotification(this, false)
            )
        } else {
            startForeground(
                GeometricWeather.NOTIFICATION_ID_RUNNING_IN_BACKGROUND,
                TimeObserverService.getForegroundNotification(this, true)
            )
        }
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
