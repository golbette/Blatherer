package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.pushy.sdk.Pushy;
import team7.tcss450.uw.edu.tcss_750_t7_2.dummy.DummyContent;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Contact;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Message;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.NewContact;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
import team7.tcss450.uw.edu.tcss_750_t7_2.utils.SendPostAsyncTask;
import team7.tcss450.uw.edu.tcss_750_t7_2.weather.FortyEightHourWeather;
import team7.tcss450.uw.edu.tcss_750_t7_2.weather.TenDayWeather;
import team7.tcss450.uw.edu.tcss_750_t7_2.utils.SendPostAsyncTask;

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
        WeatherOptionsFragment.OnWeatherOptionsFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener,
        ConversationFragment.OnConversationFragmentInteractionListener,
        BottomAppBarFragment.OnBottomNavFragmentInteractionListener,
        ContactFragment.OnContactListFragmentInteractionListener{

    final Fragment bottomAppBarFrag = new BottomAppBarFragment();
    final FragmentManager fm = getSupportFragmentManager();
    private String mJwToken;
    private Credentials mCredentials;
    private PushMessageReceiver mPushMessageReceiver;
    private String mUsername;
    private JSONObject personB;

    private FortyEightHourWeather[] mFortyEightHour;

    private HashMap<String, String> mLocationData;

    private HashMap<String, String> mCurrentObservationData;

    private TenDayWeather[] mTenDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

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

        if (savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                if (getIntent().getBooleanExtra(getString(R.string.keys_intent_notification_msg), false)) {
                    Uri uri = new Uri.Builder().scheme("https")
                            .appendPath(getString(R.string.ep_base_url))
                            .appendPath(getString(R.string.ep_messaging_base))
                            .appendPath(getString(R.string.ep_messaging_getall)).build();

                    JSONObject msg = mCredentials.asJSONObject();
                    try {
                        msg.put("username", mUsername);
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
            fm.beginTransaction().remove(bottomAppBarFrag).commit();
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
                    .appendPath(getString(R.string.ep_location))
                    .appendQueryParameter(getString(R.string.ep_location), "98402")
                    .build();
            new GetAsyncTask.Builder(uri.toString())
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleWeatherGetOnPostExecute)
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
    public void recentClicked() {
//        Uri uri = new Uri.Builder().scheme().appendPath().appendPath().appendPath().build();
        fm.beginTransaction()
                .replace(R.id.fragmentContainer, new MessageFragment())
                .addToBackStack(null).commit();
    }

    @Override
    public void contactClicked() {
//        Uri uri = new Uri.Builder().scheme().appendPath().appendPath().appendPath().build();
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
    }



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



    @Override
    public void requestClicked() {
//        Uri uri = new Uri.Builder().scheme().appendPath().appendPath().appendPath().build();
        fm.beginTransaction()
                .replace(R.id.fragmentContainer, new RequestFragment())
                .addToBackStack(null).commit();
    }

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
                        .onPreExecute(this::onWaitFragmentInteractionShow)
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

                Bundle args = new Bundle();
                args.putSerializable(WeatherFragment.ARG_LOCATION, mLocationData);
                args.putSerializable(WeatherFragment.ARG_CURRENT_OBSERVATION, mCurrentObservationData);
                args.putSerializable(WeatherFragment.ARG_FORECASTS, mTenDay);
                args.putSerializable(WeatherFragment.ARG_FORTY_EIGHT_HOUR, mFortyEightHour);
                Fragment weatherFragment = new WeatherFragment();
                weatherFragment.setArguments(args);
                onWaitFragmentInteractionHide();
                loadFragment(weatherFragment);

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
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("SENDER") && intent.hasExtra("MESSAGE")) {
                mUsername = intent.getStringExtra("SENDER");
//                String messageText = intent.getStringExtra("MESSAGE");
            }
        }
    }
}
