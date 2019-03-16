package team7.tcss450.uw.edu.tcss_750_t7_2.weather;

import java.io.Serializable;

/**
 * Builder design class for hourly weather forecast
 */
public class FortyEightHourWeather implements Serializable{
    private final String mDate;
    private final String mWeatherText;
    private final String mWeatherCode;
    private final String mTemp;

    /**
     * Class to build a single hourly forecast
     */
    public static class Builder {
        private final String mDate;
        private final String mWeatherText;
        private final String mWeatherCode;
        private final String mTemp;

        /**
         * Constructs a hour forecast
         * @param date for time
         * @param weatherText for weather description
         * @param weatherCode for icon
         * @param weatherTemp for temperature
         */
        public Builder(String date, String weatherText, String weatherCode, String weatherTemp) {
            this.mDate = date;
            this.mWeatherText = weatherText;
            this.mWeatherCode = weatherCode;
            this.mTemp = weatherTemp;
        }

        /**
         * Builds the object for use
         * @return hour object
         */
        public FortyEightHourWeather build() {
            return new FortyEightHourWeather(this);
        }
    }

    /**
     * Private constructor thats called from builder class
     * @param builder from public class
     */
    private FortyEightHourWeather(final Builder builder) {
        this.mDate = builder.mDate;
        this.mWeatherText = builder.mWeatherText;
        this.mWeatherCode = builder.mWeatherCode;
        this.mTemp = builder.mTemp;
    }

    /**
     * Gets the weather date
     * @return date of weather
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Gets the weather text description
     * @return weather description
     */
    public  String getWeatherText() {
        return mWeatherText;
    }

    /**
     * Gets weather code
     * @return weather code for icon
     */
    public String getWeatherCode() {
        return mWeatherCode;
    }

    /**
     * Gets the weather temp
     * @return weather temp
     */
    public String getTemp() {
        return mTemp;
    }
}
