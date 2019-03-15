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

import team7.tcss450.uw.edu.tcss_750_t7_2.weather.TenDayWeather;


/**
 * Fragment for ten day weather
 */
public class TenDayWeatherFragment extends Fragment {

    private static final String ARG_TEN_DAY_LIST = "ten day list";

    private int mColumnCount = 1;

    private List<TenDayWeather> mTenDay;

    /**
     * Required empty public constructor
     */
    public TenDayWeatherFragment() {
        // Required empty public constructor
    }

    /**
     * On create to set ten day weather
     * @param savedInstanceState bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mTenDay = new ArrayList<TenDayWeather>(
                    Arrays.asList((TenDayWeather[]) getArguments().getSerializable(WeatherFragment.ARG_FORECASTS)));
        }
    }

    /**
     * Sets recycler view based on ten day forecast
     * @param inflater inflates layout
     * @param container view group container
     * @param savedInstanceState bundle object
     * @return view for reference
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ten_day_weather_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyTenDayRecyclerViewAdapter(mTenDay));
        }
        return view;
    }

    /**
     * Updates ten day forecast
     * @param tenDay new ten day forecast infromation
     */
    public void updateFields(TenDayWeather[] tenDay) {
        mTenDay = new ArrayList<TenDayWeather>(
                Arrays.asList((TenDayWeather[]) tenDay));
    }
}
