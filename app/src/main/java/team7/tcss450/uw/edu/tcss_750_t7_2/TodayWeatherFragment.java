package team7.tcss450.uw.edu.tcss_750_t7_2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayWeatherFragment extends Fragment {

    private HashMap<String, String> mLocationData;

    private HashMap<String, String> mCurrentObservationData;

    public static final int[] CLEAR_DAY = new int[]{25, 34, 36};;

    public static final String[] CLEAR_DAY_STRING = new String[]{"clear-day"};

    public static final int[] CLEAR_NIGHT = new int[]{31, 33};

    public static final String[] CLEAR_NIGHT_STRING = new String[]{"clear-night"};

    public static final int[] CLOUDY = new int[]{26};

    public static final String[] CLOUDY_STRING = new String[]{"cloudy"};

    public static final int[] CLOUDY_NIGHT = new int[]{27, 29};;

    public static final String[] CLOUDY_NIGHT_STRING = new String[]{"partly-cloudy-night"};

    public static final int[] FOG = new int[]{20, 21, 22};

    public static final String[] FOG_STRING = new String[]{"fog"};

    public static final int[] PARTLY_CLOUDY = new int[]{28, 30, 44};

    public static final String[] PARTLY_CLOUDY_STRING = new String[]{"partly-cloudy-day"};

    public static final int[] RAIN = new int[]{1, 2, 3, 4, 9, 11, 12, 37, 38, 39, 40, 45, 47};

    public static final String[] RAIN_STRING = new String[]{"rain", "thunderstorm"};

    public static final int[] SLEET = new int[]{5, 6, 7, 8, 10, 17, 18, 35};

    public static final String[] SLEET_STRING = new String[]{"sleet", "hail"};

    public static final int[] SNOW = new int[]{13, 14, 15, 16, 41, 42, 43, 46};;

    public static final String[] SNOW_STRING = new String[]{"snow"};

    public static final int[] SUNNY = new int[]{32};

    public static final String[] SUNNY_STRING = new String[]{};

    public static final int[] WIND = new int[]{0, 19, 23, 24};

    public static final String[] WIND_STRING = new String[]{"wind", "tornado"};

    private View mView;

    public TodayWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mLocationData = (HashMap<String, String>) getArguments().getSerializable(WeatherFragment.ARG_LOCATION);
            mCurrentObservationData = (HashMap<String, String>) getArguments().getSerializable(WeatherFragment.ARG_CURRENT_OBSERVATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_today_weather, container, false);
        TextView sunsetData = mView.findViewById(R.id.sunset_data);
        sunsetData.setText(mCurrentObservationData.get("astronomysunset"));
        TextView windSpeedData = mView.findViewById(R.id.wind_speed_data);
        windSpeedData.setText(mCurrentObservationData.get("windspeed") + " mph");
        TextView windDirectionData = mView.findViewById(R.id.wind_direction_data);
        windDirectionData.setText(mCurrentObservationData.get("winddirection") + "\u00B0");
        TextView visibilityData = mView.findViewById(R.id.visibility_data);
        visibilityData.setText(mCurrentObservationData.get("atmospherevisibility") + " mi");
        TextView humidityData = mView.findViewById(R.id.humidity_data);
        humidityData.setText(mCurrentObservationData.get("atmospherehumidity") + "%");
        TextView pressureData = mView.findViewById(R.id.pressure_data);
        pressureData.setText(mCurrentObservationData.get("atmospherepressure") + " inHg");
        TextView sunriseData = mView.findViewById(R.id.sunrise_data);
        sunriseData.setText(mCurrentObservationData.get("astronomysunrise"));
        TextView windChillData = mView.findViewById(R.id.wind_chill_data);
        windChillData.setText(mCurrentObservationData.get("windchill"));

        TextView cityState = mView.findViewById(R.id.city_state_data);
        cityState.setText(mLocationData.get("city") +", " + mLocationData.get("region"));

        TextView monthDate = mView.findViewById(R.id.date_data);
        int timeStamp = Integer.parseInt(mCurrentObservationData.get("pubDate"));
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d");
        simpleDateFormat.setTimeZone(cal.getTimeZone());
        monthDate.setText(simpleDateFormat.format(cal.getTime()));

        TextView currentTemp = mView.findViewById(R.id.temp_data);
        currentTemp.setText(mCurrentObservationData.get("conditiontemperature") + "\u2109");

        TextView weatherDescription = mView.findViewById(R.id.weather_text_data);
        weatherDescription.setText(mCurrentObservationData.get("conditiontext"));

        ImageView weatherIcon = mView.findViewById(R.id.weather_icon_data);
        if (contains(CLEAR_DAY, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.clear_day);
        } else if (contains(CLEAR_NIGHT, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.clear_night);
        } else if(contains(CLOUDY, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.cloudy);
        } else if (contains(CLOUDY_NIGHT, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.cloudy_night);
        } else if (contains(FOG, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.fog);
        } else if (contains(PARTLY_CLOUDY, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.partly_cloudy);
        } else if (contains(RAIN, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.rain);
        } else if (contains(SLEET, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.sleet);
        } else if (contains(SNOW, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.snow);
        } else if (contains(SUNNY, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.sunny);
        } else if (contains(WIND, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.wind);
        }
        return mView;
    }

    public void updateFields(HashMap<String, String> locationData,HashMap<String, String> currentObservationData) {
        mLocationData = locationData;
        mCurrentObservationData = currentObservationData;

        TextView sunsetData = mView.findViewById(R.id.sunset_data);
        sunsetData.setText(mCurrentObservationData.get("astronomysunset"));
        TextView windSpeedData = mView.findViewById(R.id.wind_speed_data);
        windSpeedData.setText(mCurrentObservationData.get("windspeed") + " mph");
        TextView windDirectionData = mView.findViewById(R.id.wind_direction_data);
        windDirectionData.setText(mCurrentObservationData.get("winddirection") + "\u00B0");
        TextView visibilityData = mView.findViewById(R.id.visibility_data);
        visibilityData.setText(mCurrentObservationData.get("atmospherevisibility") + " mi");
        TextView humidityData = mView.findViewById(R.id.humidity_data);
        humidityData.setText(mCurrentObservationData.get("atmospherehumidity") + "%");
        TextView pressureData = mView.findViewById(R.id.pressure_data);
        pressureData.setText(mCurrentObservationData.get("atmospherepressure") + " inHg");
        TextView sunriseData = mView.findViewById(R.id.sunrise_data);
        sunriseData.setText(mCurrentObservationData.get("astronomysunrise"));
        TextView windChillData = mView.findViewById(R.id.wind_chill_data);
        windChillData.setText(mCurrentObservationData.get("windchill"));

        TextView cityState = mView.findViewById(R.id.city_state_data);
        cityState.setText(mLocationData.get("city") +", " + mLocationData.get("region"));

        TextView monthDate = mView.findViewById(R.id.date_data);
        int timeStamp = Integer.parseInt(mCurrentObservationData.get("pubDate"));
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d");
        simpleDateFormat.setTimeZone(cal.getTimeZone());
        monthDate.setText(simpleDateFormat.format(cal.getTime()));

        TextView currentTemp = mView.findViewById(R.id.temp_data);
        currentTemp.setText(mCurrentObservationData.get("conditiontemperature") + "\u2109");

        TextView weatherDescription = mView.findViewById(R.id.weather_text_data);
        weatherDescription.setText(mCurrentObservationData.get("conditiontext"));

        ImageView weatherIcon = mView.findViewById(R.id.weather_icon_data);
        if (contains(CLEAR_DAY, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.clear_day);
        } else if (contains(CLEAR_NIGHT, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.clear_night);
        } else if(contains(CLOUDY, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.cloudy);
        } else if (contains(CLOUDY_NIGHT, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.cloudy_night);
        } else if (contains(FOG, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.fog);
        } else if (contains(PARTLY_CLOUDY, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.partly_cloudy);
        } else if (contains(RAIN, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.rain);
        } else if (contains(SLEET, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.sleet);
        } else if (contains(SNOW, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.snow);
        } else if (contains(SUNNY, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.sunny);
        } else if (contains(WIND, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.wind);
        }
    }
    private boolean contains(final int[] array, final int target) {
        boolean result = false;
        for (int i : array) {
            if (i == target) {
                result = true;
                break;
            }
        }
        return result;
    }

}
