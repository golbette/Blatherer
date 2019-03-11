package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.pushy.sdk.Pushy;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Contact;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Message;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.NewContact;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
import team7.tcss450.uw.edu.tcss_750_t7_2.utils.PushReceiver;
import team7.tcss450.uw.edu.tcss_750_t7_2.utils.SendPostAsyncTask;
import team7.tcss450.uw.edu.tcss_750_t7_2.weather.FortyEightHourWeather;
import team7.tcss450.uw.edu.tcss_750_t7_2.weather.SavedLocations;
import team7.tcss450.uw.edu.tcss_750_t7_2.weather.TenDayWeather;

/**
 * Container for fragments after user is successfully
 * logged into messaging app.
 */
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnHomeFragmentInteractionListener,
        LoginFragment.OnLoginFragmentInteractionListener,
        RegisterFragment.OnRegisterFragmentInteractionListener,
        MessageFragment.OnMessageListFragmentInteractionListener,
        NewContactFragment.OnNewContactListFragmentInteractionListener,
        WeatherFragment.OnWeatherFragmentInteractionListener,
        TodayWeatherFragment.OnTodayWeatherFragmentInteractionListener,
        WeatherOptionsFragment.OnWeatherOptionsFragmentInteractionListener,
        MapFragment.OnMapFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener,
        ConversationFragment.OnConversationFragmentInteractionListener,
        ContactFragment.OnContactListFragmentInteractionListener,
        NewContactBlankFragment.OnFragmentInteractionListener{

//    final FragmentManager fm = getSupportFragmentManager();
    private String mJwToken;
    private Credentials mCredentials;
    private PushMessageReceiver mPushMessageReceiver;
    private int mChatId;
    private RecyclerView mRecyclerView;
//    private JSONObject personB;

    private FortyEightHourWeather[] mFortyEightHour;

    private HashMap<String, String> mLocationData;

    private HashMap<String, String> mCurrentObservationData;

    private TenDayWeather[] mTenDay;

    private static final String TAG = "HomeActivity";

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final int MY_PERMISSIONS_LOCATIONS = 8414;

    private LocationRequest mLocationRequest;

    private Location mCurrentLocation;

    private FusedLocationProviderClient mFusedLocationClient;

    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        mJwToken = intent.getStringExtra(getString(R.string.keys_intent_jwt));
        mCredentials = (Credentials) args.getSerializable(getString(R.string.keys_intent_credentials));

//        BroadcastReceiver br = new PushMessageReceiver();
//
//        if (br.onReceive(context, intent).hasExtra("SENDER")) {
//            mUsername = intentMsg.getStringExtra("SENDER");
//        }

        if (savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                if (getIntent().getBooleanExtra(getString(R.string.keys_intent_notification_msg), false)) {
                    mChatId = (int) getIntent().getExtras().getSerializable("chatid");
                    Uri uri = new Uri.Builder().scheme("https")
                            .appendPath(getString(R.string.ep_base_url))
                            .appendPath(getString(R.string.ep_messaging_base))
                            .appendPath(getString(R.string.ep_messaging_getall)).build();

                    JSONObject msg = mCredentials.asJSONObject();
                    try {
                        msg.put("chatid", mChatId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.wtf("CREDS", msg.toString());

                    new SendPostAsyncTask.Builder(uri.toString(), msg)
                            .onPreExecute(this::onWaitFragmentInteractionShow)
                            .onPostExecute(this::handleMessageGetOnPostExecute)
                            .onCancelled(this::handleErrorsInTask)
                            .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                            .build().execute();

//                    ChatFragment chatFragment = new ChatFragment();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, chatFragment).addToBackStack(null).commit();
                } else {
                    loadFragment(new HomeFragment());
                }
            }
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        } else {
            // The user has already allowed the use of Locations. Get the current location.
            requestLocation();
        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    setLocation(location);
                    Log.d("LOCATION UPDATE", location.toString());
                }
            }
        };

        createLocationRequest();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted, yay!
                    requestLocation();
                } else {
                    // permission denied,
                    Log.e("PERMISSION DENIED", "Nothing to see or do here.");

                    // Shuts down app
                    finishAndRemoveTask();
                }
                return;
            }

            // other 'case' lines to check for other permissions
        }
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("REQUEST LOCATION", "User did NOT allow permission to request location!");
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null
                            if (location != null) {
                                setLocation(location);
                                Log.d("LOCATION", location.toString());
                            }
                        }
                    });
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    protected void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void setLocation(final Location location) {
        mCurrentLocation = location;
    }

    /**
     * On start will load the weather
     * for the users current location.
     */
    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Create menu for users.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            loadFragment(new SettingsFragment());
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * handle navigation buttons in drawer
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_home_fragment){

            loadFragment(new HomeFragment());
//            fm.beginTransaction().remove(bottomAppBarFrag).commit();
        } else if (id == R.id.nav_message_activity_home) {
//            Uri uri = new Uri.Builder().scheme("https")
//                    .appendPath(R.string.ep_base_url)
//                    .appendPath(R.string.ep_contacts_base)
//                    .appendPath(R.string.ep_contacts_getcontacts)
//                    .build();
            loadFragment(new MessageFragment());
//            fm.beginTransaction()
//                    .replace(R.id.bottom_frag_container, bottomAppBarFrag)
//                    .addToBackStack(null).commit();
//            fm.popBackStack();

            // Handle the camera action
        } else if (id == R.id.nav_weather_activity_home) {
            //loadFragment(new WeatherFragment());

            /* 98402 is hardcoded, eventually will make default
               location based on device location
             */

            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_weather))
                    .appendPath(getString(R.string.ep_coordinates))
                    .appendQueryParameter("lat", String.valueOf(mCurrentLocation.getLatitude()))
                    .appendQueryParameter("lon", String.valueOf(mCurrentLocation.getLongitude()))
                    .build();
            new GetAsyncTask.Builder(uri.toString())
                    .onPostExecute(this::handleWeatherGetOnPostExecute)
                    .build().execute();

        } else if (id == R.id.nav_starred_weather_locations) {
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_weather))
                    .appendPath(getString(R.string.ep_location))
                    .appendPath(getString(R.string.ep_users))
                    .appendQueryParameter("username", mCredentials.getUsername())
                    .build();
            Log.e("url", uri.toString());
            new GetAsyncTask.Builder(uri.toString())
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleNavigationPreviousSavedLocationOnPost)
                    .build().execute();
        } else if (id == R.id.nav_settings_fragment){
            loadFragment(new SettingsFragment());
        } else if (id == R.id.nav_logout){
            logout();
        } else if (id == R.id.nav_contacts_activity_home) {
            Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_contacts_base))
                    .appendPath(getString(R.string.ep_contacts_getcontacts)).build();

            JSONObject msg = mCredentials.asJSONObject();

            Log.wtf("CREDS", msg.toString());

            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleContactGetOnPostExecute)
                    .onCancelled(this::handleErrorsInTask)
                    .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                    .build().execute();
        } else if (id == R.id.nav_requests_activity_home) {
            loadFragment(new RequestFragment());
        }

