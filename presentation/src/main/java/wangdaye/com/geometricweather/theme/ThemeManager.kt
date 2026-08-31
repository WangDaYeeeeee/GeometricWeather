package wangdaye.com.geometricweather.theme

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import wangdaye.com.geometricweather.common.basic.models.options.DarkMode
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.settings.SettingsManager

class ThemeManager private constructor(
    var darkMode: DarkMode,
) {

    companion object {

        @Volatile
        private var instance: ThemeManager? = null

        @JvmStatic
        fun getInstance(context: Context): ThemeManager {
            return instance ?: synchronized(ThemeManager::class) {
                instance ?: ThemeManager(
                    darkMode = SettingsManager.getInstance(context).darkMode,
                ).also { instance = it }
            }
        }

        private fun generateGlobalUIMode(
            darkMode: DarkMode
        ): Int = when (darkMode) {
            DarkMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            DarkMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    private val _uiMode = MutableStateFlow(
        generateGlobalUIMode(darkMode = darkMode)
    )
    val uiMode: StateFlow<Int> = _uiMode.asStateFlow()
    private val typedValue = TypedValue()

    fun update(darkMode: DarkMode) {
        this.darkMode = darkMode
        _uiMode.value = generateGlobalUIMode(darkMode = this.darkMode)
    }

    fun getThemeColor(context: Context, @AttrRes id: Int): Int {
        context.theme.resolveAttribute(id, typedValue, true)
        return typedValue.data
    }

    @SuppressLint("ResourceType")
    fun getThemeColors(context: Context, @AttrRes ids: IntArray): IntArray {
        val a = context.theme.obtainStyledAttributes(ids)
        val colors = ids.mapIndexed { index, _ ->
            a.getColor(index, Color.TRANSPARENT)
        }
        a.recycle()

        return colors.toIntArray()
    }

    fun generateThemeContext(
        context: Context,
        lightTheme: Boolean
    ): Context = context.createConfigurationContext(
        Configuration(context.resources.configuration).apply {
            uiMode = uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()
            uiMode = uiMode or if (lightTheme) {
                Configuration.UI_MODE_NIGHT_NO
            } else {
                Configuration.UI_MODE_NIGHT_YES
            }
        }
    ).apply {
        setTheme(R.style.GeometricWeatherTheme)
    }
}
