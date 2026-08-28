package wangdaye.com.geometricweather.remoteviews.presenters

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.WallpaperManager
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.text.TextUtils
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.NotificationTextColor
import wangdaye.com.geometricweather.common.basic.models.options.WidgetWeekIconMode
import wangdaye.com.geometricweather.common.basic.models.options.unit.DistanceUnit
import wangdaye.com.geometricweather.common.basic.models.options.unit.PrecipitationUnit
import wangdaye.com.geometricweather.common.basic.models.options.unit.PressureUnit
import wangdaye.com.geometricweather.common.basic.models.options.unit.ProbabilityUnit
import wangdaye.com.geometricweather.common.basic.models.options.unit.RelativeHumidityUnit
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Base
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.common.utils.helpers.IntentHelper
import wangdaye.com.geometricweather.common.utils.helpers.startAboutActivity
import wangdaye.com.geometricweather.common.utils.helpers.startAlertActivity
import wangdaye.com.geometricweather.common.utils.helpers.startAllergenActivity
import wangdaye.com.geometricweather.common.utils.helpers.startAwakeForegroundUpdateService
import wangdaye.com.geometricweather.common.utils.helpers.startCardDisplayManageActivity
import wangdaye.com.geometricweather.common.utils.helpers.startDailyTrendDisplayManageActivity
import wangdaye.com.geometricweather.common.utils.helpers.startDailyWeatherActivity
import wangdaye.com.geometricweather.common.utils.helpers.startHourlyTrendDisplayManageActivityForResult
import wangdaye.com.geometricweather.common.utils.helpers.startLiveWallpaperActivity
import wangdaye.com.geometricweather.common.utils.helpers.startMainActivity
import wangdaye.com.geometricweather.common.utils.helpers.startMainActivityForManagement
import wangdaye.com.geometricweather.common.utils.helpers.startPreviewIconActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSearchActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSelectProviderActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSettingsActivity
import wangdaye.com.geometricweather.common.utils.helpers.buildAwakeUpdateActivityIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityShowAlertsIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityShowDailyForecastIntent
import wangdaye.com.geometricweather.common.utils.helpers.getAwakeForegroundUpdateServiceIntent
import wangdaye.com.geometricweather.common.utils.helpers.LunarHelper
import wangdaye.com.geometricweather.remoteviews.WidgetHelper
import wangdaye.com.geometricweather.settings.ConfigStore
import wangdaye.com.geometricweather.settings.SettingsManager
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

abstract class AbstractRemoteViewsPresenter {

    class WidgetConfig {
        @JvmField var viewStyle: String? = null
        @JvmField var cardStyle: String? = null
        @JvmField var cardAlpha: Int = 0
        @JvmField var textColor: String? = null
        @JvmField var textSize: Int = 0
        @JvmField var hideSubtitle: Boolean = false
        @JvmField var subtitleData: String? = null
        @JvmField var clockFont: String? = null
        @JvmField var hideLunar: Boolean = false
        @JvmField var alignEnd: Boolean = false
    }

    class WidgetColor(
        context: Context,
        cardStyle: String,
        textColor: String
    ) {
        @JvmField val showCard: Boolean = cardStyle != "none"
        @JvmField val cardColor: ColorType = when {
            cardStyle == "auto" -> ColorType.AUTO
            cardStyle == "light" -> ColorType.LIGHT
            else -> ColorType.DARK
        }
        @JvmField @ColorInt val textColor: Int
        @JvmField val darkText: Boolean

        enum class ColorType {
            LIGHT, DARK, AUTO
        }

        init {
            if (showCard) {
                when (cardColor) {
                    ColorType.AUTO -> {
                        this.textColor = Color.TRANSPARENT
                        this.darkText = false
                    }
                    ColorType.LIGHT -> {
                        this.textColor = ContextCompat.getColor(context, R.color.colorTextDark)
                        this.darkText = true
                    }
                    else -> {
                        this.textColor = ContextCompat.getColor(context, R.color.colorTextLight)
                        this.darkText = false
                    }
                }
            } else if (textColor == "dark" || (textColor == "auto" && isLightWallpaper(context))) {
                this.textColor = ContextCompat.getColor(context, R.color.colorTextDark)
                this.darkText = true
            } else {
                this.textColor = ContextCompat.getColor(context, R.color.colorTextLight)
                this.darkText = false
            }
        }

        fun getMinimalIconColor(): NotificationTextColor {
            return if (showCard) {
                when (cardColor) {
                    ColorType.AUTO -> NotificationTextColor.GREY
                    ColorType.LIGHT -> NotificationTextColor.DARK
                    else -> NotificationTextColor.LIGHT
                }
            } else if (darkText) {
                NotificationTextColor.DARK
            } else {
                NotificationTextColor.LIGHT
            }
        }
    }

