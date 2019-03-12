package team7.tcss450.uw.edu.tcss_750_t7_2;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Location mCurrentLocation;

    private Marker mMarker;

    private OnMapFragmentInteractionListener mListener;

    private LatLng mPosition;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        FloatingActionButton search = view.findViewById(R.id.map_fab);
        search.setEnabled(false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            mCurrentLocation = (Location) getArguments().getParcelable(WeatherFragment.ARG_CURRENT_LOCATION);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near from the current location
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Toast toast = Toast.makeText(getActivity(), "Click anywhere on map to search weather at dropped marker", Toast.LENGTH_LONG);
        toast.show();

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("LAT/LONG", latLng.toString());
//                Marker marker = mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .title("New Marker"));
                mPosition = latLng;
                String title = "";
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    List<Address> allAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (allAddresses.size() > 0) {
                        Address address = allAddresses.get(0);
                        if (address.getLocality() != null) {
                            title = address.getLocality();
                            title = title + ", " + address.getAdminArea();
                        } else {
                            title = address.getLatitude() + ", " + address.getLongitude();
                        }

                    }
                } catch (IOException error) {
                    Log.e("error", error.getMessage());
                }
                MarkerOptions test = new MarkerOptions().position(latLng).title(title);

                if (mMarker != null) {
                    mMarker.setPosition(latLng);
                    mMarker.setTitle(title);
                } else {
                    mMarker = mMap.addMarker(test);
                }


                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                mMap.setOnMapClickListener(this);

                FloatingActionButton search = getActivity().findViewById(R.id.map_fab);
                search.setEnabled(true);
                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mMarker.getPosition().
                        mListener.onMapLocationSelect(String.valueOf(latLng.latitude),
                                String.valueOf(latLng.longitude));
                    }
                });

            }
        });
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng current = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
        //Zoom levels are from 2.0f to 21.f zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15.0f));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMapFragmentInteractionListener) {
            mListener = (OnMapFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMapFragmentInteractionListener");
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
    public interface OnMapFragmentInteractionListener  {
        // TODO: Update argument type and name

        void onMapLocationSelect(String lat, String lon);
    }
}
