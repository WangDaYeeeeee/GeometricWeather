package wangdaye.com.geometricweather.main.adapters.main

import wangdaye.com.geometricweather.common.ui.adapters.TagAdapter

class MainTag(
    private val name: String,
    val type: Type
) : TagAdapter.Tag {

    enum class Type { TEMPERATURE, WIND, PRECIPITATION, AIR_QUALITY, UV_INDEX }

    override fun getName(): String {
        return name
    }
}
