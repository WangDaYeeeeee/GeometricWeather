package wangdaye.com.geometricweather.weather.services

import android.content.Context
import android.text.TextUtils
import androidx.annotation.WorkerThread
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.utils.LanguageUtils

abstract class WeatherService {

    class WeatherResultWrapper(@JvmField val result: Weather?)

    interface RequestWeatherCallback {
        fun requestWeatherSuccess(requestLocation: Location)
        fun requestWeatherFailed(requestLocation: Location)
    }

    interface RequestLocationCallback {
        fun requestLocationSuccess(query: String, locationList: List<Location>)
        fun requestLocationFailed(query: String)
    }

    abstract fun requestWeather(
        context: Context,
        location: Location,
        callback: RequestWeatherCallback
    )

    @WorkerThread
    abstract fun requestLocation(context: Context, query: String): List<Location>

    abstract fun requestLocation(
        context: Context,
        location: Location,
        callback: RequestLocationCallback
    )

    abstract fun cancel()

    protected fun formatLocationString(str: String?): String {
            if (TextUtils.isEmpty(str)) {
                return ""
            }
            val value = str!!

            if (value.endsWith("地区")) {
                return value.substring(0, value.length - 2)
            }
            if (value.endsWith("区")
                && !value.endsWith("新区")
                && !value.endsWith("矿区")
                && !value.endsWith("郊区")
                && !value.endsWith("风景区")
                && !value.endsWith("东区")
                && !value.endsWith("西区")
            ) {
                return value.substring(0, value.length - 1)
            }
            if (value.endsWith("县")
                && value.length != 2
                && !value.endsWith("通化县")
                && !value.endsWith("本溪县")
                && !value.endsWith("辽阳县")
                && !value.endsWith("建平县")
                && !value.endsWith("承德县")
                && !value.endsWith("大同县")
                && !value.endsWith("五台县")
                && !value.endsWith("乌鲁木齐县")
                && !value.endsWith("伊宁县")
                && !value.endsWith("南昌县")
                && !value.endsWith("上饶县")
                && !value.endsWith("吉安县")
                && !value.endsWith("长沙县")
                && !value.endsWith("衡阳县")
                && !value.endsWith("邵阳县")
                && !value.endsWith("宜宾县")
            ) {
                return value.substring(0, value.length - 1)
            }

            if (value.endsWith("市")
                && !value.endsWith("新市")
                && !value.endsWith("沙市")
                && !value.endsWith("津市")
                && !value.endsWith("芒市")
                && !value.endsWith("西市")
                && !value.endsWith("峨眉山市")
            ) {
                return value.substring(0, value.length - 1)
            }
            if (value.endsWith("回族自治州")
                || value.endsWith("藏族自治州")
                || value.endsWith("彝族自治州")
                || value.endsWith("白族自治州")
                || value.endsWith("傣族自治州")
                || value.endsWith("蒙古自治州")
            ) {
                return value.substring(0, value.length - 5)
            }
            if (value.endsWith("朝鲜族自治州")
                || value.endsWith("哈萨克自治州")
                || value.endsWith("傈僳族自治州")
                || value.endsWith("蒙古族自治州")
            ) {
                return value.substring(0, value.length - 6)
            }
            if (value.endsWith("哈萨克族自治州")
                || value.endsWith("苗族侗族自治州")
                || value.endsWith("藏族羌族自治州")
                || value.endsWith("壮族苗族自治州")
                || value.endsWith("柯尔克孜自治州")
            ) {
                return value.substring(0, value.length - 7)
            }
            if (value.endsWith("布依族苗族自治州")
                || value.endsWith("土家族苗族自治州")
                || value.endsWith("蒙古族藏族自治州")
                || value.endsWith("柯尔克孜族自治州")
                || value.endsWith("傣族景颇族自治州")
                || value.endsWith("哈尼族彝族自治州")
            ) {
                return value.substring(0, value.length - 8)
            }
            if (value.endsWith("自治州")) {
                return value.substring(0, value.length - 3)
            }

            if (value.endsWith("省")) {
                return value.substring(0, value.length - 1)
            }
            if (value.endsWith("壮族自治区") || value.endsWith("回族自治区")) {
                return value.substring(0, value.length - 5)
            }
            if (value.endsWith("维吾尔自治区")) {
                return value.substring(0, value.length - 6)
            }
            if (value.endsWith("维吾尔族自治区")) {
                return value.substring(0, value.length - 7)
            }
            if (value.endsWith("自治区")) {
                return value.substring(0, value.length - 3)
            }
            return value
        }

    protected fun convertChinese(text: String?): String? {
        return try {
            LanguageUtils.traditionalToSimplified(text)
        } catch (_: Exception) {
            text
        }
    }
}
