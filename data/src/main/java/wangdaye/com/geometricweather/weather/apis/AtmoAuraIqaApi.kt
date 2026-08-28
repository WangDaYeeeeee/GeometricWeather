package wangdaye.com.geometricweather.weather.apis

import retrofit2.http.GET
import retrofit2.http.Query
import wangdaye.com.geometricweather.weather.json.atmoaura.AtmoAuraQAResult

interface AtmoAuraIqaApi {

    @GET("air2go/full_request")
    suspend fun getQAFull(
        @Query("api_token") apiToken: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String
    ): AtmoAuraQAResult
}
