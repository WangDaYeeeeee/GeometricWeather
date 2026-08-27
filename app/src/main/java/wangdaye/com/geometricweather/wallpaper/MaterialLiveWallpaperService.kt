package wangdaye.com.geometricweather.wallpaper

import android.app.WallpaperColors
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import android.service.wallpaper.WallpaperService
import android.text.TextUtils
import android.view.OrientationEventListener
import android.view.SurfaceHolder
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.annotation.Size
import androidx.core.content.res.ResourcesCompat
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.common.utils.helpers.AsyncHelper
import wangdaye.com.geometricweather.db.DatabaseHelper
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.weatherView.WeatherView
import wangdaye.com.geometricweather.theme.weatherView.WeatherView.WeatherKindRule
import wangdaye.com.geometricweather.theme.weatherView.WeatherViewController
import wangdaye.com.geometricweather.theme.weatherView.materialWeatherView.DelayRotateController
import wangdaye.com.geometricweather.theme.weatherView.materialWeatherView.IntervalComputer
import wangdaye.com.geometricweather.theme.weatherView.materialWeatherView.MaterialWeatherView
import wangdaye.com.geometricweather.theme.weatherView.materialWeatherView.WeatherImplementorFactory
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class MaterialLiveWallpaperService : WallpaperService() {

    private enum class DeviceOrientation {
        TOP, LEFT, BOTTOM, RIGHT
    }

    override fun onCreateEngine(): Engine {
        return WeatherEngine()
    }

    private inner class WeatherEngine : Engine() {

        private lateinit var mHolder: SurfaceHolder
        private var mIntervalComputer: IntervalComputer? = null
        private var mRotators: Array<MaterialWeatherView.RotateController>? = null

        private var mImplementor: MaterialWeatherView.WeatherAnimationImplementor? = null
        private var mBackground: Drawable? = null

        private var mOpenGravitySensor = false
        private var mSensorManager: SensorManager? = null
        private var mGravitySensor: Sensor? = null

        @Size(2)
        private lateinit var mSizes: IntArray
        @Size(2)
        private lateinit var mAdaptiveSize: IntArray
        private var mRotation2D = 0f
        private var mRotation3D = 0f

        @WeatherKindRule
        private var mWeatherKind = 0
        private var mDaytime = false

        private var mVisible = false

        private var mDeviceOrientation: DeviceOrientation = DeviceOrientation.TOP

        private var mIntervalController: AsyncHelper.Controller? = null
        private lateinit var mHandlerThread: HandlerThread
        private lateinit var mHandler: Handler
        private val mDrawableRunnable = Runnable {
            val intervalComputer = mIntervalComputer
            val implementor = mImplementor
            val background = mBackground
            val rotators = mRotators
            if (intervalComputer == null || implementor == null || background == null || rotators == null) {
                return@Runnable
            }

            intervalComputer.invalidate()

            rotators[0].updateRotation(mRotation2D.toDouble(), intervalComputer.interval)
            rotators[1].updateRotation(mRotation3D.toDouble(), intervalComputer.interval)

            try {
                val canvas = mHolder.lockCanvas()
                if (canvas != null) {
                    if (mSizes[0] != canvas.width || mSizes[1] != canvas.height) {
                        mSizes[0] = canvas.width
                        mSizes[1] = canvas.height

                        mAdaptiveSize[0] = DisplayUtils.getTabletListAdaptiveWidth(
                            applicationContext,
                            mSizes[0]
                        )
                        mAdaptiveSize[1] = mSizes[1]

                        background.setBounds(0, 0, mSizes[0], mSizes[1])
                    }

                    background.draw(canvas)

                    canvas.save()
                    canvas.translate(
                        (mSizes[0] - mAdaptiveSize[0]) / 2f,
                        (mSizes[1] - mAdaptiveSize[1]) / 2f
                    )
                    implementor.updateData(
                        mAdaptiveSize, intervalComputer.interval.toLong(),
                        rotators[0].rotation.toFloat(), rotators[1].rotation.toFloat()
                    )
                    implementor.draw(
                        mAdaptiveSize,
                        canvas,
                        0f,
                        rotators[0].rotation.toFloat(),
                        rotators[1].rotation.toFloat()
                    )
                    canvas.restore()
                    mHolder.unlockCanvasAndPost(canvas)
                }
            } catch (ignore: Throwable) {
                // do nothing.
            }
        }

        private val mGravityListener: SensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(ev: SensorEvent) {
                if (mOpenGravitySensor) {
                    val aX = ev.values[0]
                    val aY = ev.values[1]
                    val aZ = ev.values[2]
                    val g2D = sqrt((aX * aX + aY * aY).toDouble())
                    val g3D = sqrt((aX * aX + aY * aY + aZ * aZ).toDouble())
                    val cos2D = max(min(1.0, aY / g2D), -1.0)
                    val cos3D = max(min(1.0, g2D * (if (aY >= 0) 1 else -1) / g3D), -1.0)
                    mRotation2D = Math.toDegrees(acos(cos2D)).toFloat() * if (aX >= 0) 1 else -1
                    mRotation3D = Math.toDegrees(acos(cos3D)).toFloat() * if (aZ >= 0) 1 else -1

                    when (mDeviceOrientation) {
                        DeviceOrientation.TOP -> {}
                        DeviceOrientation.LEFT -> mRotation2D -= 90
                        DeviceOrientation.RIGHT -> mRotation2D += 90
                        DeviceOrientation.BOTTOM -> {
                            if (mRotation2D > 0) {
                                mRotation2D -= 180
                            } else {
                                mRotation2D += 180
                            }
                        }
                    }

                    if (60 < abs(mRotation3D) && abs(mRotation3D) < 120) {
                        mRotation2D *= (abs(abs(mRotation3D) - 90) / 30.0).toFloat()
                    }
                } else {
                    mRotation2D = 0f
                    mRotation3D = 0f
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {
                // do nothing.
            }
        }

        private val mOrientationListener = object : OrientationEventListener(applicationContext) {
            override fun onOrientationChanged(orientation: Int) {
                mDeviceOrientation = getDeviceOrientation(orientation)
            }

            private fun getDeviceOrientation(orientation: Int): DeviceOrientation {
                return if (DisplayUtils.isLandscape(applicationContext)) {
                    if (0 < orientation && orientation < 180) {
                        DeviceOrientation.RIGHT
                    } else {
                        DeviceOrientation.LEFT
                    }
                } else {
                    if (270 < orientation || orientation < 90) {
                        DeviceOrientation.TOP
                    } else {
                        DeviceOrientation.BOTTOM
                    }
                }
            }
        }

        private fun setWeather(@WeatherKindRule weatherKind: Int, daytime: Boolean) {
            mWeatherKind = weatherKind
            mDaytime = daytime
        }

        private fun setWeatherImplementor() {
            mImplementor = WeatherImplementorFactory.getWeatherImplementor(
                mWeatherKind,
                mDaytime,
                mAdaptiveSize
            )
            mRotators = arrayOf(
                DelayRotateController(mRotation2D.toDouble()),
                DelayRotateController(mRotation3D.toDouble())
            )

            mBackground = ResourcesCompat.getDrawable(
                resources,
                WeatherImplementorFactory.getBackgroundId(mWeatherKind, mDaytime),
                null
            )
            mBackground?.let { background ->
                background.setBounds(0, 0, mSizes[0], mSizes[1])
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    notifyColorsChanged()
                }
            }
        }

        private fun setIntervalComputer() {
            if (mIntervalComputer == null) {
                mIntervalComputer = IntervalComputer()
            } else {
                mIntervalComputer!!.reset()
            }
        }

        private fun setOpenGravitySensor(openGravitySensor: Boolean) {
            mOpenGravitySensor = openGravitySensor
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            mDeviceOrientation = DeviceOrientation.TOP

            mHandlerThread = HandlerThread(
                System.currentTimeMillis().toString(),
                Process.THREAD_PRIORITY_FOREGROUND
            )
            mHandlerThread.start()
            mHandler = Handler(mHandlerThread.looper)

            mSizes = intArrayOf(0, 0)
            mAdaptiveSize = intArrayOf(0, 0)

            mHolder = surfaceHolder
            mHolder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {}

                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                    mSizes[0] = width
                    mSizes[1] = height

                    mAdaptiveSize[0] = DisplayUtils.getTabletListAdaptiveWidth(
                        applicationContext,
                        mSizes[0]
                    )
                    mAdaptiveSize[1] = height

                    setWeatherImplementor()
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {}
            })
            mHolder.setFormat(PixelFormat.RGBA_8888)

            mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?
            if (mSensorManager != null) {
                mOpenGravitySensor = true
                mGravitySensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_GRAVITY)
            }

            mVisible = false
            setWeather(WeatherView.WEATHER_KING_NULL, true)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (mVisible != visible) {
                mVisible = visible
                if (visible) {
                    mRotation2D = 0f
                    mRotation3D = 0f
                    mSensorManager?.registerListener(
                        mGravityListener,
                        mGravitySensor,
                        SensorManager.SENSOR_DELAY_FASTEST
                    )
                    if (mOrientationListener.canDetectOrientation()) {
                        mOrientationListener.enable()
                    }

                    var location = DatabaseHelper
                        .getInstance(this@MaterialLiveWallpaperService)
                        .readLocationList()[0]
                    location = Location.copy(
                        location,
                        DatabaseHelper
                            .getInstance(this@MaterialLiveWallpaperService)
                            .readWeather(location)
                    )

                    val configManager = LiveWallpaperConfigManager.getInstance(
                        this@MaterialLiveWallpaperService
                    )
                    var weatherKind = configManager.weatherKind
                    if (weatherKind == "auto") {
                        weatherKind = if (location.weather != null) {
                            location.weather!!.current.weatherCode.id
                        } else {
                            ""
                        }
                    }
                    val dayNightType = configManager.dayNightType
                    var daytime = location.isDaylight
                    when (dayNightType) {
                        "day" -> daytime = true
                        "night" -> daytime = false
                    }

                    if (!TextUtils.isEmpty(weatherKind)) {
                        setWeather(
                            WeatherViewController.getWeatherKind(
                                WeatherCode.getInstance(weatherKind)
                            ),
                            daytime
                        )
                    }
                    setWeatherImplementor()
                    setIntervalComputer()
                    setOpenGravitySensor(
                        SettingsManager.getInstance(applicationContext).isGravitySensorEnabled
                    )

                    var screenRefreshRate: Float
                    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
                    screenRefreshRate = windowManager?.defaultDisplay?.refreshRate ?: 60f
                    if (screenRefreshRate < 60) {
                        screenRefreshRate = 60f
                    }
                    mIntervalController = AsyncHelper.intervalRunOnUI(
                        { mHandler.post(mDrawableRunnable) },
                        (1000.0 / screenRefreshRate).toLong(),
                        0
                    )
                } else {
                    mIntervalController?.cancel()
                    mIntervalController = null
                    mHandler.removeCallbacksAndMessages(null)
                    mSensorManager?.unregisterListener(mGravityListener, mGravitySensor)
                    mOrientationListener.disable()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O_MR1)
        override fun onComputeColors(): WallpaperColors? {
            return mBackground?.let { WallpaperColors.fromDrawable(it) }
        }

        override fun onDestroy() {
            onVisibilityChanged(false)
            mHandlerThread.quit()
        }
    }
}
