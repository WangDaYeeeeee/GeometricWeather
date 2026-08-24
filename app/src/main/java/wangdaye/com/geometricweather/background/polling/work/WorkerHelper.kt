package wangdaye.com.geometricweather.background.polling.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import wangdaye.com.geometricweather.background.polling.work.worker.NormalUpdateWorker
import wangdaye.com.geometricweather.background.polling.work.worker.TodayForecastUpdateWorker
import wangdaye.com.geometricweather.background.polling.work.worker.TomorrowForecastUpdateWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkerHelper {

    private const val MINUTES_PER_HOUR = 60L
    private const val BACKOFF_DELAY_MINUTES = 15L
    private const val WORK_NAME_NORMAL_VIEW = "NORMAL_VIEW"
    private const val WORK_NAME_TODAY_FORECAST = "TODAY_FORECAST"
    private const val WORK_NAME_TOMORROW_FORECAST = "TOMORROW_FORECAST"

    @JvmStatic
    fun setExpeditedPollingWork(context: Context) {
        val request = OneTimeWorkRequest.Builder(NormalUpdateWorker::class.java)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).setExpedited(
                OutOfQuotaPolicy.DROP_WORK_REQUEST
            ).build()
        WorkManager.getInstance(context).enqueue(request)
    }

    @JvmStatic
    fun setNormalPollingWork(context: Context, pollingRate: Float) {
        val request = PeriodicWorkRequest.Builder(
            NormalUpdateWorker::class.java,
            (pollingRate * MINUTES_PER_HOUR).toLong(),
            TimeUnit.MINUTES
        ).setBackoffCriteria(
            BackoffPolicy.LINEAR,
            BACKOFF_DELAY_MINUTES,
            TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME_NORMAL_VIEW,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    @JvmStatic
    fun cancelNormalPollingWork(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_NORMAL_VIEW)
    }

    @JvmStatic
    fun setTodayForecastUpdateWork(context: Context, todayForecastTime: String, nextDay: Boolean) {
        val request = OneTimeWorkRequest.Builder(TodayForecastUpdateWorker::class.java)
            .setInitialDelay(
                getForecastAlarmDelayInMinutes(todayForecastTime, nextDay),
                TimeUnit.MINUTES
            ).setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME_TODAY_FORECAST,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    @JvmStatic
    fun cancelTodayForecastUpdateWork(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_TODAY_FORECAST)
    }

    @JvmStatic
    fun setTomorrowForecastUpdateWork(context: Context, tomorrowForecastTime: String, nextDay: Boolean) {
        val request = OneTimeWorkRequest.Builder(TomorrowForecastUpdateWorker::class.java)
            .setInitialDelay(
                getForecastAlarmDelayInMinutes(tomorrowForecastTime, nextDay),
                TimeUnit.MINUTES
            ).setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME_TOMORROW_FORECAST,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    @JvmStatic
    fun cancelTomorrowForecastUpdateWork(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_TOMORROW_FORECAST)
    }

    private fun getForecastAlarmDelayInMinutes(time: String, nextDay: Boolean): Long {
        val realTimes = intArrayOf(
            Calendar.getInstance()[Calendar.HOUR_OF_DAY],
            Calendar.getInstance()[Calendar.MINUTE]
        )
        val setTimes = intArrayOf(
            Integer.parseInt(time.split(":".toRegex()).toTypedArray()[0]),
            Integer.parseInt(time.split(":".toRegex()).toTypedArray()[1])
        )
        var delay = (setTimes[0] - realTimes[0]) * 60 + (setTimes[1] - realTimes[1])
        if (delay <= 0 || nextDay) {
            delay += 24 * 60
        }
        return delay.toLong()
    }
}
