package wangdaye.com.geometricweather.common.utils.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.ui.activities.AlertActivity
import wangdaye.com.geometricweather.common.ui.activities.AllergenActivity
import wangdaye.com.geometricweather.daily.DailyWeatherActivity
import wangdaye.com.geometricweather.main.MainActivity

fun IntentHelper.startMainActivity(context: Context) {
    context.startActivity(
        Intent(MainActivity.ACTION_MAIN)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    )
}

fun IntentHelper.startMainActivityForManagement(activity: Activity) {
    activity.startActivity(
        Intent(MainActivity.ACTION_MANAGEMENT)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    )
}

fun IntentHelper.buildMainActivityIntent(location: Location?): Intent {
    val formattedId = location?.formattedId
    return Intent(MainActivity.ACTION_MAIN)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        .putExtra(MainActivity.KEY_MAIN_ACTIVITY_LOCATION_FORMATTED_ID, formattedId)
}

fun IntentHelper.buildMainActivityShowAlertsIntent(location: Location?): Intent {
    val formattedId = location?.formattedId
    return Intent(MainActivity.ACTION_SHOW_ALERTS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        .putExtra(MainActivity.KEY_MAIN_ACTIVITY_LOCATION_FORMATTED_ID, formattedId)
}

fun IntentHelper.buildMainActivityShowDailyForecastIntent(location: Location?, index: Int): Intent {
    val formattedId = location?.formattedId
    return Intent(MainActivity.ACTION_SHOW_DAILY_FORECAST)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        .putExtra(MainActivity.KEY_MAIN_ACTIVITY_LOCATION_FORMATTED_ID, formattedId)
        .putExtra(MainActivity.KEY_DAILY_INDEX, index)
}

fun IntentHelper.startDailyWeatherActivity(activity: Activity, formattedId: String?, index: Int) {
    val intent = Intent(activity, DailyWeatherActivity::class.java)
    intent.putExtra(DailyWeatherActivity.KEY_FORMATTED_LOCATION_ID, formattedId)
    intent.putExtra(DailyWeatherActivity.KEY_CURRENT_DAILY_INDEX, index)
    activity.startActivity(intent)
}

fun IntentHelper.startAlertActivity(activity: Activity, formattedId: String?) {
    val intent = Intent(activity, AlertActivity::class.java)
    intent.putExtra(AlertActivity.KEY_FORMATTED_ID, formattedId)
    activity.startActivity(intent)
}

fun IntentHelper.startAllergenActivity(activity: Activity, location: Location) {
    val intent = Intent(activity, AllergenActivity::class.java)
    intent.putExtra(
        AllergenActivity.KEY_ALLERGEN_ACTIVITY_LOCATION_FORMATTED_ID,
        location.formattedId
    )
    activity.startActivity(intent)
}
