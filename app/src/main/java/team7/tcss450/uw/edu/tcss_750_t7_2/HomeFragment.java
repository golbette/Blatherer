package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Request;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnHomeFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends Fragment {
    public static final String ARG_CREDS = "credentials";
    public static final String ARG_RECEIVED_REQUEST = "received";
    public static final String ARG_JWT = "jwt";


    private OnHomeFragmentInteractionListener mListener;

    private RequestReceivedListFragment.OnRequestReceivedListFragmentInteractionListener mReceivedListener;
    private List<Request> mReceivedRequests;
    private RecyclerView mReceivedReqRecyclerView;

    private Credentials mCredentials;
    private String mJwtToken;

    private HashMap<String, String> mLocationData;

    private HashMap<String, String> mCurrentObservationData;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstances){
        super.onCreate(savedInstances);
        if(getArguments() != null) {
            mReceivedRequests = (ArrayList<Request>) getArguments()
                    .getSerializable(ARG_RECEIVED_REQUEST);
            mCredentials = (Credentials) getArguments().getSerializable(ARG_CREDS);
            mJwtToken = (String) getArguments().getSerializable(ARG_JWT);
            mLocationData = (HashMap<String, String>) getArguments().getSerializable(WeatherFragment.ARG_CURRENT_LOCATION);
            mCurrentObservationData = (HashMap<String, String>) getArguments().getSerializable(WeatherFragment.ARG_CURRENT_OBSERVATION);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_home, container, false);
        Context context = v.getContext();

        if( null == mReceivedRequests){
         TextView t =   v.findViewById(R.id.text_home_fragment_received_request_recycler_received);
         t.setText("No Connection Requests");
        } else {
            mReceivedReqRecyclerView = v.findViewById(R.id.fragment_home_received_requests);
            RecyclerView.LayoutManager receivedLayoutManager = new LinearLayoutManager(context);
            mReceivedReqRecyclerView.setLayoutManager(receivedLayoutManager);
            MyRequestReceivedRecyclerViewAdapter requestReceivedRecyclerViewAdapter
                    = new MyRequestReceivedRecyclerViewAdapter(mReceivedRequests, mReceivedListener, mCredentials, mJwtToken);

            mReceivedReqRecyclerView.setAdapter(requestReceivedRecyclerViewAdapter);
        }

        TextView city = v.findViewById(R.id.home_weather_city);
        city.setText(mLocationData.get("city") + "," + mLocationData.get("region"));

        TextView temp = v.findViewById(R.id.home_weather_temp);
        temp.setText(mCurrentObservationData.get("conditiontemperature") + "\u2109");

        ImageView weatherIcon = v.findViewById(R.id.home_weather_icon);

        if (contains(TodayWeatherFragment.CLEAR_DAY, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.clear_day);
        } else if (contains(TodayWeatherFragment.CLEAR_NIGHT, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.clear_night);
        } else if(contains(TodayWeatherFragment.CLOUDY, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.cloudy);
        } else if (contains(TodayWeatherFragment.CLOUDY_NIGHT, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.cloudy_night);
        } else if (contains(TodayWeatherFragment.FOG, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.fog);
        } else if (contains(TodayWeatherFragment.PARTLY_CLOUDY, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.partly_cloudy);
        } else if (contains(TodayWeatherFragment.RAIN, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.rain);
        } else if (contains(TodayWeatherFragment.SLEET, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.sleet);
        } else if (contains(TodayWeatherFragment.SNOW, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.snow);
        } else if (contains(TodayWeatherFragment.SUNNY, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.sunny);
        } else if (contains(TodayWeatherFragment.WIND, Integer.parseInt(mCurrentObservationData.get("conditioncode")))) {
            weatherIcon.setImageResource(R.drawable.wind);
        }

        return v;
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentInteractionListener) {
            mListener = (OnHomeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeFragmentInteractionListener");
        }
    }

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
    public interface OnHomeFragmentInteractionListener {
        // TODO: Update argument type and name
        void onHomeFragmentInteraction(Uri uri);
    }
}
