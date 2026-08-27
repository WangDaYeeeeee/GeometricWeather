package wangdaye.com.geometricweather.common.basic.models.weather

import java.io.Serializable
import wangdaye.com.geometricweather.common.basic.models.options.unit.DurationUnit

/**
 * Precipitation duration.
 *
 * default unit : [DurationUnit.H]
 */
class PrecipitationDuration(
    val total: Float?,
    val thunderstorm: Float?,
    val rain: Float?,
    val snow: Float?,
    val ice: Float?
) : Serializable