    companion object {
        private const val SUBTITLE_DAILY_ITEM_LENGTH = 5

        @JvmStatic
        fun getWidgetConfig(context: Context, configStoreName: String): WidgetConfig {
            val widgetConfig = WidgetConfig()
            val configStore = ConfigStore.getInstance(context, configStoreName)
            widgetConfig.viewStyle = configStore.getString(
                context.getString(R.string.key_view_type),
                "rectangle"
            )
            widgetConfig.cardStyle = configStore.getString(
                context.getString(R.string.key_card_style),
                "none"
            )
            widgetConfig.cardAlpha = configStore.getInt(
                context.getString(R.string.key_card_alpha),
                100
            )
            widgetConfig.textColor = configStore.getString(
                context.getString(R.string.key_text_color),
                "light"
            )
            widgetConfig.textSize = configStore.getInt(
                context.getString(R.string.key_text_size),
                100
            )
            widgetConfig.hideSubtitle = configStore.getBoolean(
                context.getString(R.string.key_hide_subtitle),
                false
            )
            widgetConfig.subtitleData = configStore.getString(
                context.getString(R.string.key_subtitle_data),
                "time"
            )
            widgetConfig.clockFont = configStore.getString(
                context.getString(R.string.key_clock_font),
                "light"
            )
            widgetConfig.hideLunar = configStore.getBoolean(
                context.getString(R.string.key_hide_lunar),
                false
            )
            widgetConfig.alignEnd = configStore.getBoolean(
                context.getString(R.string.key_align_end),
                false
            )
            return widgetConfig
        }

        @JvmStatic
        fun isLightWallpaper(context: Context): Boolean {
            return try {
                val manager = WallpaperManager.getInstance(context) ?: return false
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
                val drawable = manager.drawable
                if (drawable !is BitmapDrawable) {
                    return false
                }
                DisplayUtils.isLightColor(
                    DisplayUtils.bitmapToColorInt(drawable.bitmap)
                )
            } catch (ignore: Exception) {
                false
            }
        }

        @JvmStatic
        @DrawableRes
        fun getCardBackgroundId(cardColor: WidgetColor.ColorType): Int {
            return when (cardColor) {
                WidgetColor.ColorType.AUTO -> R.drawable.widget_card_follow_system
                WidgetColor.ColorType.LIGHT -> R.drawable.widget_card_light
                else -> R.drawable.widget_card_dark
            }
        }

        @JvmStatic
        fun isWeekIconDaytime(mode: WidgetWeekIconMode, daytime: Boolean): Boolean {
            return when (mode) {
                WidgetWeekIconMode.DAY -> true
                WidgetWeekIconMode.NIGHT -> false
                else -> daytime
            }
        }

        @SuppressLint("InlinedApi")
        @JvmStatic
        fun getWeatherPendingIntent(
            context: Context,
            location: Location?,
            requestCode: Int
        ): PendingIntent {
            return PendingIntent.getActivity(
                context,
                requestCode,
                IntentHelper.buildMainActivityIntent(location),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        @SuppressLint("InlinedApi")
        @JvmStatic
        fun getDailyForecastPendingIntent(
            context: Context,
            location: Location?,
            index: Int,
            requestCode: Int
        ): PendingIntent {
            return PendingIntent.getActivity(
                context,
                requestCode,
                IntentHelper.buildMainActivityShowDailyForecastIntent(location, index),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        @SuppressLint("InlinedApi")
        @JvmStatic
        fun getAlarmPendingIntent(context: Context, requestCode: Int): PendingIntent {
            return PendingIntent.getActivity(
                context,
                requestCode,
                Intent(AlarmClock.ACTION_SHOW_ALARMS),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        @SuppressLint("InlinedApi")
        @JvmStatic
        fun getCalendarPendingIntent(context: Context, requestCode: Int): PendingIntent {
            val builder = CalendarContract.CONTENT_URI.buildUpon()
            builder.appendPath("time")
            ContentUris.appendId(builder, System.currentTimeMillis())
            return PendingIntent.getActivity(
                context,
                requestCode,
                Intent(Intent.ACTION_VIEW).setData(builder.build()),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        @JvmStatic
        fun drawableToBitmap(drawable: Drawable): Bitmap {
            return DisplayUtils.drawableToBitmap(drawable)
        }

        @SuppressLint("SimpleDateFormat")
        @JvmStatic
        fun getCustomSubtitle(
            context: Context,
            subtitleIn: String?,
            location: Location,
            weather: Weather
        ): String {
            if (TextUtils.isEmpty(subtitleIn)) {
                return ""
            }
            val temperatureUnit = SettingsManager.getInstance(context).temperatureUnit
            val precipitationUnit = SettingsManager.getInstance(context).precipitationUnit
            val pressureUnit = SettingsManager.getInstance(context).pressureUnit
            val distanceUnit = SettingsManager.getInstance(context).distanceUnit

            var subtitle = subtitleIn!!
                .replace("\$cw$", weather.current.weatherText)
                .replace(
                    "\$ct$",
                    weather.current.temperature.getTemperature(context, temperatureUnit) + ""
                ).replace(
                    "\$ctd$",
                    weather.current.temperature.getShortTemperature(context, temperatureUnit) + ""
                ).replace(
                    "\$at$",
                    weather.current.temperature.getRealFeelTemperature(context, temperatureUnit) + ""
                ).replace(
                    "\$atd$",
                    weather.current.temperature.getShortRealFeeTemperature(context, temperatureUnit) + ""
                ).replace(
                    "\$cpb$",
                    ProbabilityUnit.PERCENT.getValueText(
                        context,
                        WidgetHelper.getNonNullValue(
                            weather.current.precipitationProbability.total,
                            0f
                        ).toInt()
                    )
                ).replace(
                    "\$cp$",
                    precipitationUnit.getValueText(
                        context,
                        WidgetHelper.getNonNullValue(
                            weather.current.precipitation.total,
                            0f
                        )
                    )
                ).replace(
                    "\$cwd$",
                    weather.current.wind.level +
                        " (" +
                        weather.current.wind.direction +
                        ")"
                ).replace("\$cuv$", weather.current.uv.getShortUVDescription())
                .replace(
                    "\$ch$",
                    RelativeHumidityUnit.PERCENT.getValueText(
                        context,
                        WidgetHelper.getNonNullValue(
                            weather.current.relativeHumidity,
                            0f
                        ).toInt()
                    )
                ).replace(
                    "\$cps$",
                    pressureUnit.getValueText(
                        context,
                        WidgetHelper.getNonNullValue(
                            weather.current.pressure,
                            0f
                        )
                    )
                ).replace(
                    "\$cv$",
                    distanceUnit.getValueText(
                        context,
                        WidgetHelper.getNonNullValue(
                            weather.current.visibility,
                            0f
                        )
                    )
                ).replace(
                    "\$cdp$",
                    temperatureUnit.getValueText(
                        context,
                        WidgetHelper.getNonNullValue(
                            weather.current.dewPoint,
                            0
                        )
                    )
                ).replace("\$l$", location.getCityName(context))
                .replace("\$lat$", location.latitude.toString())
                .replace("\$lon$", location.longitude.toString())
                .replace("\$ut$", Base.getTime(context, weather.base.updateDate))
                .replace(
                    "\$d$",
                    SimpleDateFormat(context.getString(R.string.date_format_long)).format(Date())
                ).replace(
                    "\$lc$",
                    LunarHelper.getLunarDate(Date())
                ).replace(
                    "\$w$",
                    SimpleDateFormat("EEEE").format(Date())
                ).replace(
                    "\$ws$",
                    SimpleDateFormat("EEE").format(Date())
                ).replace("\$dd$", weather.current.dailyForecast + "")
                .replace("\$hd$", weather.current.hourlyForecast + "")
                .replace("\$enter$", "\n")
            subtitle = replaceAlerts(subtitle, weather)
            subtitle = replaceDaytimeWeatherSubtitle(subtitle, weather)
            subtitle = replaceNighttimeWeatherSubtitle(subtitle, weather)
            subtitle = replaceDaytimeTemperatureSubtitle(context, subtitle, weather, temperatureUnit)
            subtitle = replaceNighttimeTemperatureSubtitle(context, subtitle, weather, temperatureUnit)
            subtitle = replaceDaytimeDegreeTemperatureSubtitle(context, subtitle, weather, temperatureUnit)
            subtitle = replaceNighttimeDegreeTemperatureSubtitle(context, subtitle, weather, temperatureUnit)
            subtitle = replaceDaytimePrecipitationSubtitle(context, subtitle, weather)
            subtitle = replaceNighttimePrecipitationSubtitle(context, subtitle, weather)
            subtitle = replaceDaytimeWindSubtitle(subtitle, weather)
            subtitle = replaceNighttimeWindSubtitle(subtitle, weather)
            subtitle = replaceSunriseSubtitle(context, subtitle, weather, location.timeZone)
            subtitle = replaceSunsetSubtitle(context, subtitle, weather, location.timeZone)
            subtitle = replaceMoonriseSubtitle(context, subtitle, weather, location.timeZone)
            subtitle = replaceMoonsetSubtitle(context, subtitle, weather, location.timeZone)
            subtitle = replaceMoonPhaseSubtitle(context, subtitle, weather)
            return subtitle
        }

        private fun replaceAlerts(subtitle: String, weather: Weather): String {
            val defaultBuilder = StringBuilder()
            val shortBuilder = StringBuilder()
            for (i in weather.alertList.indices) {
                defaultBuilder.append(weather.alertList[i].description)
                    .append(", ")
                    .append(
                        DateFormat.getDateTimeInstance(
                            DateFormat.DEFAULT,
                            DateFormat.SHORT
                        ).format(weather.alertList[i].date)
                    )
                shortBuilder.append(weather.alertList[i].description)
                if (i != weather.alertList.size - 1) {
                    defaultBuilder.append("\n")
                    shortBuilder.append("\n")
                }
            }
            return subtitle.replace("\$al$", defaultBuilder.toString())
                .replace("\$als$", shortBuilder.toString())
        }

        private fun replaceDaytimeWeatherSubtitle(subtitle: String, weather: Weather): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "dw$",
                    weather.dailyForecast[i].day().weatherText
                )
            }
            return result
        }

        private fun replaceNighttimeWeatherSubtitle(subtitle: String, weather: Weather): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "nw$",
                    weather.dailyForecast[i].night().weatherText
                )
            }
            return result
        }

        private fun replaceDaytimeTemperatureSubtitle(
            context: Context,
            subtitle: String,
            weather: Weather,
            unit: TemperatureUnit
        ): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "dt$",
                    weather.dailyForecast[i].day().temperature.getTemperature(context, unit) + ""
                )
            }
            return result
        }

