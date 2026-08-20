package wangdaye.com.geometricweather.weather.json.accu

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
class AccuLocationResult(
    @JvmField val Version: Int = 0,
    @JvmField val Key: String? = null,
    @JvmField val Type: String? = null,
    @JvmField val Rank: Int = 0,
    @JvmField val LocalizedName: String? = null,
    @JvmField val EnglishName: String? = null,
    @JvmField val PrimaryPostalCode: String? = null,
    @JvmField val Region: RegionBean? = null,
    @JvmField val Country: CountryBean? = null,
    @JvmField val AdministrativeArea: AdministrativeAreaBean? = null,
    @JvmField val TimeZone: TimeZoneBean? = null,
    @JvmField val GeoPosition: GeoPositionBean? = null,
    @JvmField val IsAlias: Boolean = false,
    @JvmField val SupplementalAdminAreas: List<JsonElement>? = null,
    @JvmField val DataSets: List<String>? = null
) {
    @Serializable
    class RegionBean(
        @JvmField val ID: String? = null,
        @JvmField val LocalizedName: String? = null,
        @JvmField val EnglishName: String? = null
    )

    @Serializable
    class CountryBean(
        @JvmField val ID: String? = null,
        @JvmField val LocalizedName: String? = null,
        @JvmField val EnglishName: String? = null
    )

    @Serializable
    class AdministrativeAreaBean(
        @JvmField val ID: String? = null,
        @JvmField val LocalizedName: String? = null,
        @JvmField val EnglishName: String? = null,
        @JvmField val Level: Int = 0,
        @JvmField val LocalizedType: String? = null,
        @JvmField val EnglishType: String? = null,
        @JvmField val CountryID: String? = null
    )

    @Serializable
    class TimeZoneBean(
        @JvmField val Code: String? = null,
        @JvmField val Name: String? = null,
        @JvmField val GmtOffset: Double = 0.0,
        @JvmField val IsDaylightSaving: Boolean = false,
        @JvmField val NextOffsetChange: JsonElement? = null
    )

    @Serializable
    class GeoPositionBean(
        @JvmField val Latitude: Double = 0.0,
        @JvmField val Longitude: Double = 0.0,
        @JvmField val Elevation: ElevationBean? = null
    ) {
        @Serializable
        class ElevationBean(
            @JvmField val Metric: MetricBean? = null,
            @JvmField val Imperial: ImperialBean? = null
        ) {
            @Serializable
            class MetricBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class ImperialBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }
    }
}
