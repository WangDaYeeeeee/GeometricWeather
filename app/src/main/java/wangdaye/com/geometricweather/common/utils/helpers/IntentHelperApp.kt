package wangdaye.com.geometricweather.common.utils.helpers

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.background.polling.services.basic.AwakeForegroundUpdateService
import wangdaye.com.geometricweather.wallpaper.MaterialLiveWallpaperService

fun IntentHelper.buildAwakeUpdateActivityIntent(): Intent {
    return Intent("com.wangdaye.geometricweather.UPDATE")
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}

fun IntentHelper.startLiveWallpaperActivity(context: Context) {
    val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(
        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
        ComponentName(context, MaterialLiveWallpaperService::class.java)
    )
    if (IntentHelper.isIntentAvailable(context, intent)) {
        context.startActivity(intent)
    } else {
        SnackbarHelper.showSnackbar(
            context.getString(R.string.feedback_cannot_start_live_wallpaper_activity)
        )
    }
}

fun IntentHelper.startAwakeForegroundUpdateService(context: Context) {
    ContextCompat.startForegroundService(context, getAwakeForegroundUpdateServiceIntent(context))
}

fun IntentHelper.getAwakeForegroundUpdateServiceIntent(context: Context): Intent {
    return Intent(context, AwakeForegroundUpdateService::class.java)
}
