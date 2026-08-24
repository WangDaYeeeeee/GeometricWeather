package wangdaye.com.geometricweather.common.utils.helpers

import android.util.Log
import android.view.MotionEvent

object LogHelper {

    private const val DEBUG = true
    private const val TAG = "testing"

    @JvmStatic
    fun log(msg: String) {
        log(TAG, msg)
    }

    @JvmStatic
    fun log(tag: String, msg: String) {
        if (DEBUG) {
            Log.d(tag, msg)
        }
    }

    @JvmStatic
    fun nameAction(action: Int): String {
        return when (action) {
            MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
            MotionEvent.ACTION_POINTER_DOWN -> "ACTION_POINTER_DOWN"
            MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
            MotionEvent.ACTION_POINTER_UP -> "ACTION_POINTER_UP"
            MotionEvent.ACTION_UP -> "ACTION_UP"
            MotionEvent.ACTION_CANCEL -> "ACTION_CANCEL"
            else -> "ACTION_UNKNOWN"
        }
    }
}
