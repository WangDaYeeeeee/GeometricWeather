package wangdaye.com.geometricweather.common.utils.helpers;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.common.basic.models.Location;
import wangdaye.com.geometricweather.common.basic.models.weather.Weather;
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode;
import wangdaye.com.geometricweather.db.DatabaseHelper;
import wangdaye.com.geometricweather.theme.resource.ResourceHelper;
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider;
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory;

/**
 * Shortcuts manager.
 * */

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class ShortcutsHelper {

    public static void refreshShortcutsInNewThread(final Context c, List<Location> locationList) {
        AsyncHelper.runOnIO(() -> {
            ShortcutManager shortcutManager = c.getSystemService(ShortcutManager.class);
            if (shortcutManager == null) {
                return;
            }

            List<Location> list = Location.excludeInvalidResidentLocation(c, locationList);
            ResourceProvider provider = ResourcesProviderFactory.getNewInstance();
            List<ShortcutInfo> shortcutList = new ArrayList<>();

            // refresh button.
            Icon icon;
            String title = c.getString(R.string.refresh);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                icon = Icon.createWithAdaptiveBitmap(
                        drawableToBitmap(
                                Objects.requireNonNull(
                                        ContextCompat.getDrawable(c, R.drawable.shortcuts_refresh_foreground)
                                )
                        )
                );
            } else {
                icon = Icon.createWithResource(c, R.drawable.shortcuts_refresh);
            }
            shortcutList.add(
                    new ShortcutInfo.Builder(c, "refresh_data")
                            .setIcon(icon)
                            .setShortLabel(title)
                            .setLongLabel(title)
                            .setIntent(IntentHelper.buildAwakeUpdateActivityIntent())
                            .build()
            );

            // location list.
            int count = Math.min(
                    shortcutManager.getMaxShortcutCountPerActivity() - 1,
                    list.size()
            );
            for (int i = 0; i < count; i ++) {
                Weather weather = DatabaseHelper.getInstance(c).readWeather(list.get(i));
                if (weather != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        icon = getAdaptiveIcon(
                                provider,
                                weather.getCurrent().getWeatherCode(),
                                list.get(i).isDaylight()
                        );
                    } else {
                        icon = getIcon(
                                provider,
                                weather.getCurrent().getWeatherCode(),
                                list.get(i).isDaylight()
                        );
                    }
                } else {
                    icon = getIcon(provider, WeatherCode.CLEAR, true);
                }

                title = list.get(i).isCurrentPosition() ? c.getString(R.string.current_location) : list.get(i).getCityName(c);

                shortcutList.add(
                        new ShortcutInfo.Builder(c, list.get(i).getFormattedId())
                                .setIcon(icon)
                                .setShortLabel(title)
                                .setLongLabel(title)
                                .setIntent(IntentHelper.buildMainActivityIntent(list.get(i)))
                                .build()
                );
            }

            try {
                shortcutManager.setDynamicShortcuts(shortcutList);
            } catch (Exception ignore) {
                // do nothing.
            }
        });
    }

    @NonNull
    private static Bitmap drawableToBitmap(@NonNull Drawable drawable) {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    private static Icon getAdaptiveIcon(ResourceProvider provider, WeatherCode code, boolean daytime) {
        return Icon.createWithAdaptiveBitmap(
                drawableToBitmap(
                        ResourceHelper.getShortcutsForegroundIcon(provider, code, daytime)
                )
        );
    }

    @NonNull
    private static Icon getIcon(ResourceProvider provider, WeatherCode code, boolean daytime) {
        return Icon.createWithBitmap(
                drawableToBitmap(
                        ResourceHelper.getShortcutsIcon(provider, code, daytime)
                )
        );
    }
}
