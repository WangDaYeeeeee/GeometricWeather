package wangdaye.com.geometricweather.weather.apis

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import wangdaye.com.geometricweather.weather.json.accu.AccuAlertResult
import wangdaye.com.geometricweather.weather.json.accu.AccuAqiResult
import wangdaye.com.geometricweather.weather.json.accu.AccuCurrentResult
import wangdaye.com.geometricweather.weather.json.accu.AccuDailyResult
import wangdaye.com.geometricweather.weather.json.accu.AccuHourlyResult
import wangdaye.com.geometricweather.weather.json.accu.AccuLocationResult
import wangdaye.com.geometricweather.weather.json.accu.AccuMinuteResult

interface AccuWeatherApi {

    @GET("locations/v1/cities/translate.json")
    suspend fun getWeatherLocation(
        @Query("alias") alias: String,
        @Query("apikey") apikey: String,
        @Query("q") q: String,
        @Query("language") language: String
    ): List<AccuLocationResult>

    @GET("locations/v1/cities/geoposition/search.json")
    suspend fun getWeatherLocationByGeoPosition(
        @Query("alias") alias: String,
        @Query("apikey") apikey: String,
        @Query("q") q: String,
        @Query("language") language: String
    ): AccuLocationResult

    @GET("currentconditions/v1/{city_key}.json")
    suspend fun getCurrent(
        @Path("city_key") cityKey: String,
        @Query("apikey") apikey: String,
        @Query("language") language: String,
        @Query("details") details: Boolean
    ): List<AccuCurrentResult>

    @GET("forecasts/v1/daily/15day/{city_key}.json")
    suspend fun getDaily(
        @Path("city_key") cityKey: String,
        @Query("apikey") apikey: String,
        @Query("language") language: String,
        @Query("metric") metric: Boolean,
        @Query("details") details: Boolean
    ): AccuDailyResult

    @GET("forecasts/v1/hourly/24hour/{city_key}.json")
    suspend fun getHourly(
        @Path("city_key") cityKey: String,
        @Query("apikey") apikey: String,
        @Query("language") language: String,
        @Query("metric") metric: Boolean,
        @Query("details") details: Boolean
    ): List<AccuHourlyResult>

    @GET("forecasts/v1/minute/1minute.json")
    suspend fun getMinutely(
        @Query("apikey") apikey: String,
        @Query("language") language: String,
        @Query("details") details: Boolean,
        @Query("q") q: String
    ): AccuMinuteResult

    @GET("airquality/v1/observations/{city_key}.json")
    suspend fun getAirQuality(
        @Path("city_key") cityKey: String,
        @Query("apikey") apikey: String
    ): AccuAqiResult

    @GET("alerts/v1/{city_key}.json")
    suspend fun getAlert(
        @Path("city_key") cityKey: String,
        @Query("apikey") apikey: String,
        @Query("language") language: String,
        @Query("details") details: Boolean
    ): List<AccuAlertResult>
}
