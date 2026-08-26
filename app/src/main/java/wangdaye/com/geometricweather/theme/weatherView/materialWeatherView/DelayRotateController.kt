package wangdaye.com.geometricweather.theme.weatherView.materialWeatherView

import kotlin.math.abs
import kotlin.math.pow

class DelayRotateController(initRotation: Double) : MaterialWeatherView.RotateController() {

    private var mTargetRotation: Double = getRotationInScope(initRotation)
    private var mCurrentRotation: Double = mTargetRotation
    private var mVelocity = 0.0
    private var mAcceleration = 0.0

    override fun updateRotation(rotation: Double, interval: Double) {
        mTargetRotation = getRotationInScope(rotation)
        if (mTargetRotation == mCurrentRotation) {
            mAcceleration = 0.0
            mVelocity = 0.0
            return
        }
        val d: Double
        if (mVelocity == 0.0 || (mTargetRotation - mCurrentRotation) * mVelocity < 0) {
            mAcceleration = (if (mTargetRotation > mCurrentRotation) 1 else -1) * DEFAULT_ABS_ACCELERATION
            d = mAcceleration * interval.pow(2.0) / 2.0
            mVelocity = mAcceleration * interval
        } else if (abs(mVelocity).pow(2.0) / (2 * DEFAULT_ABS_ACCELERATION)
            < abs(mTargetRotation - mCurrentRotation)
        ) {
            mAcceleration = (if (mTargetRotation > mCurrentRotation) 1 else -1) * DEFAULT_ABS_ACCELERATION
            d = mVelocity * interval + mAcceleration * interval.pow(2.0) / 2.0
            mVelocity += mAcceleration * interval
        } else {
            mAcceleration = (if (mTargetRotation > mCurrentRotation) -1 else 1) *
                mVelocity.pow(2.0) / (2.0 * abs(mTargetRotation - mCurrentRotation))
            d = mVelocity * interval + mAcceleration * interval.pow(2.0) / 2.0
            mVelocity += mAcceleration * interval
        }
        if (abs(d) > abs(mTargetRotation - mCurrentRotation)) {
            mAcceleration = 0.0
            mCurrentRotation = mTargetRotation
            mVelocity = 0.0
        } else {
            mCurrentRotation += d
        }
    }

    override fun getRotation(): Double {
        return mCurrentRotation
    }

    private fun getRotationInScope(rotation: Double): Double {
        var value = rotation % 180
        return if (abs(value) <= 90) {
            value
        } else {
            if (value > 0) {
                90 - (value - 90)
            } else {
                -90 - (value + 90)
            }
        }
    }

    companion object {
        private const val DEFAULT_ABS_ACCELERATION = 90.0 / 200.0 / 800.0
    }
}
