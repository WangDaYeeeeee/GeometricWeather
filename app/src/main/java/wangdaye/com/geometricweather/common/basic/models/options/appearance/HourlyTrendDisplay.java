package wangdaye.com.geometricweather.common.basic.models.options.appearance;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import wangdaye.com.geometricweather.R;

public enum HourlyTrendDisplay {

    TAG_TEMPERATURE("temperature", R.string.temperature),
    TAG_WIND("wind", R.string.wind),
    TAG_PRECIPITATION("precipitation", R.string.precipitation);

    private final String value;
    private @StringRes final int nameId;

    HourlyTrendDisplay(String value, int nameId) {
        this.value = value;
        this.nameId = nameId;
    }

    public String getTagValue() {
        return value;
    }

    public String getTagName(Context context) {
        return context.getString(nameId);
    }

    @NonNull
    public static List<HourlyTrendDisplay> toHourlyTrendDisplayList(String value) {
        if (TextUtils.isEmpty(value)) {
            return new ArrayList<>();
        }
        try {
            String[] cards = value.split("&");

            List<HourlyTrendDisplay> list = new ArrayList<>();
            for (String card : cards) {
                switch (card) {
                    case "temperature":
                        list.add(TAG_TEMPERATURE);
                        break;

                    case "wind":
                        list.add(TAG_WIND);
                        break;

                    case "precipitation":
                        list.add(TAG_PRECIPITATION);
                        break;
                }
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @NonNull
    public static String toValue(@NonNull List<HourlyTrendDisplay> list) {
        StringBuilder builder = new StringBuilder();
        for (HourlyTrendDisplay v : list) {
            builder.append("&").append(v.getTagValue());
        }
        if (builder.length() > 0 && builder.charAt(0) == '&') {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    @NonNull
    public static String getSummary(Context context, @NonNull List<HourlyTrendDisplay> list) {
        StringBuilder builder = new StringBuilder();
        for (HourlyTrendDisplay v : list) {
            builder.append(",").append(v.getTagName(context));
        }
        if (builder.length() > 0 && builder.charAt(0) == ',') {
            builder.deleteCharAt(0);
        }
        return builder.toString().replace(",", ", ");
    }
}
