package team7.tcss450.uw.edu.tcss_750_t7_2.weather;

import java.io.Serializable;

public class TenDayWeather implements Serializable {
    private final String mDate;
    private final String mWeatherText;
    private final int mWeatherCode;
    private final String mTemp;

    public static class Builder {
        private final String mDate;
        private final String mWeatherText;
        private final int mWeatherCode;
        private final String mTemp;

        public Builder(String date, String weatherText, int weatherCode, String weatherTemp) {
            this.mDate = date;
            this.mWeatherText = weatherText;
            this.mWeatherCode = weatherCode;
            this.mTemp = weatherTemp;
        }

        public TenDayWeather build() {
            return new TenDayWeather(this);
        }
    }

    private TenDayWeather(final Builder builder) {
        this.mDate = builder.mDate;
        this.mWeatherText = builder.mWeatherText;
        this.mWeatherCode = builder.mWeatherCode;
        this.mTemp = builder.mTemp;
    }

    public String getDate() {
        return mDate;
    }

    public  String getWeatherText() {
        return mWeatherText;
    }

    public int getWeatherCode() {
        return mWeatherCode;
    }

    public String getTemp() {
        return mTemp;
    }
}
