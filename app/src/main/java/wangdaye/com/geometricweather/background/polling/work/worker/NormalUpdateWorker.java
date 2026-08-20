package wangdaye.com.geometricweather.background.polling.work.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.WorkerParameters;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import wangdaye.com.geometricweather.common.basic.models.Location;
import wangdaye.com.geometricweather.location.LocationHelper;
import wangdaye.com.geometricweather.remoteviews.NotificationHelper;
import wangdaye.com.geometricweather.remoteviews.WidgetHelper;
import wangdaye.com.geometricweather.weather.WeatherHelper;

@HiltWorker
public class NormalUpdateWorker extends AsyncUpdateWorker {

    @AssistedInject
    public NormalUpdateWorker(@Assisted @NonNull Context context,
                              @Assisted @NonNull WorkerParameters workerParams,
                              LocationHelper locationHelper,
                              WeatherHelper weatherHelper) {
        super(context, workerParams, locationHelper, weatherHelper);
    }

    @Override
    public void updateView(Context context, Location location) {
        WidgetHelper.updateWidgetIfNecessary(context, location);
    }

    @Override
    public void updateView(Context context, List<Location> locationList) {
        WidgetHelper.updateWidgetIfNecessary(context, locationList);
        NotificationHelper.updateNotificationIfNecessary(context, locationList);
    }

    @NonNull
    @Override
    public Result handleUpdateResult(boolean failed) {
        return failed ? Result.retry() : Result.success();
    }
}
