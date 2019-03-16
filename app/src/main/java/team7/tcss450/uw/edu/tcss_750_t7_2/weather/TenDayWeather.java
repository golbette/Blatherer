package team7.tcss450.uw.edu.tcss_750_t7_2.weather;

import java.io.Serializable;

/**
 * Builder design class for ten day weather forecast
 */
public class TenDayWeather implements Serializable {
    private final String mDate;
    private final String mWeatherText;
    private final int mWeatherCode;
    private final String mTemp;

    /**
     * Public builder class
     */
    public static class Builder {
        private final String mDate;
        private final String mWeatherText;
        private final int mWeatherCode;
        private final String mTemp;

        /**
         * Public builder constructor for ten day forecast
         * @param date weather date
         * @param weatherText weather description
         * @param weatherCode weather code for icon
         * @param weatherTemp weather temp
         */
        public Builder(String date, String weatherText, int weatherCode, String weatherTemp) {
            this.mDate = date;
            this.mWeatherText = weatherText;
            this.mWeatherCode = weatherCode;
            this.mTemp = weatherTemp;
        }

        /**
         * Creates ten day forecast object
         * @return TenDayWeather object
         */
        public TenDayWeather build() {
            return new TenDayWeather(this);
        }
    }

    /**
     * Private constructor called by builder class
     * @param builder from builder class
     */
    private TenDayWeather(final Builder builder) {
        this.mDate = builder.mDate;
        this.mWeatherText = builder.mWeatherText;
        this.mWeatherCode = builder.mWeatherCode;
        this.mTemp = builder.mTemp;
    }

    /**
     * Gets weather date
     * @return weather date
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Gets weather description
     * @return weather description
     */
    public  String getWeatherText() {
        return mWeatherText;
    }

    /**
     * Gets weather code
     * @return weather code for icon
     */
    public int getWeatherCode() {
        return mWeatherCode;
    }

    /**
     * Gets weather temp
     * @return weather temp
     */
    public String getTemp() {
        return mTemp;
    }
}
