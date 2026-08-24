package wangdaye.com.geometricweather.common.basic.models.weather

import java.io.Serializable
import java.util.Date

class History(
    val date: Date,
    val time: Long,
    val daytimeTemperature: Int,
    val nighttimeTemperature: Int
) : Serializable
