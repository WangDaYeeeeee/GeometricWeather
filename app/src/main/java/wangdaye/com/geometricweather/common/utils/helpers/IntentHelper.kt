package wangdaye.com.geometricweather.common.utils.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.background.polling.services.basic.AwakeForegroundUpdateService
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.ui.activities.AlertActivity
import wangdaye.com.geometricweather.common.ui.activities.AllergenActivity
import wangdaye.com.geometricweather.daily.DailyWeatherActivity
import wangdaye.com.geometricweather.main.MainActivity
import wangdaye.com.geometricweather.search.SearchActivity
import wangdaye.com.geometricweather.settings.activities.AboutActivity
import wangdaye.com.geometricweather.settings.activities.CardDisplayManageActivity
import wangdaye.com.geometricweather.settings.activities.DailyTrendDisplayManageActivity
import wangdaye.com.geometricweather.settings.activities.HourlyTrendDisplayManageActivity
import wangdaye.com.geometricweather.settings.activities.PreviewIconActivity
import wangdaye.com.geometricweather.settings.activities.SelectProviderActivity
import wangdaye.com.geometricweather.settings.activities.SettingsActivity
import wangdaye.com.geometricweather.wallpaper.MaterialLiveWallpaperService

/**
 * Intent helper.
 */
object IntentHelper {