        private fun replaceNighttimeTemperatureSubtitle(
            context: Context,
            subtitle: String,
            weather: Weather,
            unit: TemperatureUnit
        ): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "nt$",
                    weather.dailyForecast[i].night().temperature.getTemperature(context, unit) + ""
                )
            }
            return result
        }

        private fun replaceDaytimeDegreeTemperatureSubtitle(
            context: Context,
            subtitle: String,
            weather: Weather,
            unit: TemperatureUnit
        ): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "dtd$",
                    weather.dailyForecast[i].day().temperature.getShortTemperature(context, unit) + ""
                )
            }
            return result
        }

        private fun replaceNighttimeDegreeTemperatureSubtitle(
            context: Context,
            subtitle: String,
            weather: Weather,
            unit: TemperatureUnit
        ): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "ntd$",
                    weather.dailyForecast[i].night().temperature.getShortTemperature(context, unit) + ""
                )
            }
            return result
        }

        private fun replaceDaytimePrecipitationSubtitle(
            context: Context,
            subtitle: String,
            weather: Weather
        ): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "dp$",
                    ProbabilityUnit.PERCENT.getValueText(
                        context,
                        WidgetHelper.getNonNullValue(
                            weather.dailyForecast[i].day().precipitationProbability.total,
                            0f
                        ).toInt()
                    )
                )
            }
            return result
        }

        private fun replaceNighttimePrecipitationSubtitle(
            context: Context,
            subtitle: String,
            weather: Weather
        ): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "np$",
                    ProbabilityUnit.PERCENT.getValueText(
                        context,
                        WidgetHelper.getNonNullValue(
                            weather.dailyForecast[i].night().precipitationProbability.total,
                            0f
                        ).toInt()
                    )
                )
            }
            return result
        }

        private fun replaceDaytimeWindSubtitle(subtitle: String, weather: Weather): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "dwd$",
                    weather.dailyForecast[i].day().wind.level +
                        " (" +
                        weather.dailyForecast[i].day().wind.direction +
                        ")"
                )
            }
            return result
        }

        private fun replaceNighttimeWindSubtitle(subtitle: String, weather: Weather): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "nwd$",
                    weather.dailyForecast[i].night().wind.level +
                        " (" +
                        weather.dailyForecast[i].night().wind.direction +
                        ")"
                )
            }
            return result
        }

        private fun replaceSunriseSubtitle(
            context: Context,
            subtitle: String,
            weather: Weather,
            timeZone: TimeZone
        ): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "sr$",
                    weather.dailyForecast[i].sun().getRiseTime(context, timeZone) + ""
                )
            }
            return result
        }

        private fun replaceSunsetSubtitle(
            context: Context,
            subtitle: String,
            weather: Weather,
            timeZone: TimeZone
        ): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "ss$",
                    weather.dailyForecast[i].sun().getSetTime(context, timeZone) + ""
                )
            }
            return result
        }

        private fun replaceMoonriseSubtitle(
            context: Context,
            subtitle: String,
            weather: Weather,
            timeZone: TimeZone
        ): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "mr$",
                    weather.dailyForecast[i].moon().getRiseTime(context, timeZone) + ""
                )
            }
            return result
        }

        private fun replaceMoonsetSubtitle(
            context: Context,
            subtitle: String,
            weather: Weather,
            timeZone: TimeZone
        ): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "ms$",
                    weather.dailyForecast[i].moon().getSetTime(context, timeZone) + ""
                )
            }
            return result
        }

        private fun replaceMoonPhaseSubtitle(
            context: Context,
            subtitle: String,
            weather: Weather
        ): String {
            var result = subtitle
            for (i in 0 until SUBTITLE_DAILY_ITEM_LENGTH) {
                result = result.replace(
                    "\$" + i + "mp$",
                    weather.dailyForecast[i].moonPhase.getMoonPhase(context) + ""
                )
            }
            return result
        }
    }
}
