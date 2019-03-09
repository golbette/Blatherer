package team7.tcss450.uw.edu.tcss_750_t7_2;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
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

import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
import team7.tcss450.uw.edu.tcss_750_t7_2.weather.SavedLocations;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavedWeatherLocationFragment extends Fragment {

    private static final String ARG_SAVED_LOCATION_LIST = "saved location list";

    private int mColumnCount = 1;

    private List<SavedLocations> mSavedLocation;

    private Credentials mCredentials;

    public SavedWeatherLocationFragment() {
        // Required empty public constructor
    }

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
//                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // call http delete here
//
//                        Uri uri = new Uri.Builder()
//                                .scheme("https")
//                                .appendPath(v.getContext().getResources().getString(R.string.ep_base_url))
//                                .appendPath(v.getContext().getResources().getString(R.string.ep_weather))
//                                .appendPath(v.getContext().getResources().getString(R.string.ep_location))
//                                .appendQueryParameter("username", mCredentials.getUsername())
//                                .appendQueryParameter("nickname", mValues.get(position).getNickname())
//                                .build();
//                        Log.e("url", uri.toString());
//                        new DeleteAsyncTask.Builder(uri.toString(), new JSONObject())
//                                .onPostExecute(MySavedWeatherLocationRecyclerViewAdapter.this::handleDeleteOnPost)
//                                .build().execute();
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // cancel
//                    }
//                });
                builder.show();
            }


        }
        return view;
    }

    public void updateFields(SavedLocations[] savedLocations) {
        mSavedLocation = new ArrayList<SavedLocations>(
                Arrays.asList((SavedLocations[]) savedLocations));
    }


}
