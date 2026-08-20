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
    @JvmField val Region: Region? = null,
    @JvmField val Country: Country? = null,
    @JvmField val AdministrativeArea: AdministrativeArea? = null,
    @JvmField val TimeZone: TimeZone? = null,
    @JvmField val GeoPosition: GeoPosition? = null,
    @JvmField val IsAlias: Boolean = false,
    @JvmField val SupplementalAdminAreas: List<JsonElement>? = null,
    @JvmField val DataSets: List<String>? = null
) {
    @Serializable
    class Region(
        @JvmField val ID: String? = null,
        @JvmField val LocalizedName: String? = null,
        @JvmField val EnglishName: String? = null
    )

    @Serializable
    class Country(
        @JvmField val ID: String? = null,
        @JvmField val LocalizedName: String? = null,
        @JvmField val EnglishName: String? = null
    )

    @Serializable
    class AdministrativeArea(
        @JvmField val ID: String? = null,
        @JvmField val LocalizedName: String? = null,
        @JvmField val EnglishName: String? = null,
        @JvmField val Level: Int = 0,
        @JvmField val LocalizedType: String? = null,
        @JvmField val EnglishType: String? = null,
        @JvmField val CountryID: String? = null
    )

    @Serializable
    class TimeZone(
        @JvmField val Code: String? = null,
        @JvmField val Name: String? = null,
        @JvmField val GmtOffset: Double = 0.0,
        @JvmField val IsDaylightSaving: Boolean = false,
        @JvmField val NextOffsetChange: JsonElement? = null
    )

    @Serializable
    class GeoPosition(
        @JvmField val Latitude: Double = 0.0,
        @JvmField val Longitude: Double = 0.0,
        @JvmField val Elevation: Elevation? = null
    ) {
        @Serializable
        class Elevation(
            @JvmField val Metric: Metric? = null,
            @JvmField val Imperial: Imperial? = null
        ) {
            @Serializable
            class Metric(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Imperial(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }
    }
}
