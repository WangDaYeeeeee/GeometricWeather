package wangdaye.com.geometricweather.background.polling.services.permanent.observer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.background.polling.services.permanent.update.ForegroundNormalUpdateService
import wangdaye.com.geometricweather.background.polling.services.permanent.update.ForegroundTodayForecastUpdateService
import wangdaye.com.geometricweather.background.polling.services.permanent.update.ForegroundTomorrowForecastUpdateService
import wangdaye.com.geometricweather.settings.SettingsManager
import java.util.Calendar

class TimeObserverService : Service() {

    private var receiver: TimeTickReceiver? = null

    private inner class TimeTickReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action ?: return
            when (action) {
                Intent.ACTION_TIME_TICK -> doRefreshWork()
                Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> {
                    lastUpdateNormalViewTime = System.currentTimeMillis()
                    lastTodayForecastTime = System.currentTimeMillis()
                    lastTomorrowForecastTime = System.currentTimeMillis()
                    doRefreshWork()
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundNotification()
        initData()
        registerTimeTickReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        readData(intent)
        doRefreshWork()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterTimeTickReceiver()
        stopForeground(true)
    }

    private fun initData() {
        pollingRate = 1.5f
        todayForecastTime = SettingsManager.DEFAULT_TODAY_FORECAST_TIME
        tomorrowForecastTime = SettingsManager.DEFAULT_TOMORROW_FORECAST_TIME
        lastUpdateNormalViewTime = System.currentTimeMillis()
        lastTodayForecastTime = System.currentTimeMillis()
        lastTomorrowForecastTime = System.currentTimeMillis()
    }

    private fun readData(intent: Intent?) {
        if (intent != null) {
            if (intent.getBooleanExtra(KEY_CONFIG_CHANGED, false)) {
                pollingRate = intent.getFloatExtra(KEY_POLLING_RATE, 1.5f)
                lastTodayForecastTime = System.currentTimeMillis()
                lastTomorrowForecastTime = System.currentTimeMillis()
                todayForecastTime = intent.getStringExtra(KEY_TODAY_FORECAST_TIME)
                tomorrowForecastTime = intent.getStringExtra(KEY_TOMORROW_FORECAST_TIME)
            }
            if (intent.getBooleanExtra(KEY_POLLING_FAILED, false)) {
                lastUpdateNormalViewTime = System.currentTimeMillis() - pollingInterval + 15 * 60 * 1000
            }
        }
    }

    private fun doRefreshWork() {
        if (System.currentTimeMillis() - lastUpdateNormalViewTime > pollingInterval) {
            lastUpdateNormalViewTime = System.currentTimeMillis()
            val intent = Intent(this, ForegroundNormalUpdateService::class.java)
            ContextCompat.startForegroundService(this, intent)
        }
        if (!TextUtils.isEmpty(todayForecastTime) &&
            isForecastTime(todayForecastTime, lastTodayForecastTime)
        ) {
            lastTodayForecastTime = System.currentTimeMillis()
            val intent = Intent(this, ForegroundTodayForecastUpdateService::class.java)
            ContextCompat.startForegroundService(this, intent)
        }
        if (!TextUtils.isEmpty(tomorrowForecastTime) &&
            isForecastTime(tomorrowForecastTime, lastTomorrowForecastTime)
        ) {
            lastTomorrowForecastTime = System.currentTimeMillis()
            val intent = Intent(this, ForegroundTomorrowForecastUpdateService::class.java)
            ContextCompat.startForegroundService(this, intent)
        }
    }

    private fun registerTimeTickReceiver() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_TIME_TICK)
        filter.addAction(Intent.ACTION_TIME_CHANGED)
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED)
        receiver = TimeTickReceiver()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(receiver, filter)
        }
    }

    private fun unregisterTimeTickReceiver() {
        if (receiver != null) {
            unregisterReceiver(receiver)
            receiver = null
        }
    }

    private fun startForegroundNotification() {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(
                GeometricWeather.NOTIFICATION_ID_RUNNING_IN_BACKGROUND,
                getForegroundNotification(this, true)
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            startForeground(
                GeometricWeather.NOTIFICATION_ID_RUNNING_IN_BACKGROUND,
                getForegroundNotification(this, false)
            )
            startService(Intent(this, FakeForegroundService::class.java))
        } else {
            startForeground(
                GeometricWeather.NOTIFICATION_ID_RUNNING_IN_BACKGROUND,
                getForegroundNotification(this, true)
            )
            startService(Intent(this, FakeForegroundService::class.java))
        }
    }

    private val pollingInterval: Long
        get() = (pollingRate * 1000 * 60 * 60).toLong()

    companion object {
        private var pollingRate = 0f
        private var lastUpdateNormalViewTime: Long = 0
        private var lastTodayForecastTime: Long = 0
        private var lastTomorrowForecastTime: Long = 0
        private var todayForecastTime: String? = null
        private var tomorrowForecastTime: String? = null

        const val KEY_CONFIG_CHANGED = "config_changed"
        const val KEY_POLLING_FAILED = "polling_failed"
        const val KEY_POLLING_RATE = "polling_rate"
        const val KEY_TODAY_FORECAST_TIME = "today_forecast_time"
        const val KEY_TOMORROW_FORECAST_TIME = "tomorrow_forecast_time"

        @JvmStatic
        fun getForegroundNotification(context: Context, setIcon: Boolean): Notification {
            return NotificationCompat.Builder(context, GeometricWeather.NOTIFICATION_CHANNEL_ID_BACKGROUND)
                .setSmallIcon(if (setIcon) R.drawable.ic_running_in_background else 0)
                .setContentTitle(context.getString(R.string.geometric_weather))
                .setContentText(context.getString(R.string.feedback_running_in_background))
                .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAutoCancel(true)
                .build()
        }

        private fun isForecastTime(time: String?, lastForecastTime: Long): Boolean {
            if (time == null) {
                return false
            }
            val currentTime = System.currentTimeMillis()

            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = Integer.parseInt(time.split(":".toRegex()).toTypedArray()[0])
            calendar[Calendar.MINUTE] = Integer.parseInt(time.split(":".toRegex()).toTypedArray()[1])
            val configTime = calendar.timeInMillis

            return currentTime >= configTime && configTime > lastForecastTime
        }
    }
}
