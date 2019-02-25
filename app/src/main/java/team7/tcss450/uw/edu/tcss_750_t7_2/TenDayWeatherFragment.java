package team7.tcss450.uw.edu.tcss_750_t7_2;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import team7.tcss450.uw.edu.tcss_750_t7_2.weather.TenDayWeather;


/**
 * A simple {@link Fragment} subclass.
 */
public class TenDayWeatherFragment extends Fragment {

    private static final String ARG_TEN_DAY_LIST = "ten day list";

    private int mColumnCount = 1;

    private List<TenDayWeather> mTenDay;

    public TenDayWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mTenDay = new ArrayList<TenDayWeather>(
                    Arrays.asList((TenDayWeather[]) getArguments().getSerializable(WeatherFragment.ARG_FORECASTS)));
        }
    }

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

    public void updateFields(TenDayWeather[] tenDay) {
        mTenDay = new ArrayList<TenDayWeather>(
                Arrays.asList((TenDayWeather[]) tenDay));
    }
}
