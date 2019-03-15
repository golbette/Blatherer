package team7.tcss450.uw.edu.tcss_750_t7_2;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
import team7.tcss450.uw.edu.tcss_750_t7_2.utils.PutAsyncTask;


/**
 * The current weather fragment class.
 */
public class TodayWeatherFragment extends Fragment {

    public static OnTodayWeatherFragmentInteractionListener mListener;

    private HashMap<String, String> mLocationData;

    private HashMap<String, String> mCurrentObservationData;

    private Credentials mCredentials;

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

    /**
     * Required empty public constructor
     */
    public TodayWeatherFragment() {
        // Required empty public constructor
    }

    /**
     * On create method for current weather details
     * @param savedInstanceState bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mLocationData = (HashMap<String, String>) getArguments().getSerializable(WeatherFragment.ARG_LOCATION);
            mCurrentObservationData = (HashMap<String, String>) getArguments().getSerializable(WeatherFragment.ARG_CURRENT_OBSERVATION);
            mCredentials = (Credentials) getArguments().getSerializable(WeatherFragment.ARG_CREDENTIALS);
        }
    }

    /**
     * Sets the required weather info for user
     * @param inflater inflates layout
     * @param container view group
     * @param savedInstanceState bundle object
     * @return view for fragment reuse
     */
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
        cityState.setText(mLocationData.get("city") +"," + mLocationData.get("region"));

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



        FloatingActionButton fab = mView.findViewById(R.id.save_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.nickname_popup, null);

                EditText nicknameText = view.findViewById(R.id.nickname_text);
                nicknameText.setText(mLocationData.get("city") + "," + mLocationData.get("region"));

                PopupWindow window = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.showAtLocation(mView.findViewById(R.id.constraintLayout), Gravity.CENTER, 0, 0);
                window.setFocusable(true);
                window.update();

                Button saveButton = view.findViewById(R.id.save_button);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        doSave(nicknameText.getText().toString());

                    }
                });
            }
        });

        return mView;
    }

    /**
     * Calls async task to save weather location
     * @param nickname requested nickname to save location
     */
    private void doSave(String nickname) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_coordinates))
                .appendQueryParameter("username", mCredentials.getUsername())
                .appendQueryParameter("lat", mLocationData.get("lat"))
                .appendQueryParameter("lon", mLocationData.get("long"))
                .appendQueryParameter("nickname", nickname)
                .build();
        new PutAsyncTask.Builder(uri.toString(), new JSONObject())
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleSaveOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Handles after save call is made
     * @param result JSON object from backend
     */
    private void handleSaveOnPost(final String result) {
        //parse JSON
        try {
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_json_weather_success))
                    && root.getBoolean(getString(R.string.keys_json_weather_success)) == true) {
                Toast toast = Toast.makeText(getActivity(), "Location saved", Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getActivity(), "Location failed to save (nickname already exists)", Toast.LENGTH_LONG);
                toast.show();
            }
            onWaitFragmentInteractionHide();
        } catch (JSONException error) {
            error.printStackTrace();
            Log.e("ERROR!", error.getMessage());
            Toast toast = Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG);
            toast.show();
            //notify user
            mListener.onWaitFragmentInteractionHide();
        }
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR", result);
    }

    /**
     * Updates required fields when user changes weather location
     * @param locationData hash map of location information
     * @param currentObservationData hash map of weather information based on location
     */
    public void updateFields(HashMap<String, String> locationData,HashMap<String, String> currentObservationData) {
        mLocationData = locationData;
        mCurrentObservationData = currentObservationData;
    }

    /**
     * Searches if the target is in the array
     * @param array to be searched
     * @param target to be found
     * @return boolean if target is found
     */
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

    /**
     * Shows wait fragment
     */
    private void onWaitFragmentInteractionShow() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Hides wait fragment
     */
    private void onWaitFragmentInteractionHide() {
        mListener.onWaitFragmentInteractionHide();
    }

    /**
     * Handles listener
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTodayWeatherFragmentInteractionListener) {
            mListener = (OnTodayWeatherFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWeatherFragmentInteractionListener");
        }
    }

    /**
     * Overrides on detach
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTodayWeatherFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener {
        void onTodayWeatherFragmentInteraction(Uri uri);
    }
}
