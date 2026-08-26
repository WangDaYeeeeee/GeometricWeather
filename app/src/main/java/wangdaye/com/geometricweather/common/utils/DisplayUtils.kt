package wangdaye.com.geometricweather.common.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.os.Build
import android.text.format.DateFormat
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.Size
import androidx.annotation.StyleRes
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import com.google.android.material.resources.TextAppearance
import kotlin.math.min

object DisplayUtils {

    @JvmField
    val FLOATING_DECELERATE_INTERPOLATOR: Interpolator = DecelerateInterpolator(1f)

    private const val MAX_TABLET_ADAPTIVE_LIST_WIDTH_DIP_PHONE = 512
    private const val MAX_TABLET_ADAPTIVE_LIST_WIDTH_DIP_TABLET = 600

    const val DEFAULT_CARD_LIST_ITEM_ELEVATION_DP = 2f

    @JvmStatic
    fun dpToPx(context: Context, dp: Float): Float {
        return dp * (context.resources.displayMetrics.densityDpi / 160f)
    }

    @JvmStatic
    fun spToPx(context: Context, sp: Int): Float {
        return sp * context.resources.displayMetrics.scaledDensity
    }

    @JvmStatic
    fun pxToDp(context: Context, @Px px: Int): Float {
        return px / (context.resources.displayMetrics.densityDpi / 160f)
    }

    @JvmStatic
    fun setSystemBarStyle(
        context: Context,
        window: Window,
        lightStatus: Boolean,
        lightNavigation: Boolean
    ) {
        setSystemBarStyle(
            context,
            window,
            false,
            lightStatus,
            false,
            lightNavigation
        )
    }

    @JvmStatic
    fun setSystemBarStyle(
        context: Context,
        window: Window,
        statusShader: Boolean,
        lightStatus: Boolean,
        navigationShader: Boolean,
        lightNavigation: Boolean
    ) {
        var statusLight = lightStatus
        var statusShade = statusShader
        var navLight = lightNavigation
        var navShade = navigationShader

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = statusLight
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            insetsController.isAppearanceLightNavigationBars = navLight
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            statusLight = false
            statusShade = true
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            navLight = false
            navShade = true
        }
        navShade = navShade && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q

        if (!statusShade) {
            window.statusBarColor = Color.TRANSPARENT
        } else {
            window.statusBarColor = ColorUtils.setAlphaComponent(
                if (statusLight) Color.WHITE else Color.BLACK,
                ((if (statusLight) 0.5 else 0.2) * 255).toInt()
            )
        }
        if (!navShade) {
            window.navigationBarColor = Color.TRANSPARENT
        } else {
            window.navigationBarColor = ColorUtils.setAlphaComponent(
                if (navLight) Color.WHITE else Color.BLACK,
                ((if (navLight) 0.5 else 0.2) * 255).toInt()
            )
        }
    }

    @JvmStatic
    fun isTabletDevice(context: Context): Boolean {
        return (context.resources.configuration.screenLayout
            and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    @JvmStatic
    fun isLandscape(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    @JvmStatic
    fun isRtl(context: Context): Boolean {
        return context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
    }

    @JvmStatic
    fun isDarkMode(context: Context): Boolean {
        return (context.resources.configuration.uiMode
            and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    @JvmStatic
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    @JvmStatic
    @ColorInt
    fun bitmapToColorInt(bitmap: Bitmap): Int {
        return ThumbnailUtils.extractThumbnail(bitmap, 1, 1).getPixel(0, 0)
    }

    @JvmStatic
    fun isLightColor(@ColorInt color: Int): Boolean {
        val alpha = 0xFF shl 24
        var grey = color
        val red = (grey and 0x00FF0000) shr 16
        val green = (grey and 0x0000FF00) shr 8
        val blue = grey and 0x000000FF
        grey = (red * 0.3 + green * 0.59 + blue * 0.11).toInt()
        grey = alpha or (grey shl 16) or (grey shl 8) or grey
        return grey > 0xffbdbdbd.toInt()
    }

    @JvmStatic
    @Px
    fun getTabletListAdaptiveWidth(context: Context, @Px width: Int): Int {
        if (!isTabletDevice(context) && !isLandscape(context)) {
            return width
        }
        return min(
            width.toDouble(),
            dpToPx(
                context,
                (
                    if (isTabletDevice(context)) MAX_TABLET_ADAPTIVE_LIST_WIDTH_DIP_TABLET
                    else MAX_TABLET_ADAPTIVE_LIST_WIDTH_DIP_PHONE
                    ).toFloat()
            ).toDouble()
        ).toInt()
    }

    @JvmStatic
    @ColorInt
    fun blendColor(@ColorInt foreground: Int, @ColorInt background: Int): Int {
        val scr = Color.red(foreground)
        val scg = Color.green(foreground)
        val scb = Color.blue(foreground)
        val sa = foreground ushr 24
        val dcr = Color.red(background)
        val dcg = Color.green(background)
        val dcb = Color.blue(background)
        val colorR = dcr * (0xff - sa) / 0xff + scr * sa / 0xff
        val colorG = dcg * (0xff - sa) / 0xff + scg * sa / 0xff
        val colorB = dcb * (0xff - sa) / 0xff + scb * sa / 0xff
        return (colorR shl 16) + (colorG shl 8) + colorB or -0x1000000
    }

    @JvmStatic
    fun is12Hour(context: Context): Boolean {
        return !DateFormat.is24HourFormat(context)
    }

    @JvmStatic
    @Size(3)
    fun getFloatingOvershotEnterAnimators(view: View): Array<Animator> {
        return getFloatingOvershotEnterAnimators(view, 1.5f)
    }

    @JvmStatic
    @Size(3)
    fun getFloatingOvershotEnterAnimators(view: View, overshootFactor: Float): Array<Animator> {
        return getFloatingOvershotEnterAnimators(
            view,
            overshootFactor,
            view.translationY,
            view.scaleX,
            view.scaleY
        )
    }

    @JvmStatic
    @Size(3)
    fun getFloatingOvershotEnterAnimators(
        view: View,
        overshootFactor: Float,
        translationYFrom: Float,
        scaleXFrom: Float,
        scaleYFrom: Float
    ): Array<Animator> {
        val translation = ObjectAnimator.ofFloat(view, "translationY", translationYFrom, 0f)
        translation.interpolator = OvershootInterpolator(overshootFactor)
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", scaleXFrom, 1f)
        scaleX.interpolator = FLOATING_DECELERATE_INTERPOLATOR
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", scaleYFrom, 1f)
        scaleY.interpolator = FLOATING_DECELERATE_INTERPOLATOR
        return arrayOf(translation, scaleX, scaleY)
    }

    @JvmStatic
    fun getVisibleDisplayFrame(view: View, rect: Rect) {
        view.getWindowVisibleDisplayFrame(rect)
    }

    @SuppressLint("RestrictedApi", "VisibleForTests")
    @JvmStatic
    fun getTypefaceFromTextAppearance(context: Context, @StyleRes textAppearanceId: Int): Typeface {
        return TextAppearance(context, textAppearanceId).getFont(context)
    }

    @JvmStatic
    @ColorInt
    fun getWidgetSurfaceColor(
        elevationDp: Float,
        @ColorInt tintColor: Int,
        @ColorInt surfaceColor: Int
    ): Int {
        if (elevationDp == 0f) {
            return surfaceColor
        }
        val foreground = ColorUtils.setAlphaComponent(
            tintColor,
            (((4.5f * Math.log((elevationDp + 1).toDouble())) + 2f) / 100f * 255).toInt()
        )
        return blendColor(foreground, surfaceColor)
    }
}