//        case R.id.nav_recents:
//
//        return true;

//        case R.id.nav_contacts:
//
//        return true;

//        case R.id.nav_requests:
//        fm.beginTransaction().hide(active).show(requestFrag).commit();
//        active = requestFrag;
//        return true;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * @author Charles Bryan
     * @param frag
     */
    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .addToBackStack(null); //// remove this adding to backstack.
        // Commit the transaction
        transaction.commit();
    }

    /**
     * Logs user our, clears saved credentials, and returns to the Login Screen.
     */
    private void logout() {
        new DeleteTokenAsyncTask().execute();
    }

    @Override
    public void onMessageListFragmentInteraction(Message item) {

    }

    @Override
    public void onNewContactListFragmentInteraction(NewContact item) {

    }

    @Override
    public void onSearchClicked() {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts_base))
                .appendPath(getString(R.string.ep_contacts_searchcontacts)).build();

        JSONObject msg = new JSONObject();
        EditText et = findViewById(R.id.new_contact_et_search);

        if (!et.getText().toString().isEmpty()) {
            try {
                msg.put("input", et.getText().toString());
                msg.put("email", mCredentials.getEmail());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleSearchOnPostExecute)
                    .onCancelled(this::handleErrorsInTask)
                    .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                    .build().execute();
        }
    }

    @Override
    public void onNoResults() { // TODO: Remove this.
    }

    @Override
    public void onRequestSent(String email_b) {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts_base))
                .appendPath("connReq").build();

        JSONObject msg = new JSONObject();
        try {
            msg.put("email_a", mCredentials.getEmail());
            msg.put("email_b", email_b);
        } catch (JSONException e) {
            Log.wtf("JSON", "Error creating JSON: " + e.getMessage());
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleSendConnReq)
                .onCancelled(this::handleErrorsInTask)
                .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                .build().execute();
    }

    private void handleSendConnReq(final String result){
        Log.wtf("SEND_CONNREQ_RESULT", result);
        onWaitFragmentInteractionHide();
        Toast.makeText(this, "Request sent!", Toast.LENGTH_SHORT).show();
    }

    private void handleSearchOnPostExecute(final String result) {
        Log.wtf("SEARCH_RESULT", result);
        try {
            JSONObject response = new JSONObject(result);

            if (response.has(getString(R.string.keys_json_contact_message))) {

                JSONArray data = response.getJSONArray(getString(R.string.keys_json_contact_message));

                List<NewContact> newContacts = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonContact = data.getJSONObject(i);
                    newContacts.add(new NewContact.Builder(jsonContact.getString(getString(R.string.keys_json_contact_first_name)), jsonContact.getString(getString(R.string.keys_json_contact_last_name)))
                            .addEmail(jsonContact.getString(getString(R.string.keys_json_contact_email)))
                            .addUsername(jsonContact.getString(getString(R.string.keys_json_contact_username)))
                            .addMemberId(jsonContact.getInt("memberid"))
                            .build());
                }
                NewContact[] contactsAsArray = new NewContact[newContacts.size()];
                contactsAsArray = newContacts.toArray(contactsAsArray);
                Bundle args = new Bundle();
                args.putSerializable(NewContactFragment.ARG_NEW_CONTACT_LIST, contactsAsArray);
                Fragment frag = new NewContactFragment();
                frag.setArguments(args);
                onWaitFragmentInteractionHide();

                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, frag)
                        .addToBackStack(null);
                transaction.commit();
            } else {
                Log.wtf("ERROR", "no data in array");
                onWaitFragmentInteractionHide();

                NewContactBlankFragment newContactBlankFragment = new NewContactBlankFragment();
                Bundle args = new Bundle();
                args.putSerializable("new_contact_status", "No results.");
                newContactBlankFragment.setArguments(args);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, newContactBlankFragment).addToBackStack(null);
                transaction.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.wtf("ERROR", e.getMessage());
            onWaitFragmentInteractionHide();

            NewContactBlankFragment newContactBlankFragment = new NewContactBlankFragment();
            Bundle args = new Bundle();
            args.putSerializable("new_contact_status", "No results.");
            newContactBlankFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, newContactBlankFragment).addToBackStack(null);
            transaction.commit();
        }
    }

