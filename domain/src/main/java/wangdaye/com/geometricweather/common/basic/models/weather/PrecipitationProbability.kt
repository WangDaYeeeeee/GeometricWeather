package wangdaye.com.geometricweather.common.basic.models.weather

import java.io.Serializable
import wangdaye.com.geometricweather.common.basic.models.options.unit.ProbabilityUnit

/**
 * Precipitation duration.
 *
 * default unit : [ProbabilityUnit.PERCENT]
 */
class PrecipitationProbability(
    val total: Float?,
    val thunderstorm: Float?,
    val rain: Float?,
    val snow: Float?,
    val ice: Float?
) : Serializable {

    val isValid: Boolean
        get() = total != null && total > 0
}
