package team7.tcss450.uw.edu.tcss_750_t7_2;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
import team7.tcss450.uw.edu.tcss_750_t7_2.weather.SavedLocations;


/**
 * Saved weather locations fragment
 */
public class SavedWeatherLocationFragment extends Fragment {

    private static final String ARG_SAVED_LOCATION_LIST = "saved location list";

    private int mColumnCount = 1;

    private List<SavedLocations> mSavedLocation;

    private Credentials mCredentials;

    /**
     * Required empty public constructor
     */
    public SavedWeatherLocationFragment() {
        // Required empty public constructor
    }

    /**
     * Retrieves saved locations
     * @param savedInstanceState bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            if (getArguments().containsKey(WeatherFragment.ARG_SAVED_LOCATIONS)) {
                mSavedLocation = new ArrayList<SavedLocations>(
                        Arrays.asList((SavedLocations[]) getArguments().getSerializable(WeatherFragment.ARG_SAVED_LOCATIONS)));
            }

            mCredentials = (Credentials) getArguments().getSerializable(WeatherFragment.ARG_CREDENTIALS);
        }
    }

    /**
     * Sets recycler view based on previous saved locations
     * @param inflater inflates layout
     * @param container view group
     * @param savedInstanceState bundle object
     * @return view for reference
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_weather_location_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            if (mSavedLocation != null) {
                recyclerView.setAdapter(new MySavedWeatherLocationRecyclerViewAdapter(mSavedLocation, getActivity(), mCredentials));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Nothing to see here...\nWhy not try checking out the weather first?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        }
        return view;
    }

    /**
     * Update fields of saved locations
     * @param savedLocations new array of saved locations
     */
    public void updateFields(SavedLocations[] savedLocations) {
        mSavedLocation = new ArrayList<SavedLocations>(
                Arrays.asList((SavedLocations[]) savedLocations));
    }

}