//    @Override
//    public void recentClicked() {
////        Uri uri = new Uri.Builder().scheme().appendPath().appendPath().appendPath().build();
//        fm.beginTransaction()
//                .replace(R.id.fragmentContainer, new MessageFragment())
//                .addToBackStack(null).commit();
//    }

//    @Override
//    public void contactClicked() {
////        Uri uri = new Uri.Builder().scheme().appendPath().appendPath().appendPath().build();
//        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
//                .appendPath(getString(R.string.ep_contacts_base))
//                .appendPath(getString(R.string.ep_contacts_getcontacts)).build();
//
//        JSONObject msg = mCredentials.asJSONObject();
//
//        Log.wtf("CREDS", msg.toString());
//
//        new SendPostAsyncTask.Builder(uri.toString(), msg)
//                .onPreExecute(this::onWaitFragmentInteractionShow)
//                .onPostExecute(this::handleContactGetOnPostExecute)
//                .onCancelled(this::handleErrorsInTask)
//                .addHeaderField("authorization", mJwToken) // Add the JWT as a header
//                .build().execute();
//    }



    private void handleContactGetOnPostExecute(final String result) {
        Log.wtf("CONTACT_RESULT", result);
        try {
            JSONObject response = new JSONObject(result);

            if (response.has(getString(R.string.keys_json_contact_message))) {

                JSONArray data = response.getJSONArray(getString(R.string.keys_json_contact_message));

                List<Contact> contacts = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonContact = data.getJSONObject(i);
                    contacts.add(new Contact.Builder(jsonContact.getString(getString(R.string.keys_json_contact_first_name)), jsonContact.getString(getString(R.string.keys_json_contact_last_name)))
                            .addEmail(jsonContact.getString(getString(R.string.keys_json_contact_email)))
                            .addUsername(jsonContact.getString(getString(R.string.keys_json_contact_username)))
                            .build());
                }
                Contact[] contactsAsArray = new Contact[contacts.size()];
                contactsAsArray = contacts.toArray(contactsAsArray);
                Bundle args = new Bundle();
                args.putSerializable(ContactFragment.ARG_CONTACTS_LIST, contactsAsArray);
                Fragment frag = new ContactFragment();
                frag.setArguments(args);
                onWaitFragmentInteractionHide();

                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, frag)
                        .addToBackStack(null);
                transaction.commit();
            } else {
                Log.wtf("ERROR", "no data in array");
                onWaitFragmentInteractionHide();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.wtf("ERROR", e.getMessage());
            onWaitFragmentInteractionHide();
        }
    }



