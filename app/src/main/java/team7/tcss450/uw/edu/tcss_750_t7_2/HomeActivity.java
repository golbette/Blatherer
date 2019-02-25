package team7.tcss450.uw.edu.tcss_750_t7_2;

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

import java.util.ArrayList;
import java.util.List;

import me.pushy.sdk.Pushy;
import team7.tcss450.uw.edu.tcss_750_t7_2.dummy.DummyContent;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Contact;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Message;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.NewContact;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
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
    private JSONObject personB;

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
                    ChatFragment chatFragment = new ChatFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, chatFragment).addToBackStack(null).commit();
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
            loadFragment(new WeatherFragment());
        } else if (id == R.id.nav_settings_fragment){
            loadFragment(new SettingsFragment());
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

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provided from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR", result);
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



        Bundle data = new Bundle();
        data.putSerializable(getString(R.string.contact_tv_contact_initials), item.getContactName());
        data.putSerializable(getString(R.string.contact_tv_contact_name), item.getInitials());
        data.putSerializable(getString(R.string.contact_tv_email), item.getEmail());
        data.putSerializable(getString(R.string.contact_tv_username), item.getmUsername());

        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(data);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, chatFragment).addToBackStack(null);
        transaction.commit();
    }

    private void handleMessageGetOnPostExecute(final String result) {
        Log.wtf("CONTACT_RESULT", result);
        try {
            JSONObject response = new JSONObject(result);

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
                Bundle args = new Bundle();
                args.putSerializable(ContactFragment.ARG_CONTACTS_LIST, messagesAsArray);

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
}
