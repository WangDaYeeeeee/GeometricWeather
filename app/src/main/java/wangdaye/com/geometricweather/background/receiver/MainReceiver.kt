package wangdaye.com.geometricweather.background.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import wangdaye.com.geometricweather.background.polling.PollingManager

class MainReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (TextUtils.isEmpty(action)) {
            return
        }
        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_WALLPAPER_CHANGED ->
                PollingManager.resetAllBackgroundTask(context, true)
        }
    }
}