//    @Override
//    public void requestClicked() {
////        Uri uri = new Uri.Builder().scheme().appendPath().appendPath().appendPath().build();
//        fm.beginTransaction()
//                .replace(R.id.fragmentContainer, new RequestFragment())
//                .addToBackStack(null).commit();
//    }

    @Override
    public void onContactListFragmentInteraction(Contact item) throws JSONException {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_getall)).build();

        JSONObject msg = mCredentials.asJSONObject();
        msg.put("contactemail", item.getEmail());

        Log.wtf("CREDS", msg.toString());

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleMessageGetOnPostExecute)
                .onCancelled(this::handleErrorsInTask)
                .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                .build().execute();

//        Bundle data = new Bundle();
//        data.putSerializable(getString(R.string.contact_tv_contact_initials), item.getContactName());
//        data.putSerializable(getString(R.string.contact_tv_contact_name), item.getInitials());
//        data.putSerializable(getString(R.string.contact_tv_email), item.getEmail());
//        data.putSerializable(getString(R.string.contact_tv_username), item.getmUsername());
//
//        ChatFragment chatFragment = new ChatFragment();
//        chatFragment.setArguments(data);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, chatFragment).addToBackStack(null);
//        transaction.commit();
    }

    @Override
    public void onNewContactClicked() {
        NewContactBlankFragment newContactBlankFragment = new NewContactBlankFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, newContactBlankFragment).addToBackStack(null);
        transaction.commit();
    }

    private void handleMessageGetOnPostExecute(final String result) {
        Log.wtf("MESSAGE_RESULT", result);
        try {
            JSONObject response = new JSONObject(result);
            Bundle args = new Bundle();
            if (response.has("chatid") && response.has("username")){
                args.putSerializable("send_username", (Serializable) response.getString("username"));
                args.putSerializable("send_chat_id", (Serializable) response.getInt("chatid"));
            }
            if (response.has(getString(R.string.keys_json_message_message))) {

                JSONArray data = response.getJSONArray(getString(R.string.keys_json_message_message));

                List<Message> messages = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonMessage = data.getJSONObject(i);
                    messages.add(new Message.Builder(jsonMessage.getString(getString(R.string.keys_json_message_username)),
                            jsonMessage.getString(getString(R.string.keys_json_message_message)),
                            jsonMessage.getString(getString(R.string.keys_json_message_timestamp)))
                            .build());
                }
                Message[] messagesAsArray = new Message[messages.size()];
                messagesAsArray = messages.toArray(messagesAsArray);

                args.putSerializable(MessageFragment.ARG_MESSAGE_LIST, messagesAsArray);

                Fragment frag = new ChatFragment();
                frag.setArguments(args);
                onWaitFragmentInteractionHide();

                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, frag)
                        .addToBackStack(null);
                transaction.commit();
            } else {
                Log.wtf("ERROR", "no data in array");
                onWaitFragmentInteractionHide();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.wtf("ERROR", e.getMessage());
            onWaitFragmentInteractionHide();
        }
    }
