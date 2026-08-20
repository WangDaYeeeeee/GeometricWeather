package wangdaye.com.geometricweather.daily.adapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.common.basic.models.options.unit.DurationUnit;
import wangdaye.com.geometricweather.common.basic.models.options.unit.PrecipitationUnit;
import wangdaye.com.geometricweather.common.basic.models.options.unit.ProbabilityUnit;
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit;
import wangdaye.com.geometricweather.common.basic.models.weather.Daily;
import wangdaye.com.geometricweather.common.basic.models.weather.HalfDay;
import wangdaye.com.geometricweather.common.basic.models.weather.Precipitation;
import wangdaye.com.geometricweather.common.basic.models.weather.PrecipitationDuration;
import wangdaye.com.geometricweather.common.basic.models.weather.PrecipitationProbability;
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature;
import wangdaye.com.geometricweather.daily.adapter.model.DailyAirQuality;
import wangdaye.com.geometricweather.daily.adapter.model.DailyAstro;
import wangdaye.com.geometricweather.daily.adapter.model.DailyPollen;
import wangdaye.com.geometricweather.daily.adapter.model.DailyUV;
import wangdaye.com.geometricweather.daily.adapter.model.DailyWind;
import wangdaye.com.geometricweather.daily.adapter.model.LargeTitle;
import wangdaye.com.geometricweather.daily.adapter.model.Line;
import wangdaye.com.geometricweather.daily.adapter.model.Margin;
import wangdaye.com.geometricweather.daily.adapter.model.Overview;
import wangdaye.com.geometricweather.daily.adapter.model.Title;
import wangdaye.com.geometricweather.daily.adapter.model.Value;
import wangdaye.com.geometricweather.settings.SettingsManager;

/** Builds Compose daily-detail models. RecyclerView holders were removed after Phase 6. */
public class DailyWeatherAdapter {

    public interface ViewModel {
        int getCode();
    }

    public static List<ViewModel> buildModelList(Context context, TimeZone timeZone, Daily daily) {
        List<ViewModel> modelList = new ArrayList<>();

        modelList.add(new LargeTitle(context.getString(R.string.daytime)));
        modelList.add(new Overview(daily.day(), true));
        modelList.add(new DailyWind(daily.day().getWind()));
        modelList.addAll(getHalfDayOptionalModelList(context, daily.day()));

        modelList.add(new Line());
        modelList.add(new LargeTitle(context.getString(R.string.nighttime)));
        modelList.add(new Overview(daily.night(), false));
        modelList.add(new DailyWind(daily.night().getWind()));
        modelList.addAll(getHalfDayOptionalModelList(context, daily.night()));

        modelList.add(new Line());
        modelList.add(new LargeTitle(context.getString(R.string.life_details)));
        modelList.add(new DailyAstro(timeZone, daily.sun(), daily.moon(), daily.getMoonPhase()));
        if (daily.getAirQuality().isValid()) {
            modelList.add(new Title(R.drawable.weather_haze_mini_xml, context.getString(R.string.air_quality)));
            modelList.add(new DailyAirQuality(daily.getAirQuality()));
        }
        if (daily.getPollen().isValid()) {
            modelList.add(new Title(R.drawable.ic_flower, context.getString(R.string.allergen)));
            modelList.add(new DailyPollen(daily.getPollen()));
        }
        if (daily.getUV().isValid()) {
            modelList.add(new Title(R.drawable.ic_uv, context.getString(R.string.uv_index)));
            modelList.add(new DailyUV(daily.getUV()));
        }
        modelList.add(new Line());
        modelList.add(new Value(
                context.getString(R.string.hours_of_sun),
                DurationUnit.H.getValueText(context, daily.getHoursOfSun())
        ));
        modelList.add(new Margin());
        return modelList;
    }

