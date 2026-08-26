package wangdaye.com.geometricweather.theme.resource.utils

class Config {
    @JvmField var hasWeatherIcons = true
    @JvmField var hasWeatherAnimators = false
    @JvmField var hasMinimalIcons = true
    @JvmField var hasShortcutIcons = true
    @JvmField var hasSunMoonDrawables = true

    override fun toString(): String {
        return "config : \n" +
            "hasWeatherIcons = $hasWeatherIcons\n" +
            "hasWeatherAnimators = $hasWeatherAnimators\n" +
            "hasMinimalIcons = $hasMinimalIcons\n" +
            "hasShortcutIcons = $hasShortcutIcons\n" +
            "hasSunMoonDrawables = $hasSunMoonDrawables\n"
    }
}