//    @Override
//    public void newContactClicked() {
//        fm.beginTransaction()
//                .replace(R.id.fragmentContainer, new NewContactFragment())
//                .addToBackStack(null).commit();
//    }

    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onWaitFragmentInteractionShow();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Since we are already doing stuff in the background, go ahead and remove the credentials from shared prefs here.
            SharedPreferences prefs = getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
            Boolean rememberVal = prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in), false);
            Log.wtf("REMEMBER", rememberVal.toString() + "(logout)");

            if (!rememberVal) {
                prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();
//                prefs.edit().remove(getString(R.string.keys_prefs_username)).apply();
                prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
            }

            // Unregister the device from the Pushy servers
            Pushy.unregister(HomeActivity.this);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Close this activity and bring back the Login
            boolean loggedOutByUser = true;

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.putExtra(getString(R.string.keys_logged_out_by_user), loggedOutByUser);
            startActivity(intent);
            // End this Activity and remove it from the Activity back stack
            finish();
        }

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
                mLocationData = new HashMap<>();
                keys = location.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    String value = location.getString(key);
                    mLocationData.put(key, value);
                }

                //Handle current observation JSONObject
                JSONObject currentObservation = root.getJSONObject(getString(R.string.keys_json_weather_current_observation));
                mCurrentObservationData = new HashMap<>();
                keys = currentObservation.keys();
                while(keys.hasNext()) {
                    String outerKey = (String) keys.next();
                    if (!outerKey.equals("pubDate")) {
                        JSONObject innerObject = currentObservation.getJSONObject(outerKey);
                        Iterator<?> innerKeys = innerObject.keys();
                        while (innerKeys.hasNext()) {
                            String innerKey = (String) innerKeys.next();
                            String value = innerObject.getString(innerKey);
                            mCurrentObservationData.put(outerKey + innerKey, value);
                        }
                    } else {
                        mCurrentObservationData.put(outerKey, currentObservation.getString(outerKey));
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
                mTenDay = new TenDayWeather[tenDay.size()];
                mTenDay = tenDay.toArray(mTenDay);
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
                        .onPostExecute(this::handleHourlyOnPost)
                        .build().execute();


            } else {
                Log.e("ERROR!", "Invalid location entered");

                //notify user
                onWaitFragmentInteractionHide();
            }

        } catch (JSONException error) {
            error.printStackTrace();
            Log.e("ERROR!", error.getMessage());

            //notify user
            onWaitFragmentInteractionHide();
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

                mFortyEightHour = new FortyEightHourWeather[fortyEightHour.size()];
                mFortyEightHour = fortyEightHour.toArray(mFortyEightHour);

//                Bundle args = new Bundle();
//                args.putSerializable(WeatherFragment.ARG_LOCATION, mLocationData);
//                args.putSerializable(WeatherFragment.ARG_CURRENT_OBSERVATION, mCurrentObservationData);
//                args.putSerializable(WeatherFragment.ARG_FORECASTS, mTenDay);
//                args.putSerializable(WeatherFragment.ARG_FORTY_EIGHT_HOUR, mFortyEightHour);
//                Fragment weatherFragment = new WeatherFragment();
//                weatherFragment.setArguments(args);
//                onWaitFragmentInteractionHide();
//                loadFragment(weatherFragment);

                Uri uri = new Uri.Builder()
                        .scheme("https")
                        .appendPath(getString(R.string.ep_base_url))
                        .appendPath(getString(R.string.ep_weather))
                        .appendPath(getString(R.string.ep_location))
                        .appendPath(getString(R.string.ep_users))
                        .appendQueryParameter("username", mCredentials.getUsername())
                        .build();
                Log.e("url", uri.toString());
                new GetAsyncTask.Builder(uri.toString())
                        .onPreExecute(this::onWaitFragmentInteractionShow)
                        .onPostExecute(this::handlePreviousSavedLocationOnPost)
                        .build().execute();
            }
        } catch (JSONException error) {
            error.printStackTrace();
            Log.e("ERROR!", error.getMessage());


        }
    }

    private void handlePreviousSavedLocationOnPost(String result) {
        //parse JSON
        try {
            Bundle args = new Bundle();
            SavedLocations[] savedLocationArray;
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_json_weather_status))) {
                String status = root.getString(getString(R.string.keys_json_weather_status));
                if (status.equals(getString(R.string.keys_json_weather_Success))) {
                    List<SavedLocations> savedLocation = new ArrayList<>();
                    JSONArray data = root.getJSONArray(getString(R.string.keys_json_weather_data));
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject entry = data.getJSONObject(i);
                        savedLocation.add(new SavedLocations.Builder(
                                entry.getString(
                                        getString(R.string.keys_json_weather_nickname)),
                                entry.getString(
                                        getString(R.string.keys_json_weather_lat)),
                                entry.getString(
                                        getString(R.string.keys_json_weather_long)),
                                entry.getString(
                                        getString(R.string.keys_json_weather_zip)))
                                .build());
                    }
                    savedLocationArray = new SavedLocations[savedLocation.size()];
                    savedLocationArray = savedLocation.toArray(savedLocationArray);
                    args.putSerializable(WeatherFragment.ARG_SAVED_LOCATIONS, savedLocationArray);


                }
                args.putSerializable(WeatherFragment.ARG_LOCATION, mLocationData);
                args.putSerializable(WeatherFragment.ARG_CURRENT_OBSERVATION, mCurrentObservationData);
                args.putSerializable(WeatherFragment.ARG_FORECASTS, mTenDay);
                args.putSerializable(WeatherFragment.ARG_FORTY_EIGHT_HOUR, mFortyEightHour);
                args.putSerializable(WeatherFragment.ARG_CREDENTIALS, mCredentials);

                if (mCurrentLocation != null) {
                    args.putParcelable(WeatherFragment.ARG_CURRENT_LOCATION, mCurrentLocation);
                }

                Fragment weatherFragment = new WeatherFragment();

                weatherFragment.setArguments(args);
                onWaitFragmentInteractionHide();
                loadFragment(weatherFragment);


