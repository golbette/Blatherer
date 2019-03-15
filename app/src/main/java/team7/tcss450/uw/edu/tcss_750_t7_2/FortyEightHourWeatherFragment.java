package team7.tcss450.uw.edu.tcss_750_t7_2;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import team7.tcss450.uw.edu.tcss_750_t7_2.weather.FortyEightHourWeather;
import team7.tcss450.uw.edu.tcss_750_t7_2.weather.TenDayWeather;


/**
 * Hourly weawther fragment
 */
public class FortyEightHourWeatherFragment extends Fragment {

    private static final String ARG_FORTY_EIGHT_HOUR_LIST = "forty eight hour list";

    private int mColumnCount = 1;

    private List<FortyEightHourWeather> mFortyEightHour;

    /**
     * Required empty public constructor
     */
    public FortyEightHourWeatherFragment() {
        // Required empty public constructor
    }

    /**
     * On create to set hourly forecast
     * @param savedInstanceState bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mFortyEightHour = new ArrayList<FortyEightHourWeather>(
                    Arrays.asList((FortyEightHourWeather[]) getArguments().getSerializable(WeatherFragment.ARG_FORTY_EIGHT_HOUR)));
        }
    }

    /**
     * Sets hourly forecast for user to see
     * @param inflater for layout inflation
     * @param container of view group
     * @param savedInstanceState bundle object
     * @return view for reference
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forty_eight_hour_weather_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyFortyEightHourRecyclerViewAdapter(mFortyEightHour));
        }
        return view;
    }

    /**
     * Update fields when new location is searched
     * @param fortyEightHour of new hourly forecast retrieved
     */
    public void updateFields(FortyEightHourWeather[] fortyEightHour) {
        mFortyEightHour = new ArrayList<FortyEightHourWeather>(
                Arrays.asList((FortyEightHourWeather[]) fortyEightHour));
    }

}
