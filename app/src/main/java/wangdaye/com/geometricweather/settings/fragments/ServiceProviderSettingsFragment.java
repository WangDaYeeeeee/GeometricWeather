package wangdaye.com.geometricweather.settings.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;

import java.util.ArrayList;
import java.util.List;

import wangdaye.com.geometricweather.GeometricWeather;
import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.common.basic.models.Location;
import wangdaye.com.geometricweather.common.basic.models.options.provider.LocationProvider;
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource;
import wangdaye.com.geometricweather.db.DatabaseHelper;
import wangdaye.com.geometricweather.common.utils.helpters.SnackbarHelper;

/**
 * Service provider settings fragment.
 * */

public class ServiceProviderSettingsFragment extends AbstractSettingsFragment {

    private @Nullable OnWeatherSourceChangedListener mListener;

    public interface OnWeatherSourceChangedListener {
        void onWeatherSourceChanged(Location location);
    }

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
        chineseSource.setSummary(getSettingsOptionManager().getWeatherSource().getSourceName(requireContext()));
        chineseSource.setOnPreferenceChangeListener((preference, newValue) -> {
            WeatherSource source = WeatherSource.getInstance((String) newValue);

            getSettingsOptionManager().setWeatherSource(source);
            preference.setSummary(source.getSourceName(requireContext()));

            List<Location> locationList = DatabaseHelper.getInstance(requireActivity()).readLocationList();
            for (int i = 0; i < locationList.size(); i ++) {
                Location src = locationList.get(i);
                if (src.isCurrentPosition()) {
                    if (src.getWeatherSource() != null) {
                        DatabaseHelper.getInstance(requireActivity()).deleteWeather(src);
                    }
                    locationList.set(i, new Location(src, source));
                    if (mListener != null) {
                        mListener.onWeatherSourceChanged(locationList.get(i));
                    }
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
                        && LocationProvider.getInstance((String) locationService.getEntryValues()[i]) != LocationProvider.BAIDU) {
                    locationEntries.add(locationService.getEntries()[i]);
                    locationValues.add(locationService.getEntryValues()[i]);
                }
            }
            setListPreferenceValues(locationService, locationEntries, locationValues);
        }

        locationService.setSummary(getSettingsOptionManager().getLocationProvider().getProviderName(requireContext()));
        locationService.setOnPreferenceChangeListener((preference, newValue) -> {
            getSettingsOptionManager().setLocationProvider(LocationProvider.getInstance((String) newValue));
            preference.setSummary(getSettingsOptionManager().getLocationProvider().getProviderName(requireContext()));
            SnackbarHelper.showSnackbar(
                    getString(R.string.feedback_restart),
                    getString(R.string.restart),
                    v -> GeometricWeather.getInstance().recreateAllActivities()
            );
            return true;
        });
    }

    public void setOnWeatherSourceChangedListener(@Nullable OnWeatherSourceChangedListener l) {
        mListener = l;
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