//                if (getSupportFragmentManager().findFragmentByTag("weatherFragment") != null) {
//                    WeatherFragment frag = (WeatherFragment) getSupportFragmentManager().findFragmentByTag("weatherFragment");
//                    Log.e("already exists", "should get arguments" + mLocationData.get("city"));
//                    frag.setArguments(args);
//                    FragmentTransaction transaction = getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.fragmentContainer, frag);
//                    transaction.commit();
//                    //frag.doUpdateLatLon(mLocationData.get("lat"), mLocationData.get("long"));
//
//                } else {
//                    Fragment weatherFragment = new WeatherFragment();
//
//                    weatherFragment.setArguments(args);
//                    FragmentTransaction transaction = getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.fragmentContainer, weatherFragment, "weatherFragment")
//                            .addToBackStack(null);
//                    transaction.commit();
//                }


            }
        } catch (JSONException error) {
            error.printStackTrace();
            Log.e("ERROR!", error.getMessage());
        }
    }

    private void handleNavigationPreviousSavedLocationOnPost(String result) {
        //parse JSON
        try {
            Bundle args = new Bundle();
            SavedLocations[] savedLocationArray;
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_json_weather_status))) {
                String status = root.getString(getString(R.string.keys_json_weather_status));
                if (status.equals(getString(R.string.keys_json_weather_Success))) {
                    List<SavedLocations> savedLocation = new ArrayList<>();
                    JSONArray data = root.getJSONArray(getString(R.string.keys_json_weather_data));
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject entry = data.getJSONObject(i);
                        savedLocation.add(new SavedLocations.Builder(
                                entry.getString(
                                        getString(R.string.keys_json_weather_nickname)),
                                entry.getString(
                                        getString(R.string.keys_json_weather_lat)),
                                entry.getString(
                                        getString(R.string.keys_json_weather_long)),
                                entry.getString(
                                        getString(R.string.keys_json_weather_zip)))
                                .build());
                        Log.e("test", entry.getString("nickname"));
                    }
                    savedLocationArray = new SavedLocations[savedLocation.size()];
                    savedLocationArray = savedLocation.toArray(savedLocationArray);
                    args.putSerializable(WeatherFragment.ARG_SAVED_LOCATIONS, savedLocationArray);


                }
                args.putSerializable(WeatherFragment.ARG_CREDENTIALS, mCredentials);

                Fragment savedWeatherLocationFragment = new SavedWeatherLocationFragment();
                savedWeatherLocationFragment.setArguments(args);
                onWaitFragmentInteractionHide();
                loadFragment(savedWeatherLocationFragment);

            }
        } catch (JSONException error) {
            error.printStackTrace();
            Log.e("ERROR!", error.getMessage());
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
     * Interaction listener for weather fragment that loads
     * on homepage when user is successfully logged in.
     * @param uri
     */
    @Override
    public void onWeatherFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMapFragmentInteraction() {
        if (mCurrentLocation != null) {
            MapFragment fragment = new MapFragment();
            Bundle args = new Bundle();
            args.putParcelable(WeatherFragment.ARG_CURRENT_LOCATION, mCurrentLocation);
            fragment.setArguments(args);
            loadFragment(fragment);
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    ; //// remove this adding to backstack.
            // Commit the transaction
            transaction.commit();
        }

    }

    @Override
    public void onMapLocationSelect(String lat, String lon) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_coordinates))
                .appendQueryParameter("lat", lat)
                .appendQueryParameter("lon", lon)
                .build();
        new GetAsyncTask.Builder(uri.toString())
                .onPostExecute(this::handleWeatherGetOnPostExecute)
                .build().execute();

        //WeatherFragment frag = (WeatherFragment) getSupportFragmentManager().findFragmentByTag("weatherFragment");
        //Log.e("already exists", "should get arguments" + mLocationData.get("city"));
        //frag.setArguments(args);
        //frag.doUpdateLatLon(lat, lon);
//        FragmentTransaction transaction = getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragmentContainer, frag);
//        transaction.commit();
        //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.detach(frag).attach(frag).commit();

    }

    @Override
    public void onLoginSuccess(Credentials credentials, String jwt) {

    }

    @Override
    public void onRegisterClicked() {

    }


    @Override
    public void onRegisterSuccess(Credentials credentials) {

    }


    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }

    /**
     * Weather options fragments listener
     * @param uri
     */
    @Override
    public void onWeatherOptionsFragmentInteraction(Uri uri) {

    }
    /**
     *
     * Weather options fragments listener
     * @param uri
     */
    @Override
    public void onTodayWeatherFragmentInteraction(Uri uri) {

    }

    @Override
    public void onHomeFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSettingsFragmentInteraction(Uri uri) {

    }

    @Override
    public void onConversationFragmentInteraction(Uri uri) {

    }

    private class PushMessageReceiver extends BroadcastReceiver {
        public PushMessageReceiver() {
            // Require constructor
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("CHATID")) {
//                mChatId = intent.getStringExtra("CHATID");
//                String messageText = intent.getStringExtra("MESSAGE");
            }
        }
    }
}
