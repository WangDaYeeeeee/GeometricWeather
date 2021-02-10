package wangdaye.com.geometricweather.settings.fragments;

import android.os.Bundle;

import androidx.preference.ListPreference;

import java.util.ArrayList;
import java.util.List;

import wangdaye.com.geometricweather.GeometricWeather;
import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.basic.models.Location;
import wangdaye.com.geometricweather.basic.models.options.provider.LocationProvider;
import wangdaye.com.geometricweather.basic.models.options.provider.WeatherSource;
import wangdaye.com.geometricweather.db.DatabaseHelper;
import wangdaye.com.geometricweather.utils.helpters.SnackbarHelper;

/**
 * Service provider settings fragment.
 * */

public class ServiceProviderSettingsFragment extends AbstractSettingsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.perference_service_provider);
        initPreferences();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // do nothing.
    }

    private void initPreferences() {
        // weather source.
        ListPreference chineseSource = findPreference(getString(R.string.key_weather_source));

        // Remove closed source providers if building the F-Droid flavor
        if (getBuildFlavor().contains("fdroid")) {
            List<CharSequence> weatherEntries = new ArrayList<>();
            List<CharSequence> weatherValues = new ArrayList<>();
            for (int i = 0; i < chineseSource.getEntries().length; ++i) {
                if (WeatherSource.getInstance((String) chineseSource.getEntryValues()[i]) != WeatherSource.CN
                        && WeatherSource.getInstance((String) chineseSource.getEntryValues()[i]) != WeatherSource.CAIYUN) {
                    weatherEntries.add(chineseSource.getEntries()[i]);
                    weatherValues.add(chineseSource.getEntryValues()[i]);
                }
            }
            setListPreferenceValues(chineseSource, weatherEntries, weatherValues);
        }

        chineseSource.setSummary(getSettingsOptionManager().getWeatherSource().getSourceName(getActivity()));
        chineseSource.setOnPreferenceChangeListener((preference, newValue) -> {
            WeatherSource source = WeatherSource.getInstance((String) newValue);

            getSettingsOptionManager().setWeatherSource(source);
            preference.setSummary(source.getSourceName(getActivity()));

            List<Location> locationList = DatabaseHelper.getInstance(requireActivity()).readLocationList();
            for (int i = 0; i < locationList.size(); i ++) {
                Location src = locationList.get(i);
                if (src.isCurrentPosition()) {
                    if (src.getWeatherSource() != null) {
                        DatabaseHelper.getInstance(requireActivity()).deleteWeather(src);
                    }
                    locationList.set(i, new Location(src, source));
                    break;
                }
            }
            DatabaseHelper.getInstance(requireActivity()).writeLocationList(locationList);
            return true;
        });

        // location source.
        ListPreference locationService = findPreference(getString(R.string.key_location_service));

        // Remove closed source providers if building the F-Droid flavor
        if (getBuildFlavor().contains("fdroid")) {
            List<CharSequence> locationEntries = new ArrayList<>();
            List<CharSequence> locationValues = new ArrayList<>();
            for (int i = 0; i < locationService.getEntries().length; ++i) {
                if (LocationProvider.getInstance((String) locationService.getEntryValues()[i]) != LocationProvider.AMAP
                        && LocationProvider.getInstance((String) locationService.getEntryValues()[i]) != LocationProvider.BAIDU
                        && LocationProvider.getInstance((String) locationService.getEntryValues()[i]) != LocationProvider.BAIDU_IP) {
                    locationEntries.add(locationService.getEntries()[i]);
                    locationValues.add(locationService.getEntryValues()[i]);
                }
            }
            setListPreferenceValues(locationService, locationEntries, locationValues);
        }

        locationService.setSummary(getSettingsOptionManager().getLocationProvider().getProviderName(getActivity()));
        locationService.setOnPreferenceChangeListener((preference, newValue) -> {
            getSettingsOptionManager().setLocationProvider(LocationProvider.getInstance((String) newValue));
            preference.setSummary(getSettingsOptionManager().getLocationProvider().getProviderName(getActivity()));
            SnackbarHelper.showSnackbar(
                    getString(R.string.feedback_restart),
                    getString(R.string.restart),
                    v -> GeometricWeather.getInstance().recreateAllActivities()
            );
            return true;
        });
    }

    private static void setListPreferenceValues(ListPreference pref, List<CharSequence> entries, List<CharSequence> values) {
        CharSequence[] contents = new CharSequence[entries.size()];
        entries.toArray(contents);
        pref.setEntries(contents);
        contents = new CharSequence[values.size()];
        values.toArray(contents);
        pref.setEntryValues(contents);
    }

    private String getBuildFlavor() {
        return wangdaye.com.geometricweather.BuildConfig.FLAVOR;
    }
}
