package wangdaye.com.geometricweather.common.snackbar

import android.os.Handler
import android.os.Looper
import android.os.Message

internal class SnackbarManager private constructor() {

    companion object {
        private const val MSG_TIMEOUT = 0
        private const val SHORT_DURATION_MS = 1500
        private const val LONG_DURATION_MS = 3000

        @Volatile
        private var sInstance: SnackbarManager? = null

        @JvmStatic
        fun getInstance(): SnackbarManager {
            var instance = sInstance
            if (instance == null) {
                instance = SnackbarManager()
                sInstance = instance
            }
            return instance
        }
    }

    interface Callback {
        fun show()
        fun dismiss(event: Int)
    }

    private val mLock = Any()
    private val mHandler: Handler = Handler(Looper.getMainLooper()) { message ->
        when (message.what) {
            MSG_TIMEOUT -> {
                handleTimeout(message.obj as SnackbarRecord)
                true
            }
            else -> false
        }
    }

    private var mCurrentRecord: SnackbarRecord? = null
    private var mNextRecord: SnackbarRecord? = null

    fun show(duration: Int, callback: Callback) {
        synchronized(mLock) {
            if (isCurrentSnackbar(callback)) {
                mCurrentRecord!!.mDuration = duration
                mHandler.removeCallbacksAndMessages(mCurrentRecord)
                scheduleTimeoutLocked(mCurrentRecord!!)
                return
            } else if (isNextSnackbar(callback)) {
                mNextRecord!!.mDuration = duration
            } else {
                mNextRecord = SnackbarRecord(duration, callback)
            }

            if (mCurrentRecord != null && cancelSnackbarLocked(
                    mCurrentRecord!!,
                    Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE
                )
            ) {
                return
            } else {
                mCurrentRecord = null
                showNextSnackbarLocked()
            }
        }
    }

    fun dismiss(callback: Callback, event: Int) {
        synchronized(mLock) {
            if (isCurrentSnackbar(callback)) {
                cancelSnackbarLocked(mCurrentRecord!!, event)
            } else if (isNextSnackbar(callback)) {
                cancelSnackbarLocked(mNextRecord!!, event)
            }
        }
    }

    fun onDismissed(callback: Callback) {
        synchronized(mLock) {
            if (isCurrentSnackbar(callback)) {
                mCurrentRecord = null
                if (mNextRecord != null) {
                    showNextSnackbarLocked()
                }
            }
        }
    }

    fun onShown(callback: Callback) {
        synchronized(mLock) {
            if (isCurrentSnackbar(callback)) {
                scheduleTimeoutLocked(mCurrentRecord!!)
            }
        }
    }

    fun cancelTimeout(callback: Callback) {
        synchronized(mLock) {
            if (isCurrentSnackbar(callback)) {
                mHandler.removeCallbacksAndMessages(mCurrentRecord)
            }
        }
    }

    fun restoreTimeout(callback: Callback) {
        synchronized(mLock) {
            if (isCurrentSnackbar(callback)) {
                scheduleTimeoutLocked(mCurrentRecord!!)
            }
        }
    }

    fun isCurrent(callback: Callback): Boolean {
        synchronized(mLock) {
            return isCurrentSnackbar(callback)
        }
    }

    fun isCurrentOrNext(callback: Callback): Boolean {
        synchronized(mLock) {
            return isCurrentSnackbar(callback) || isNextSnackbar(callback)
        }
    }

    private class SnackbarRecord(
        var mDuration: Int,
        private val mCallback: Callback
    ) {
        fun isSnackbar(callback: Callback?): Boolean {
            return callback != null && mCallback === callback
        }

        fun callback(): Callback = mCallback
    }

    private fun showNextSnackbarLocked() {
        if (mNextRecord != null) {
            mCurrentRecord = mNextRecord
            mNextRecord = null

            val callback = mCurrentRecord!!.callback()
            callback.show()
        }
    }

    private fun cancelSnackbarLocked(record: SnackbarRecord, event: Int): Boolean {
        val callback = record.callback()
        callback.dismiss(event)
        return true
    }

    private fun isCurrentSnackbar(callback: Callback): Boolean {
        return mCurrentRecord != null && mCurrentRecord!!.isSnackbar(callback)
    }

    private fun isNextSnackbar(callback: Callback): Boolean {
        return mNextRecord != null && mNextRecord!!.isSnackbar(callback)
    }

    private fun scheduleTimeoutLocked(r: SnackbarRecord) {
        if (r.mDuration == Snackbar.LENGTH_INDEFINITE) {
            return
        }

        var durationMs = LONG_DURATION_MS
        if (r.mDuration > 0) {
            durationMs = r.mDuration
        } else if (r.mDuration == Snackbar.LENGTH_SHORT) {
            durationMs = SHORT_DURATION_MS
        }
        mHandler.removeCallbacksAndMessages(r)
        mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_TIMEOUT, r), durationMs.toLong())
    }

    private fun handleTimeout(record: SnackbarRecord) {
        synchronized(mLock) {
            if (mCurrentRecord === record || mNextRecord === record) {
                cancelSnackbarLocked(record, Snackbar.Callback.DISMISS_EVENT_TIMEOUT)
            }
        }
    }
}
