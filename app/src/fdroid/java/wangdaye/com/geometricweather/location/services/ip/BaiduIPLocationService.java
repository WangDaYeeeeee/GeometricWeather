package wangdaye.com.geometricweather.location.services.ip;

import android.content.Context;
import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;
import wangdaye.com.geometricweather.location.services.LocationService;

public class BaiduIPLocationService extends LocationService {

    public BaiduIPLocationService() {
        // Do nothing
    }

    @Override
    public void requestLocation(Context context, @NonNull @NotNull LocationCallback callback) {
        // Do nothing
    }

    @Override
    public void cancel() {
        // Do nothing
    }

    @Override
    public String[] getPermissions() {
        // Do nothing
        return new String[0];
    }
}