    @JvmStatic
    fun startMainActivity(context: Context) {
        context.startActivity(
            Intent(MainActivity.ACTION_MAIN)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    @JvmStatic
    fun startMainActivityForManagement(activity: Activity) {
        activity.startActivity(
            Intent(MainActivity.ACTION_MANAGEMENT)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    @JvmStatic
    fun buildMainActivityIntent(location: Location?): Intent {
        val formattedId = location?.formattedId
        return Intent(MainActivity.ACTION_MAIN)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .putExtra(MainActivity.KEY_MAIN_ACTIVITY_LOCATION_FORMATTED_ID, formattedId)
    }

    @JvmStatic
    fun buildMainActivityShowAlertsIntent(location: Location?): Intent {
        val formattedId = location?.formattedId
        return Intent(MainActivity.ACTION_SHOW_ALERTS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .putExtra(MainActivity.KEY_MAIN_ACTIVITY_LOCATION_FORMATTED_ID, formattedId)
    }

    @JvmStatic
    fun buildMainActivityShowDailyForecastIntent(location: Location?, index: Int): Intent {
        val formattedId = location?.formattedId
        return Intent(MainActivity.ACTION_SHOW_DAILY_FORECAST)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .putExtra(MainActivity.KEY_MAIN_ACTIVITY_LOCATION_FORMATTED_ID, formattedId)
            .putExtra(MainActivity.KEY_DAILY_INDEX, index)
    }

    @JvmStatic
    fun buildAwakeUpdateActivityIntent(): Intent {
        return Intent("com.wangdaye.geometricweather.UPDATE")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    @JvmStatic
    fun startDailyWeatherActivity(activity: Activity, formattedId: String?, index: Int) {
        val intent = Intent(activity, DailyWeatherActivity::class.java)
        intent.putExtra(DailyWeatherActivity.KEY_FORMATTED_LOCATION_ID, formattedId)
        intent.putExtra(DailyWeatherActivity.KEY_CURRENT_DAILY_INDEX, index)
        activity.startActivity(intent)
    }

    @JvmStatic
    fun startAlertActivity(activity: Activity, formattedId: String?) {
        val intent = Intent(activity, AlertActivity::class.java)
        intent.putExtra(AlertActivity.KEY_FORMATTED_ID, formattedId)
        activity.startActivity(intent)
    }

    @JvmStatic
    fun startAllergenActivity(activity: Activity, location: Location) {
        val intent = Intent(activity, AllergenActivity::class.java)
        intent.putExtra(
            AllergenActivity.KEY_ALLERGEN_ACTIVITY_LOCATION_FORMATTED_ID,
            location.formattedId
        )
        activity.startActivity(intent)
    }

    @JvmStatic
    fun startSearchActivity(activity: Activity, bar: View) {
        activity.startActivity(
            Intent(activity, SearchActivity::class.java),
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                Pair.create(bar, activity.getString(R.string.transition_activity_search_bar))
            ).toBundle()
        )
    }

    @JvmStatic
    fun startSettingsActivity(activity: Activity) {
        activity.startActivity(Intent(activity, SettingsActivity::class.java))
    }

    @JvmStatic
    fun startCardDisplayManageActivity(activity: Activity) {
        activity.startActivity(Intent(activity, CardDisplayManageActivity::class.java))
    }

    @JvmStatic
    fun startDailyTrendDisplayManageActivity(activity: Activity) {
        activity.startActivity(Intent(activity, DailyTrendDisplayManageActivity::class.java))
    }

    @JvmStatic
    fun startHourlyTrendDisplayManageActivityForResult(activity: Activity) {
        activity.startActivity(Intent(activity, HourlyTrendDisplayManageActivity::class.java))
    }

    @JvmStatic
    fun startSelectProviderActivity(activity: Activity) {
        activity.startActivity(Intent(activity, SelectProviderActivity::class.java))
    }

    @JvmStatic
    fun startPreviewIconActivity(activity: Activity, packageName: String?) {
        activity.startActivity(
            Intent(activity, PreviewIconActivity::class.java).putExtra(
                PreviewIconActivity.KEY_ICON_PREVIEW_ACTIVITY_PACKAGE_NAME,
                packageName
            )
        )
    }

    @JvmStatic
    fun startAboutActivity(activity: Activity) {
        activity.startActivity(Intent(activity, AboutActivity::class.java))
    }

    @JvmStatic
    fun startApplicationDetailsActivity(context: Context) {
        startApplicationDetailsActivity(context, context.packageName)
    }

    @JvmStatic
    fun startApplicationDetailsActivity(context: Context, pkgName: String?) {
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                Uri.fromParts("package", pkgName, null)
            )
        )
    }

    @JvmStatic
    fun startLocationSettingsActivity(context: Context) {
        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    @JvmStatic
    fun startLiveWallpaperActivity(context: Context) {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(
            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
            ComponentName(context, MaterialLiveWallpaperService::class.java)
        )
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent)
        } else {
            SnackbarHelper.showSnackbar(context.getString(R.string.feedback_cannot_start_live_wallpaper_activity))
        }
    }

    @JvmStatic
    fun startAppStoreDetailsActivity(context: Context) {
        startAppStoreDetailsActivity(context, context.packageName)
    }

    @JvmStatic
    fun startAppStoreDetailsActivity(context: Context, packageName: String?) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$packageName")
        )
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent)
        } else {
            SnackbarHelper.showSnackbar("Unavailable AppStore.")
        }
    }

    @JvmStatic
    fun startAppStoreSearchActivity(context: Context, query: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://search?q=$query")
        )
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent)
        } else {
            SnackbarHelper.showSnackbar("Unavailable AppStore.")
        }
    }

    @JvmStatic
    fun startWebViewActivity(context: Context, url: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent)
        } else {
            SnackbarHelper.showSnackbar("Unavailable internet browser.")
        }
    }

    @JvmStatic
    fun startEmailActivity(context: Context, url: String?) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent)
        } else {
            SnackbarHelper.showSnackbar("Unavailable e-mail.")
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("BatteryLife")
    @JvmStatic
    fun startBatteryOptimizationActivity(context: Context) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse("package:" + context.packageName)
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent)
        } else {
            SnackbarHelper.showSnackbar("Unavailable battery optimization activity.")
        }
    }

    @JvmStatic
    fun startAwakeForegroundUpdateService(context: Context) {
        ContextCompat.startForegroundService(context, getAwakeForegroundUpdateServiceIntent(context))
    }

    @JvmStatic
    fun getAwakeForegroundUpdateServiceIntent(context: Context): Intent {
        return Intent(context, AwakeForegroundUpdateService::class.java)
    }

    @SuppressLint("WrongConstant")
    private fun isIntentAvailable(context: Context, intent: Intent): Boolean {
        return context.packageManager
            .queryIntentActivities(intent, PackageManager.GET_ACTIVITIES)
            .size > 0
    }
}
