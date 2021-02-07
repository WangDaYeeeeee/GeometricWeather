package wangdaye.com.geometricweather.location.service;

import android.content.Context;

/**
 * A map location service.
 * Does not exist in F-Droid variant, falling back to Android Location service
 * */
public class AMapLocationService extends AndroidLocationService {

    public AMapLocationService(Context c) {
        super(c);
    }
}
