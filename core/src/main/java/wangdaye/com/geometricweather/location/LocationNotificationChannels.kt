package wangdaye.com.geometricweather.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.core.R

/**
 * Location-request notification ids/channels used by [wangdaye.com.geometricweather.location.services.LocationService].
 * Lives in `:core` so location code in `:data` does not depend on the Application class.
 */
object LocationNotificationChannels {

    const val CHANNEL_ID = "location"
    const val NOTIFICATION_ID = 4

    fun getChannelName(context: Context): String {
        return context.getString(R.string.geometric_weather) +
            " " +
            context.getString(R.string.feedback_request_location)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getChannel(context: Context): NotificationChannel {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getChannelName(context),
            NotificationManager.IMPORTANCE_MIN
        )
        channel.setShowBadge(false)
        channel.lightColor = ContextCompat.getColor(context, R.color.colorPrimary)
        return channel
    }

    fun getNotification(context: Context): Notification {
        return NotificationCompat
            .Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_location)
            .setContentTitle(context.getString(R.string.feedback_request_location))
            .setContentText(context.getString(R.string.feedback_request_location_in_background))
            .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setAutoCancel(true)
            .setProgress(0, 0, true)
            .build()
    }
}
