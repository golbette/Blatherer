package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
import team7.tcss450.uw.edu.tcss_750_t7_2.weather.FortyEightHourWeather;
import team7.tcss450.uw.edu.tcss_750_t7_2.weather.SavedLocations;
import team7.tcss450.uw.edu.tcss_750_t7_2.weather.TenDayWeather;


/**
 * Fragment that holds all required information for
 * daily, hourly, and ten day forecasts.
 */
public class WeatherFragment extends Fragment {

    public static final String ARG_LOCATION = "location";

    public static final String ARG_CURRENT_OBSERVATION = "current observation";

    public static final String ARG_FORECASTS = "forecasts";

    public static final String ARG_FORTY_EIGHT_HOUR = "forty eight hour";

    public static final String ARG_SAVED_LOCATIONS = "saved";

    public static final String ARG_CREDENTIALS = "credentials";

    public static final String ARG_CURRENT_LOCATION = "current location";


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Location mCurrentLocation;

    private HashMap<String, String> mLocationData;

    private HashMap<String, String> mCurrentObservationData;

    private TenDayWeather[] mTenDayWeatherData;

    private FortyEightHourWeather[] mFortyEightHourData;

    private SavedLocations[] mSavedLocations;

    private Credentials mCredentials;

    private TodayWeatherFragment mTodayWeatherFragment;

    private TenDayWeatherFragment mTenDayWeatherFragment;

    private FortyEightHourWeatherFragment mFortyEightHourWeatherFragment;

    public static OnWeatherFragmentInteractionListener mListener;

    private View mView;

    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.e("got it", "got something");
            mLocationData = (HashMap<String, String>) getArguments().getSerializable(ARG_LOCATION);
            mCurrentObservationData = (HashMap<String, String>) getArguments().getSerializable(ARG_CURRENT_OBSERVATION);
            mTenDayWeatherData = (TenDayWeather[]) getArguments().getSerializable(ARG_FORECASTS);
            mFortyEightHourData = (FortyEightHourWeather[]) getArguments().getSerializable(ARG_FORTY_EIGHT_HOUR);
            mSavedLocations = (SavedLocations[]) getArguments().getSerializable(ARG_SAVED_LOCATIONS);
            mCredentials = (Credentials) getArguments().getSerializable(ARG_CREDENTIALS);
            if (getArguments().containsKey(WeatherFragment.ARG_CURRENT_LOCATION)) {
                mCurrentLocation = (Location) getArguments().getParcelable(WeatherFragment.ARG_CURRENT_LOCATION);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("resume", "resumed");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_weather, container, false);
        mView = v;


        Log.e("onCreateView", "weatherFragment");
        Bundle args;

        List<String> options = new ArrayList<>();
        options.add("Choose on map...");

