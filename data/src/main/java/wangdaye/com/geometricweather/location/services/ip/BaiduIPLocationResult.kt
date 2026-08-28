package wangdaye.com.geometricweather.location.services.ip

import kotlinx.serialization.Serializable

@Serializable
class BaiduIPLocationResult(
    @JvmField val address: String? = null,
    @JvmField val content: ContentBean? = null,
    @JvmField val status: Int = 0
) {
    @Serializable
    class ContentBean(
        @JvmField val address: String? = null,
        @JvmField val address_detail: AddressDetailBean? = null,
        @JvmField val point: PointBean? = null
    ) {
        @Serializable
        class AddressDetailBean(
            @JvmField val city: String? = null,
            @JvmField val city_code: Int = 0,
            @JvmField val district: String? = null,
            @JvmField val province: String? = null,
            @JvmField val street: String? = null,
            @JvmField val street_number: String? = null
        )

        @Serializable
        class PointBean(
            @JvmField val x: String? = null,
            @JvmField val y: String? = null
        )
    }
}
