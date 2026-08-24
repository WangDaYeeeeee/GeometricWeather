package wangdaye.com.geometricweather.background.receiver.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import wangdaye.com.geometricweather.background.polling.PollingManager

abstract class AbstractWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        PollingManager.resetAllBackgroundTask(context, true)
    }
}
