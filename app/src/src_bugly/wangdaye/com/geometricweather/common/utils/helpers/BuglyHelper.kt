package wangdaye.com.geometricweather.common.utils.helpers

import android.content.Context
import com.tencent.bugly.crashreport.CrashReport

object BuglyHelper {

    @JvmStatic
    fun init(context: Context) {
        CrashReport.initCrashReport(context.applicationContext, "148f1437d5", false)
    }

    @JvmStatic
    fun report(e: Exception) {
        CrashReport.postCatchedException(e)
    }
}
