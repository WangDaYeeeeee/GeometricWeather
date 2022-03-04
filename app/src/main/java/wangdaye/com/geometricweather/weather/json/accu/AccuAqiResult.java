package wangdaye.com.geometricweather.weather.json.accu;

import java.util.Date;
import java.util.List;

/**
 * Accu aqi result.
 * */

public class AccuAqiResult {

    /**
     * {
     *   "success": true,
     *   "status": "200",
     *   "version": 2,
     *   "data": [
     *     {
     *       "date": "2020-02-03T05:00:00+05:30",
     *       "epochDate": 1580706000,
     *       "overallIndex": 270.6,
     *       "overallPlumeLabsIndex": 208.8,
     *       "dominantPollutant": "Sulfure Dioxide",
     *       "category": "Very Unhealthy",
     *       "categoryColor": "#C72EAA",
     *       "hazardStatement": "Health effects will be immediately felt by sensitive groups and should avoid outdoor activity. Healthy individuals are likely to experience difficulty breathing and throat irritation; consider staying indoors and rescheduling outdoor activities."
     *       link": "https://www.accuweather.com/en/in/shyamapur/3182693/air-quality-index/3182693?lang=en-us
     *     }
     *     ]
     * }
     */

    public boolean success;
    public int status;
    public int version;

    public static class AqiData {
        public Date date;
        public long epochDate;
        public int overallIndex;
        public String dominantPollutant;
        public String category;
        public String categoryColor;
        public String hazardStatement;

        public static class Pollutant {
            public String type;
            public String name;

            public static class Concentration {
                public Float value;
                public String unit;
                public int unitType;
            }

            public Concentration concentration;
            public String source;
        }

        public List<Pollutant> pollutants;
    }

    public List<AqiData> data;
}
