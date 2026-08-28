package wangdaye.com.geometricweather.theme.resource.utils

data class Config(
    @JvmField var hasWeatherIcons: Boolean = true,
    @JvmField var hasWeatherAnimators: Boolean = false,
    @JvmField var hasMinimalIcons: Boolean = true,
    @JvmField var hasShortcutIcons: Boolean = true,
    @JvmField var hasSunMoonDrawables: Boolean = true,
) {
    override fun toString(): String {
        return "config : \n" +
            "hasWeatherIcons = $hasWeatherIcons\n" +
            "hasWeatherAnimators = $hasWeatherAnimators\n" +
            "hasMinimalIcons = $hasMinimalIcons\n" +
            "hasShortcutIcons = $hasShortcutIcons\n" +
            "hasSunMoonDrawables = $hasSunMoonDrawables\n"
    }
}
