package wangdaye.com.geometricweather.utils.helpter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

import wangdaye.com.geometricweather.data.entity.model.Location;
import wangdaye.com.geometricweather.data.service.WeatherService;

/**
 * Location utils.
 * */

public class LocationHelper {

    private WeatherService weather;

    private static final String PREFERENCE_LOCAL = "LOCAL_PREFERENCE";
    private static final String KEY_LAST_RESULT = "LAST_RESULT";
    
    private class SimpleWeatherLocationListener implements OnRequestWeatherLocationListener {
        // data
        private Location location;
        private OnRequestLocationListener listener;

        SimpleWeatherLocationListener(Location location, OnRequestLocationListener l) {
            this.location = location;
            this.listener = l;
        }

        @Override
        public void requestWeatherLocationSuccess(String query, List<Location> locationList) {
            if (locationList.size() > 0) {
                listener.requestLocationSuccess(locationList.get(0).setLocal(), true);
            } else {
                listener.requestLocationFailed(location);
            }
        }

        @Override
        public void requestWeatherLocationFailed(String query) {
            listener.requestLocationFailed(location);
        }
    }

    public LocationHelper(Context c) {
        weather = new WeatherService();
    }

    public void requestLocation(Context c, Location location, OnRequestLocationListener l) {
        NetworkInfo info = ((ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            l.requestLocationFailed(location);
        }
    }

    public void requestWeatherLocation(Context c, String query, OnRequestWeatherLocationListener l) {
        weather = WeatherService.getService().requestNewLocation(c, query, l);
    }

    private void requestWeatherLocationByGeoPosition(Context c, Location location, OnRequestWeatherLocationListener l) {
        weather = WeatherService.getService().requestNewLocationByGeoPosition(c, location.lat, location.lon, l);
    }

    public void cancel() {
        if (weather != null) {
            weather.cancel();
        }
    }

    // interface.

    public interface OnRequestLocationListener {
        void requestLocationSuccess(Location requestLocation, boolean locationChanged);
        void requestLocationFailed(Location requestLocation);
    }

    public interface OnRequestWeatherLocationListener {
        void requestWeatherLocationSuccess(String query, List<Location> locationList);
        void requestWeatherLocationFailed(String query);
    }
}