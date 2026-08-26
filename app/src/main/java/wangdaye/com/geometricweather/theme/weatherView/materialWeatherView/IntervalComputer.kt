package wangdaye.com.geometricweather.theme.weatherView.materialWeatherView

class IntervalComputer {

    private var mCurrentTime: Long = 0
    private var mLastTime: Long = 0
    private var mInterval = 0.0

    init {
        reset()
    }

    fun reset() {
        mCurrentTime = -1
        mLastTime = -1
        mInterval = 0.0
    }

    fun invalidate() {
        mCurrentTime = System.currentTimeMillis()
        mInterval = if (mLastTime == -1L) 0.0 else (mCurrentTime - mLastTime).toDouble()
        mLastTime = mCurrentTime
    }

    val interval: Double
        get() = mInterval
}