        //Add previous saved entries here
        if (mSavedLocations != null) {
            for (int i = 0; i < mSavedLocations.length; i++) {
                options.add(mSavedLocations[i].getNickname());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, options);

        AutoCompleteTextView searchText = v.findViewById(R.id.search_text_view);
        searchText.setAdapter(adapter);
        searchText.setThreshold(1);
        searchText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchText.showDropDown();
                return false;
            }
        });

        searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("click", parent.getItemAtPosition(position).toString() + " " + position);
                if (parent.getItemAtPosition(position).toString().equals("Choose on map...")) {

                    mListener.onMapFragmentInteraction();
                } else {
                    SavedLocations selected = mSavedLocations[adapter.getPosition(parent.getItemAtPosition(position).toString()) - 1];

                    if (selected.getLat().equals("null") && selected.getLong().equals("null")) {

                        Log.e("update", "shoould update");
                        doUpdate(selected.getZip());

                    } else {
                        doUpdateLatLon(selected.getLat(), selected.getLong());

                    }
                }

                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    if (v.getText().toString().isEmpty()) {
                        Toast toast = Toast.makeText(getActivity(), "Please type in a valid search entry", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        doUpdate(v.getText().toString());
                    }
                }
                return false;
            }
        });

        Button searchButton = v.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchText.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(getActivity(), "Please type in a valid search entry", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    doUpdate(searchText.getText().toString());

                }
            }
        });




        mTodayWeatherFragment = new TodayWeatherFragment();
        args = new Bundle();
        args.putSerializable(ARG_LOCATION, mLocationData);
        args.putSerializable(ARG_CURRENT_OBSERVATION, mCurrentObservationData);
        args.putSerializable(ARG_CREDENTIALS, mCredentials);
        mTodayWeatherFragment.setArguments(args);

        mFortyEightHourWeatherFragment = new FortyEightHourWeatherFragment();
        args = new Bundle();
        args.putSerializable(ARG_FORTY_EIGHT_HOUR, mFortyEightHourData);
        mFortyEightHourWeatherFragment.setArguments(args);

        mTenDayWeatherFragment = new TenDayWeatherFragment();
        args = new Bundle();
        args.putSerializable(ARG_FORECASTS, mTenDayWeatherData);
        mTenDayWeatherFragment.setArguments(args);



        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        return v;
    }
    private void updateFields() {
        Bundle args;
        args = new Bundle();
        args.putSerializable(ARG_LOCATION, mLocationData);
        for (int i = 0; i < mTenDayWeatherData.length; i++) {
            Log.e("update", mTenDayWeatherData[i].getWeatherText());
        }

        args.putSerializable(ARG_CURRENT_OBSERVATION, mCurrentObservationData);
        mTodayWeatherFragment.updateFields(mLocationData, mCurrentObservationData);
        args = new Bundle();
        args.putSerializable(ARG_FORECASTS, mTenDayWeatherData);
        mTenDayWeatherFragment.updateFields(mTenDayWeatherData);

        args = new Bundle();
        args.putSerializable(ARG_FORTY_EIGHT_HOUR, mFortyEightHourData);
        mFortyEightHourWeatherFragment.updateFields(mFortyEightHourData);

        ViewPager viewPager = (ViewPager) mView.findViewById(R.id.viewpager);
        Log.e("update", viewPager.toString());
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) mView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(mTodayWeatherFragment, "Today");
        adapter.addFragment(mFortyEightHourWeatherFragment, "Hourly");
        adapter.addFragment(mTenDayWeatherFragment, "10 days");

        viewPager.setAdapter(adapter);
        viewPager.getAdapter().notifyDataSetChanged();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void doUpdate(final String newLocation) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_location))
                .appendQueryParameter(getString(R.string.ep_location), newLocation)
                .build();
        new GetAsyncTask.Builder(uri.toString())
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleWeatherGetOnPostExecute)
                .build().execute();
    }

    public void doUpdateLatLon(final String lat, final String lon) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_coordinates))
                .appendQueryParameter("lat", lat)
                .appendQueryParameter("lon", lon)
                .build();
        new GetAsyncTask.Builder(uri.toString())
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleWeatherGetOnPostExecute)
                .build().execute();
    }

    private void onWaitFragmentInteractionShow() {
        mListener.onWaitFragmentInteractionShow();
    }

    private void handleWeatherGetOnPostExecute(final String result) {
        //parse JSON
        try {
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_json_weather_location))
                    && root.getJSONObject(getString(R.string.keys_json_weather_location)).length() != 0) {
                Iterator<?> keys;
                //Handle location JSONObject
                JSONObject location = root.getJSONObject(getString(R.string.keys_json_weather_location));
                HashMap<String, String> locationData = new HashMap<>();
                keys = location.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    String value = location.getString(key);
                    locationData.put(key, value);
                }

                //Handle current observation JSONObject
                JSONObject currentObservation = root.getJSONObject(getString(R.string.keys_json_weather_current_observation));
                HashMap<String, String> currentObservationData = new HashMap<>();
                keys = currentObservation.keys();
                while(keys.hasNext()) {
                    String outerKey = (String) keys.next();
                    if (!outerKey.equals("pubDate")) {
                        JSONObject innerObject = currentObservation.getJSONObject(outerKey);
                        Iterator<?> innerKeys = innerObject.keys();
                        while (innerKeys.hasNext()) {
                            String innerKey = (String) innerKeys.next();
                            String value = innerObject.getString(innerKey);
                            currentObservationData.put(outerKey + innerKey, value);
                        }
                    } else {
                        currentObservationData.put(outerKey, currentObservation.getString(outerKey));
                    }
                }
                //public Builder(String date, String weatherText, int weatherCode, String weatherTemp)
                //Handle forecasts JSONObject
                JSONArray forecasts = root.getJSONArray(getString(R.string.keys_json_weather_forecasts));
                List<TenDayWeather> tenDay = new ArrayList<>();
                for (int i = 0; i < forecasts.length(); i++) {
                    JSONObject weather = forecasts.getJSONObject(i);
                    tenDay.add(new TenDayWeather.Builder(
                            weather.getString(
                                    getString(R.string.keys_json_weather_date)),
                            weather.getString(
                                    getString(R.string.keys_json_weather_text)
                            ),
                            Integer.parseInt(weather.getString(
                                    getString(R.string.keys_json_weather_code)
                            )),
                            weather.getString(
                                    getString(R.string.keys_json_weather_high)
                            ) + "\u00B0"
                                    +  "\n"
                                    + weather.getString(
                                    getString(R.string.keys_json_weather_low)
                            ) + "\u00B0")
                            .build());
                }
                TenDayWeather[] tenDayAsArray = new TenDayWeather[tenDay.size()];
                tenDayAsArray = tenDay.toArray(tenDayAsArray);

                mListener.onWaitFragmentInteractionHide();
                mLocationData = locationData;
                mCurrentObservationData = currentObservationData;
                mTenDayWeatherData = tenDayAsArray;

                String lat = mLocationData.get("lat");
                String lon = mLocationData.get("long");
                Uri uri = new Uri.Builder()
                        .scheme("https")
                        .appendPath(getString(R.string.ep_base_url))
                        .appendPath(getString(R.string.ep_weather))
                        .appendPath(getString(R.string.ep_hourly))
                        .appendQueryParameter("lat", lat)
                        .appendQueryParameter("lon", lon)
                        .build();
                Log.e("url", uri.toString());
                new GetAsyncTask.Builder(uri.toString())
                        .onPreExecute(this::onWaitFragmentInteractionShow)
                        .onPostExecute(this::handleHourlyOnPost)
                        .build().execute();

            } else {
                Log.e("ERROR!", "Invalid location entered");
                Toast toast = Toast.makeText(getActivity(), "Invalid location entered", Toast.LENGTH_LONG);
                toast.show();
                //notify user
                mListener.onWaitFragmentInteractionHide();
            }

        } catch (JSONException error) {
            error.printStackTrace();
            Log.e("ERROR!", error.getMessage());
            Toast toast = Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG);
            toast.show();
            //notify user
            mListener.onWaitFragmentInteractionHide();
        }
    }

    private void handleHourlyOnPost(String result) {
        //parse JSON
        try {
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_json_weather_hourly))) {
                JSONObject hourly = root.getJSONObject(getString(R.string.keys_json_weather_hourly));
                JSONArray data = hourly.getJSONArray(getString(R.string.keys_json_weather_data));
                List<FortyEightHourWeather> fortyEightHour = new ArrayList<>();
                //public Builder(String date, String weatherText, String weatherCode, String weatherTemp) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject hour = data.getJSONObject(i);
                    fortyEightHour.add(new FortyEightHourWeather.Builder(
                            hour.getString(
                                    getString(R.string.keys_json_weather_time)
                            ),
                            hour.getString(
                                    getString(R.string.keys_json_weather_summary)
                            ),
                            hour.getString(
                                    getString(R.string.keys_json_weather_icon)
                            ),
                            hour.getString(
                                    getString(R.string.keys_json_weather_temperature)
                            )
                    ).build());
                }
                for (int i = 0; i < fortyEightHour.size(); i++) {
                    Log.e("check", fortyEightHour.get(i).getTemp());
                }
                mFortyEightHourData = new FortyEightHourWeather[fortyEightHour.size()];
                mFortyEightHourData = fortyEightHour.toArray(mFortyEightHourData);
                mListener.onWaitFragmentInteractionHide();
                updateFields();
            }
        } catch (JSONException error) {
            error.printStackTrace();
            Log.e("ERROR!", error.getMessage());


        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onWeatherFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWeatherFragmentInteractionListener) {
            mListener = (OnWeatherFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWeatherFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
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
    public interface OnWeatherFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onWeatherFragmentInteraction(Uri uri);
        void onMapFragmentInteraction();
    }
}
