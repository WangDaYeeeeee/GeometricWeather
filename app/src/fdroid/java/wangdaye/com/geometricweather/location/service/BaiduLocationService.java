package wangdaye.com.geometricweather.location.service;

import android.content.Context;

/**
 * Baidu location service.
 * Does not exist in F-Droid variant, falling back to Android Location service
 * */

public class BaiduLocationService extends AndroidLocationService {

    public BaiduLocationService(Context c) {
        super(c);
    }
}