    private static List<ViewModel> getHalfDayOptionalModelList(Context context, HalfDay halfDay) {
        List<ViewModel> list = new ArrayList<>();
        // temperature.
        Temperature temperature = halfDay.getTemperature();
        TemperatureUnit temperatureUnit = SettingsManager.getInstance(context).getTemperatureUnit();
        if (temperature.isValid()) {
            TemperatureUnit unit = SettingsManager.getInstance(context).getTemperatureUnit();
            int resId;
            if (unit == TemperatureUnit.C) {
                resId = R.drawable.ic_temperature_celsius;
            } else if (unit == TemperatureUnit.F) {
                resId = R.drawable.ic_temperature_fahrenheit;
            } else {
                resId = R.drawable.ic_temperature_kelvin;
            }
            list.add(new Title(resId, context.getString(R.string.temperature)));
            if (temperature.getRealFeelTemperature() != null) {
                list.add(new Value(
                        context.getString(R.string.real_feel_temperature),
                        temperatureUnit.getValueText(context, temperature.getRealFeelTemperature())
                ));
            }
            if (temperature.getRealFeelShaderTemperature() != null) {
                list.add(new Value(
                        context.getString(R.string.real_feel_shade_temperature),
                        temperatureUnit.getValueText(context, temperature.getRealFeelShaderTemperature())
                ));
            }
            if (temperature.getApparentTemperature() != null) {
                list.add(new Value(
                        context.getString(R.string.apparent_temperature),
                        temperatureUnit.getValueText(context, temperature.getApparentTemperature())
                ));
            }
            if (temperature.getWindChillTemperature() != null) {
                list.add(new Value(
                        context.getString(R.string.wind_chill_temperature),
                        temperatureUnit.getValueText(context, temperature.getWindChillTemperature())
                ));
            }
            if (temperature.getWetBulbTemperature() != null) {
                list.add(new Value(
                        context.getString(R.string.wet_bulb_temperature),
                        temperatureUnit.getValueText(context, temperature.getWetBulbTemperature())
                ));
            }
            if (temperature.getDegreeDayTemperature() != null) {
                list.add(new Value(
                        context.getString(R.string.degree_day_temperature),
                        temperatureUnit.getValueText(context, temperature.getDegreeDayTemperature())
                ));
            }
            list.add(new Margin());
        }

        // precipitation.
        Precipitation precipitation = halfDay.getPrecipitation();
        PrecipitationUnit precipitationUnit = SettingsManager.getInstance(context).getPrecipitationUnit();
        if (precipitation.getTotal() != null && precipitation.getTotal() > 0) {
            list.add(new Title(R.drawable.ic_water, context.getString(R.string.precipitation)));
            list.add(new Value(
                    context.getString(R.string.total),
                    precipitationUnit.getValueText(context, precipitation.getTotal())
            ));
            if (precipitation.getRain() != null && precipitation.getRain() > 0) {
                list.add(new Value(
                        context.getString(R.string.rain),
                        precipitationUnit.getValueText(context, precipitation.getRain())
                ));
            }
            if (precipitation.getSnow() != null && precipitation.getSnow() > 0) {
                list.add(new Value(
                        context.getString(R.string.snow),
                        precipitationUnit.getValueText(context, precipitation.getSnow())
                ));
            }
            if (precipitation.getIce() != null && precipitation.getIce() > 0) {
                list.add(new Value(
                        context.getString(R.string.ice),
                        precipitationUnit.getValueText(context, precipitation.getIce())
                ));
            }
            if (precipitation.getThunderstorm() != null && precipitation.getThunderstorm() > 0) {
                list.add(new Value(
                        context.getString(R.string.thunderstorm),
                        precipitationUnit.getValueText(context, precipitation.getThunderstorm())
                ));
            }
            list.add(new Margin());
        }

        // precipitation probability.
        PrecipitationProbability probability = halfDay.getPrecipitationProbability();
        if (probability.getTotal() != null && probability.getTotal() > 0) {
            list.add(new Title(R.drawable.ic_water_percent, context.getString(R.string.precipitation_probability)));
            list.add(new Value(
                    context.getString(R.string.total),
                    ProbabilityUnit.PERCENT.getValueText(context, (int) (float) probability.getTotal())
            ));
            if (probability.getRain() != null && probability.getRain() > 0) {
                list.add(new Value(
                        context.getString(R.string.rain),
                        ProbabilityUnit.PERCENT.getValueText(context, (int) (float) probability.getRain())
                ));
            }
            if (probability.getSnow() != null && probability.getSnow() > 0) {
                list.add(new Value(
                        context.getString(R.string.snow),
                        ProbabilityUnit.PERCENT.getValueText(context, (int) (float) probability.getSnow())
                ));
            }
            if (probability.getIce() != null && probability.getIce() > 0) {
                list.add(new Value(
                        context.getString(R.string.ice),
                        ProbabilityUnit.PERCENT.getValueText(context, (int) (float) probability.getIce())
                ));
            }
            if (probability.getThunderstorm() != null && probability.getThunderstorm() > 0) {
                list.add(new Value(
                        context.getString(R.string.thunderstorm),
                        ProbabilityUnit.PERCENT.getValueText(context, (int) (float) probability.getThunderstorm())
                ));
            }
            list.add(new Margin());
        }

        // precipitation duration.
        PrecipitationDuration duration = halfDay.getPrecipitationDuration();
        if (duration.getTotal() != null && duration.getTotal() > 0) {
            list.add(new Title(R.drawable.ic_time, context.getString(R.string.precipitation_duration)));
            list.add(new Value(
                    context.getString(R.string.total),
                    DurationUnit.H.getValueText(context, duration.getTotal())
            ));
            if (duration.getRain() != null && duration.getRain() > 0) {
                list.add(new Value(
                        context.getString(R.string.rain),
                        DurationUnit.H.getValueText(context, duration.getRain())
                ));
            }
            if (duration.getSnow() != null && duration.getSnow() > 0) {
                list.add(new Value(
                        context.getString(R.string.snow),
                        DurationUnit.H.getValueText(context, duration.getSnow())
                ));
            }
            if (duration.getIce() != null && duration.getIce() > 0) {
                list.add(new Value(
                        context.getString(R.string.ice),
                        DurationUnit.H.getValueText(context, duration.getIce())
                ));
            }
            if (duration.getThunderstorm() != null && duration.getThunderstorm() > 0) {
                list.add(new Value(
                        context.getString(R.string.thunderstorm),
                        DurationUnit.H.getValueText(context, duration.getThunderstorm())
                ));
            }
            list.add(new Margin());
        }
        return list;
    }
}
