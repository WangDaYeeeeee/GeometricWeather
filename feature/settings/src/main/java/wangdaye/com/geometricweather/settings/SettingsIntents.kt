package wangdaye.com.geometricweather.common.utils.helpers

import android.app.Activity
import android.content.Intent
import wangdaye.com.geometricweather.common.utils.helpers.IntentHelper
import wangdaye.com.geometricweather.settings.activities.AboutActivity
import wangdaye.com.geometricweather.settings.activities.CardDisplayManageActivity
import wangdaye.com.geometricweather.settings.activities.DailyTrendDisplayManageActivity
import wangdaye.com.geometricweather.settings.activities.HourlyTrendDisplayManageActivity
import wangdaye.com.geometricweather.settings.activities.PreviewIconActivity
import wangdaye.com.geometricweather.settings.activities.SelectProviderActivity
import wangdaye.com.geometricweather.settings.activities.SettingsActivity

fun IntentHelper.startSettingsActivity(activity: Activity) {
    activity.startActivity(Intent(activity, SettingsActivity::class.java))
}

fun IntentHelper.startCardDisplayManageActivity(activity: Activity) {
    activity.startActivity(Intent(activity, CardDisplayManageActivity::class.java))
}

fun IntentHelper.startDailyTrendDisplayManageActivity(activity: Activity) {
    activity.startActivity(Intent(activity, DailyTrendDisplayManageActivity::class.java))
}

fun IntentHelper.startHourlyTrendDisplayManageActivityForResult(activity: Activity) {
    activity.startActivity(Intent(activity, HourlyTrendDisplayManageActivity::class.java))
}

fun IntentHelper.startSelectProviderActivity(activity: Activity) {
    activity.startActivity(Intent(activity, SelectProviderActivity::class.java))
}

fun IntentHelper.startPreviewIconActivity(activity: Activity, packageName: String?) {
    activity.startActivity(
        Intent(activity, PreviewIconActivity::class.java).putExtra(
            PreviewIconActivity.KEY_ICON_PREVIEW_ACTIVITY_PACKAGE_NAME,
            packageName
        )
    )
}

fun IntentHelper.startAboutActivity(activity: Activity) {
    activity.startActivity(Intent(activity, AboutActivity::class.java))
}
