package wangdaye.com.geometricweather.common.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;
import androidx.annotation.Size;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import java.util.Calendar;
import java.util.TimeZone;

import wangdaye.com.geometricweather.R;

/**
 * Display utils.
 * */

public class DisplayUtils {

    public static final Interpolator FLOATING_DECELERATE_INTERPOLATOR
            = new DecelerateInterpolator(1f);

    private static final int MAX_TABLET_ADAPTIVE_LIST_WIDTH_DIP_PHONE = 512;
    private static final int MAX_TABLET_ADAPTIVE_LIST_WIDTH_DIP_TABLET = 600;

    public static float dpToPx(Context context, float dp) {
        return dp * (context.getResources().getDisplayMetrics().densityDpi / 160f);
    }

    public static float spToPx(Context context, int sp) {
        return sp * (context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static void setSystemBarStyle(Context context, Window window,
                                         boolean statusShader, boolean lightStatus,
                                         boolean navigationShader, boolean lightNavigation) {
        setSystemBarStyle(context, window,
                false, statusShader, lightStatus, navigationShader, lightNavigation);
    }

    public static void setSystemBarStyle(Context context, Window window, boolean miniAlpha,
                                         boolean statusShader, boolean lightStatus,
                                         boolean navigationShader, boolean lightNavigation) {

        // statusShader &= Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;
        lightStatus &= Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        navigationShader &= Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;
        lightNavigation &= Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (lightStatus) {
            visibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        if (lightNavigation) {
            visibility |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        window.getDecorView().setSystemUiVisibility(visibility);

        setSystemBarColor(context, window, miniAlpha, statusShader, lightStatus, navigationShader, lightNavigation);
    }

    public static void setSystemBarColor(Context context, Window window, boolean miniAlpha,
                                         boolean statusShader, boolean lightStatus,
                                         boolean navigationShader, boolean lightNavigation) {

        // statusShader &= Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;
        lightStatus &= Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        navigationShader &= Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;
        lightNavigation &= Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

        if (!statusShader) {
            window.setStatusBarColor(Color.argb(0, 0, 0, 0));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(getStatusBarColor23(context, lightStatus, miniAlpha));
        } else {
            window.setStatusBarColor(getStatusBarColor21());
        }
        if (!navigationShader) {
            window.setNavigationBarColor(Color.argb(0, 0, 0, 0));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setNavigationBarColor(getStatusBarColor26(context, lightNavigation, miniAlpha));
        } else {
            window.setNavigationBarColor(getNavigationBarColor21());
        }
    }

    @ColorInt
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private static int getStatusBarColor21() {
        return ColorUtils.setAlphaComponent(Color.BLACK, (int) (0.1 * 255));
    }

    @ColorInt
    @RequiresApi(Build.VERSION_CODES.M)
    private static int getStatusBarColor23(Context context, boolean light, boolean miniAlpha) {
        if (miniAlpha) {
            return light
                    ? ColorUtils.setAlphaComponent(Color.WHITE, (int) (0.2 * 255))
                    : ColorUtils.setAlphaComponent(Color.BLACK, (int) (0.1 * 255));
        }
        return ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, light ? R.color.colorRoot_light : R.color.colorRoot_dark),
                (int) (0.8 * 255)
        );
    }

    @ColorInt
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private static int getNavigationBarColor21() {
        return ColorUtils.setAlphaComponent(Color.BLACK, (int) (0.1 * 255));
    }

    @ColorInt
    @RequiresApi(Build.VERSION_CODES.O)
    private static int getStatusBarColor26(Context context, boolean light, boolean miniAlpha) {
        if (miniAlpha) {
            return light
                    ? ColorUtils.setAlphaComponent(Color.WHITE, (int) (0.2 * 255))
                    : ColorUtils.setAlphaComponent(Color.BLACK, (int) (0.1 * 255));
        }
        return ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, light ? R.color.colorRoot_light : R.color.colorRoot_dark),
                (int) (0.8 * 255)
        );
    }

