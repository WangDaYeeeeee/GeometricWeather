package wangdaye.com.geometricweather.common.utils.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

/**
 * Cross-feature intents that do not reference `:app` Activity classes.
 */
object IntentHelper {

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

    @SuppressLint("WrongConstant")
    @JvmStatic
    fun isIntentAvailable(context: Context, intent: Intent): Boolean {
        return context.packageManager
            .queryIntentActivities(intent, PackageManager.GET_ACTIVITIES)
            .size > 0
    }
}