    public static boolean isTabletDevice(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isRtl(Context context) {
        return context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    public static boolean isDarkMode(Context context) {
        return (context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    @NonNull
    public static Bitmap drawableToBitmap(@NonNull Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @ColorInt
    public static int bitmapToColorInt(@NonNull Bitmap bitmap) {
        return ThumbnailUtils.extractThumbnail(bitmap, 1, 1)
                .getPixel(0, 0);
    }

    public static boolean isLightColor(@ColorInt int color) {
        int alpha = 0xFF << 24;
        int grey = color;
        int red = ((grey & 0x00FF0000) >> 16);
        int green = ((grey & 0x0000FF00) >> 8);
        int blue = (grey & 0x000000FF);

        grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
        grey = alpha | (grey << 16) | (grey << 8) | grey;
        return grey > 0xffbdbdbd;
    }

    @Px
    public static int getTabletListAdaptiveWidth(Context context, @Px int width) {
        if (!isTabletDevice(context) && !isLandscape(context)) {
            return width;
        }
        return (int) Math.min(
                width,
                DisplayUtils.dpToPx(
                        context,
                        isTabletDevice(context)
                                ? MAX_TABLET_ADAPTIVE_LIST_WIDTH_DIP_TABLET
                                : MAX_TABLET_ADAPTIVE_LIST_WIDTH_DIP_PHONE
                )
        );
    }

    @ColorInt
    public static int blendColor(@ColorInt int foreground, @ColorInt int background) {
        int scr = Color.red(foreground);
        int scg = Color.green(foreground);
        int scb = Color.blue(foreground);
        int sa = foreground >>> 24;
        int dcr = Color.red(background);
        int dcg = Color.green(background);
        int dcb = Color.blue(background);
        int color_r = dcr * (0xff - sa) / 0xff + scr * sa / 0xff;
        int color_g = dcg * (0xff - sa) / 0xff + scg * sa / 0xff;
        int color_b = dcb * (0xff - sa) / 0xff + scb * sa / 0xff;
        return ((color_r << 16) + (color_g << 8) + color_b) | (0xff000000);
    }

    public static boolean is12Hour(Context context) {
        return !DateFormat.is24HourFormat(context);
    }

    public static boolean isDaylight(TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(timeZone);
        int time = 60 * calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE);

        int sr = 60 * 6;
        int ss = 60 * 18;
        return sr < time && time < ss;
    }

    // translationY, scaleX, scaleY
    @Size(3)
    public static Animator[] getFloatingOvershotEnterAnimators(View view) {
        return getFloatingOvershotEnterAnimators(view, 1.5f);
    }

    @Size(3)
    public static Animator[] getFloatingOvershotEnterAnimators(View view, float overshootFactor) {
        return getFloatingOvershotEnterAnimators(view, overshootFactor,
                view.getTranslationY(), view.getScaleX(), view.getScaleY());
    }

    @Size(3)
    public static Animator[] getFloatingOvershotEnterAnimators(View view,
                                                               float overshootFactor,
                                                               float translationYFrom,
                                                               float scaleXFrom,
                                                               float scaleYFrom) {
        Animator translation = ObjectAnimator.ofFloat(
                view, "translationY", translationYFrom, 0f);
        translation.setInterpolator(new OvershootInterpolator(overshootFactor));

        Animator scaleX = ObjectAnimator.ofFloat(
                view, "scaleX", scaleXFrom, 1f);
        scaleX.setInterpolator(FLOATING_DECELERATE_INTERPOLATOR);

        Animator scaleY = ObjectAnimator.ofFloat(
                view, "scaleY", scaleYFrom, 1f);
        scaleY.setInterpolator(FLOATING_DECELERATE_INTERPOLATOR);

        return new Animator[] {translation, scaleX, scaleY};
    }

    public static void getVisibleDisplayFrame(View view, Rect rect) {
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) view.getContext().getSystemService(
                    Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getRealMetrics(metrics);

            WindowInsets insets = view.getRootWindowInsets();

            rect.set(
                    insets.getSystemWindowInsetLeft(),
                    insets.getSystemWindowInsetTop(),
                    metrics.widthPixels - insets.getSystemWindowInsetRight(),
                    metrics.heightPixels - insets.getSystemWindowInsetBottom()
            );
        } else {
            view.getWindowVisibleDisplayFrame(rect);
        }
        */
        // looks like has a good performance.
        view.getWindowVisibleDisplayFrame(rect);
    }
}
