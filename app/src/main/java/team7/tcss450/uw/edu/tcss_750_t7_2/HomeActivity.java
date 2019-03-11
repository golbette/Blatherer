package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.pushy.sdk.Pushy;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.ChatCount;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Contact;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Message;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.NamesByChatId;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.NewContact;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.BadgeDrawerArrowDrawable;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Request;

import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
import team7.tcss450.uw.edu.tcss_750_t7_2.utils.SendPostAsyncTask;
import team7.tcss450.uw.edu.tcss_750_t7_2.weather.FortyEightHourWeather;
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
        WeatherOptionsFragment.OnWeatherOptionsFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener,
        ConversationFragment.OnConversationFragmentInteractionListener,
        ContactFragment.OnContactListFragmentInteractionListener,
        RequestSentListFragment.OnRequestSentListFragmentInteractionListener,
        RequestReceivedListFragment.OnRequestReceivedListFragmentInteractionListener,
        RequestContainer.OnRequestContainerFragmentInteractionListener,
        NewContactBlankFragment.OnFragmentInteractionListener,
        NamesByChatIdFragment.OnRecentChatListFragmentInteractionListener,
        ChatFragment.OnChatFragmentInteractionListener{


    private String mJwToken;

    /** Credentials passed in from MainActivity when loginSuccess. */
    private Credentials mCredentials;

    /** This is the receiver used to receive broadcast messages */
    private PushMessageReceiver mPushMessageReceiver;
//    private CustomReceiver mReceiver = new CustomReceiver(); // TODO

    /** This chatid is used when user comes from the push notification. This is the chatid of the chatroom that initiated the notification. */
    private int mChatId;

    /** This username is used when user comes from the push notification. This is the username of the user. */
    private String mMyUsername;

    /** This number is displayed on the badge on the nav drawer item when there is a new connection request. */
    private int mConnCount;

    /** This number is displayed on the badge on the nav drawer item when there is a new conversation request. */
    private int mConvoCount;

    /** This number is displayed on the badge on the nav drawer item when there is a new message. */
    private int mTotalChatCount;

    /** This is the sum of all notification counts that will be displayed on the hamburger. */
    private int mTotalNotificationCount;

    /** This is the hamburger that's going to be badged. */
    private BadgeDrawerArrowDrawable mBadgeDrawable;

    /** True if user comes from a chatroom trying to add a new chat member. */
    private boolean mAddMember;

//    /** Navigation Item Requests Clicked */
//    private boolean mLoadNavRequest = false;

    private FortyEightHourWeather[] mFortyEightHour;

    private HashMap<String, String> mLocationData;

    private HashMap<String, String> mCurrentObservationData;

    private TenDayWeather[] mTenDay;

    /** TextView for nav drawer items Messages, Contacts, and Requests */
    private TextView mMessagesTV, mContactsTV, mRequestsTV;

    /** List to hold JSON of user's received contact request */
    private List<Request> mRequestsRecieved;

    /** List to hold JSON of user's sent contact requests*/
    private List<Request> mRequestsSent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Setting up broadcast receiver
//        IntentFilter filter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.RECEIVED_); // TODO

        Intent intent = getIntent(); // intent from MainActivity
        Bundle args = intent.getExtras();
        mJwToken = intent.getStringExtra(getString(R.string.keys_intent_jwt));
        mCredentials = (Credentials) args.getSerializable(getString(R.string.keys_intent_credentials));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Setting up the badge and notification count on the hamburger
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mBadgeDrawable = new BadgeDrawerArrowDrawable(getSupportActionBar().getThemedContext());
        toggle.setDrawerArrowDrawable(mBadgeDrawable);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Setting up the navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mMessagesTV = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_message_activity_home));
        mContactsTV = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_contacts_activity_home));
        mRequestsTV = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_requests_activity_home));

        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                if (getIntent().getBooleanExtra(getString(R.string.keys_intent_notification_msg), false)) {
                    mChatId = (int) getIntent().getExtras().getSerializable("chatid");
                    mMyUsername = (String) getIntent().getExtras().getSerializable("username");
                    Uri uri = new Uri.Builder().scheme("https")
                            .appendPath(getString(R.string.ep_base_url))
                            .appendPath(getString(R.string.ep_messaging_base))
                            .appendPath(getString(R.string.ep_messaging_getall)).build();

                    JSONObject msg = mCredentials.asJSONObject();
                    try {
                        msg.put("chatid", mChatId);
                        msg.put("username", mMyUsername);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.wtf("CREDS", msg.toString());

                    if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
                        onWaitFragmentInteractionHide();
                    }
                    new SendPostAsyncTask.Builder(uri.toString(), msg)
                            .onPreExecute(this::onWaitFragmentInteractionShow)
                            .onPostExecute(this::handleMessageGetOnPostExecute)
                            .onCancelled(this::handleErrorsInTask)
                            .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                            .build().execute();
                } else {
                   loadHomeWidgets();
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
    protected void onResume() {
        super.onResume();
        setNotification();
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
     * @author Hari Vignesh Jayapalan
     * This tutorial is found at: https://android.jlelse.eu/android-adding-badge-or-count-to-the-navigation-drawer-84c93af1f4d9
     */
    private void initializeCountDrawer() {
        if (mTotalChatCount > 0 || mConnCount > 0 || mConvoCount > 0) {
            mBadgeDrawable.setEnabled(true);
            mBadgeDrawable.setText(Integer.toString(mTotalChatCount + mConnCount + mConvoCount));
        } else {
            mBadgeDrawable.setEnabled(false);
        }

        if (mTotalChatCount > 0) {
            mMessagesTV.setGravity(Gravity.CENTER_VERTICAL);
            mMessagesTV.setTypeface(null, Typeface.BOLD);
            mMessagesTV.setTextColor(getResources().getColor(R.color.badge_red));
            mMessagesTV.setText(Integer.toString(mTotalChatCount));
        } else {
            mMessagesTV.setText("");
        }

//        if (mContactsTV) {
//            mContactsTV.setGravity(Gravity.CENTER_VERTICAL);
//            mContactsTV.setTypeface(null, Typeface.BOLD);
//            mContactsTV.setTextColor(getResources().getColor(R.color.badge_red));
//            mContactsTV.setText(mTotalChatCount);
//        }

        if (mConnCount > 0 || mConvoCount > 0) {
            mRequestsTV.setGravity(Gravity.CENTER_VERTICAL);
            mRequestsTV.setTypeface(null, Typeface.BOLD);
            mRequestsTV.setTextColor(getResources().getColor(R.color.badge_red));
            mRequestsTV.setText(Integer.toString(mConnCount + mConvoCount));
        } else {
            mRequestsTV.setText("");
        }
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
            loadHomeWidgets();
        } else if (id == R.id.nav_message_activity_home) {
            clearNotification("msg", null);
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath("messaging")
                    .appendPath("getchats")
                    .build();

            Log.wtf("RECENTS", uri.toString());

            JSONObject msg = new JSONObject();

            try {
                msg.put("email", mCredentials.getEmail());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.wtf("RECENTS", msg.toString());

            if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
                onWaitFragmentInteractionHide();
            }
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleGetChatsPost)
                    .onCancelled(this::handleErrorsInTask)
//                    .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                    .addHeaderField("content-type", "application/Json")
                    .build()
                    .execute();
        } else if (id == R.id.nav_weather_activity_home) { // Handle the camera action
            /* 98402 is hardcoded, eventually will make default location based on device location */
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_weather))
                    .appendPath(getString(R.string.ep_location))
                    .appendQueryParameter(getString(R.string.ep_location), "98402")
                    .build();
            if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
                onWaitFragmentInteractionHide();
            }
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
            if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
                onWaitFragmentInteractionHide();
            }
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleContactGetOnPostExecute)
                    .onCancelled(this::handleErrorsInTask)
                    .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                    .build().execute();
        } else if (id == R.id.nav_requests_activity_home) {

            clearNotification("connreq", null);

            /** tart the get query to return all requests from potential contacts. */
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_contacts_base))
                    .appendPath(getString(R.string.ep_contacts_getconnreq))
                    .appendQueryParameter("email", mCredentials.getEmail())
                    .build();

            JSONObject creds = mCredentials.asJSONObject();
            if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
                onWaitFragmentInteractionHide();
            }
            new GetAsyncTask.Builder(uri.toString())
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleRequestGetOnPostExecute)
                    .build().execute();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * @author Charles Bryan
     * helper method that loads the fragment
     * that user is navigating to.
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
     * Logs user our, clears saved credentials,
     * and returns to the Login Screen.
     */
    private void logout() {
        new DeleteTokenAsyncTask().execute();
    }

    public void handleGetChatsPost(final String result) {
        Log.wtf("CHATS_RESULT", result);
        try {
            JSONObject response = new JSONObject(result);
            if (response.has("chatids")) {
                JSONArray chatids = response.getJSONArray("chatids");
                List<Integer> idsList = new ArrayList<>();
                List<NamesByChatId> namesByChatIdList = new ArrayList<>();
                for (int i = 0; i < chatids.length(); i++) {
                    JSONObject chatid = chatids.getJSONObject(i);
                    idsList.add(chatid.getInt("chatid"));
                }
                JSONArray memberinfos = response.getJSONArray("memberinfos");
                for (int i = 0; i < idsList.size(); i++) {
                    StringBuilder names = new StringBuilder();
                    for (int j = 0; j < memberinfos.length(); j++) {
                        if (idsList.get(i) == memberinfos.getJSONObject(j).getInt("chatid")) {
                            names.append(memberinfos.getJSONObject(j).getString("firstname"));
                            if (j < memberinfos.length() - 1) {
                                names.append(", ");
                            }
                        }
                    }
                    namesByChatIdList.add(new NamesByChatId(idsList.get(i), names.toString()));
                }
                NamesByChatId[] nbciArray = new NamesByChatId[namesByChatIdList.size()];
                nbciArray = namesByChatIdList.toArray(nbciArray);
                Bundle args = new Bundle();
                args.putSerializable(NamesByChatIdFragment.ARG_RECENT_CHATS_LIST, nbciArray);
                args.putSerializable("username", response.getString("username"));
                Fragment frag = new NamesByChatIdFragment();
                frag.setArguments(args);
                onWaitFragmentInteractionHide();

//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, frag)
                        .addToBackStack(null);
                transaction.commit();
            } else {
                Log.wtf("ERROR", "no data in array");
                onWaitFragmentInteractionHide();

//                NewContactBlankFragment newContactBlankFragment = new NewContactBlankFragment();
//                Bundle args = new Bundle();
//                args.putSerializable("new_contact_status", "No results.");
//                newContactBlankFragment.setArguments(args);
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, newContactBlankFragment).addToBackStack(null);
//                transaction.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.wtf("ERROR", e.getMessage());
            onWaitFragmentInteractionHide();

//            NewContactBlankFragment newContactBlankFragment = new NewContactBlankFragment();
//            Bundle args = new Bundle();
//            args.putSerializable("new_contact_status", "No results.");
//            newContactBlankFragment.setArguments(args);
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, newContactBlankFragment).addToBackStack(null);
//            transaction.commit();
        }
    }

    @Override
    public void onSearchClicked(boolean addmember, int chatid) {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts_base))
                .appendPath(getString(R.string.ep_contacts_searchcontacts)).build();

        JSONObject msg = new JSONObject();
        EditText et = findViewById(R.id.new_contact_et_search);

        mAddMember = addmember;
        mChatId = chatid;

        if (!et.getText().toString().isEmpty()) {
            try {
                msg.put("input", et.getText().toString());
                msg.put("email", mCredentials.getEmail());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
                onWaitFragmentInteractionHide();
            }
//            if (addmember) {
//                new SendPostAsyncTask.Builder(uri.toString(), msg)
//                        .onPreExecute(this::onWaitFragmentInteractionShow)
//                        .onPostExecute(this::handleAddSearchOnPostExecute)
//                        .onCancelled(this::handleErrorsInTask)
//                        .addHeaderField("authorization", mJwToken) // Add the JWT as a header
//                        .build().execute();
//            } else {
                new SendPostAsyncTask.Builder(uri.toString(), msg)
                        .onPreExecute(this::onWaitFragmentInteractionShow)
                        .onPostExecute(this::handleSearchOnPostExecute)
                        .onCancelled(this::handleErrorsInTask)
                        .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                        .build().execute();
//            }
        }
    }

    public void handleAddSearchOnPostExecute(final String result) {
//        Log.wtf("ADD_SEARCH_RESULT", result);
//        try {
//            JSONObject response = new JSONObject(result);
//            if (response.has(getString(R.string.keys_json_contact_message))) {
//                JSONArray data = response.getJSONArray(getString(R.string.keys_json_contact_message));
//                List<NewContact> newContacts = new ArrayList<>();
//                for (int i = 0; i < data.length(); i++) {
//                    JSONObject jsonContact = data.getJSONObject(i);
//                    newContacts.add(new NewContact.Builder(jsonContact.getString(getString(R.string.keys_json_contact_first_name)), jsonContact.getString(getString(R.string.keys_json_contact_last_name)))
//                            .addEmail(jsonContact.getString(getString(R.string.keys_json_contact_email)))
//                            .addUsername(jsonContact.getString(getString(R.string.keys_json_contact_username)))
//                            .addMemberId(jsonContact.getInt("memberid"))
//                            .build());
//                }
//                NewContact[] contactsAsArray = new NewContact[newContacts.size()];
//                contactsAsArray = newContacts.toArray(contactsAsArray);
//                Bundle args = new Bundle();
//                args.putSerializable(NewContactFragment.ARG_NEW_CONTACT_LIST, contactsAsArray);
//                Fragment frag = new NewContactFragment();
//                frag.setArguments(args);
//                onWaitFragmentInteractionHide();
////                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                FragmentTransaction transaction = getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragmentContainer, frag)
//                        .addToBackStack(null);
//                transaction.commit();
//            } else {
//                Log.wtf("ERROR", "no data in array");
//                onWaitFragmentInteractionHide();
//
//                NewContactBlankFragment newContactBlankFragment = new NewContactBlankFragment();
//                Bundle args = new Bundle();
//                args.putSerializable("new_contact_status", "No results.");
//                newContactBlankFragment.setArguments(args);
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, newContactBlankFragment).addToBackStack(null);
//                transaction.commit();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.wtf("ERROR", e.getMessage());
//            onWaitFragmentInteractionHide();
//            NewContactBlankFragment newContactBlankFragment = new NewContactBlankFragment();
//            Bundle args = new Bundle();
//            args.putSerializable("new_contact_status", "No results.");
//            newContactBlankFragment.setArguments(args);
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, newContactBlankFragment).addToBackStack(null);
//            transaction.commit();
//        }
    }

    @Override
    public void onRequestSent(String email_b, boolean addmember, int chatid) {


        mAddMember = addmember;
        mChatId = chatid;

        JSONObject msg = new JSONObject();
        try {
            msg.put("email_a", mCredentials.getEmail());
            msg.put("email_b", email_b);
        } catch (JSONException e) {
            Log.wtf("JSON", "Error creating JSON: " + e.getMessage());
            e.printStackTrace();
        }
        if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
            onWaitFragmentInteractionHide();
        }

        if (addmember) {
            try {
                msg.put("email", email_b);
                msg.put("chatID", chatid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Uri uri = new Uri.Builder().scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_contacts_base))
                    .appendPath("convoAdd").build();
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleConvoAdd) // TODO: handle add member request
                    .onCancelled(this::handleErrorsInTask)
                    .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                    .build().execute();
        } else {
            Uri uri = new Uri.Builder().scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_contacts_base))
                    .appendPath("connReq").build();
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleSendConnReq)
                    .onCancelled(this::handleErrorsInTask)
                    .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                    .build().execute();
        }
    }

    public void handleConvoAdd(final String result) {
        Log.wtf("SEND_CONNREQ_RESULT", result);
        onWaitFragmentInteractionHide();
        Toast.makeText(this, "Member added!", Toast.LENGTH_SHORT).show();
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
                args.putSerializable("addmember", mAddMember);
                args.putSerializable("chatid", mChatId);
                Fragment frag = new NewContactFragment();
                frag.setArguments(args);
                onWaitFragmentInteractionHide();

//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

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

    private void handleContactGetOnPostExecute(final String result) {
        Log.wtf("CONTACT_RESULT", result);
        try {
            JSONObject response = new JSONObject(result);

            if (response.has(getString(R.string.keys_json_contact_message))) {

                JSONArray data = response.getJSONArray(getString(R.string.keys_json_contact_message));

                List<Contact> contacts = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonContact = data.getJSONObject(i);
                    contacts.add(new Contact.Builder(jsonContact.getString(getString(R.string.keys_json_contact_first_name)),
                            jsonContact.getString(getString(R.string.keys_json_contact_last_name)))
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

//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

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
    public void onContactListFragmentInteraction(Contact item) throws JSONException {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_getall)).build();

        JSONObject msg = mCredentials.asJSONObject();
        msg.put("contactemail", item.getEmail());

        Log.wtf("CREDS", msg.toString());
        if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
            onWaitFragmentInteractionHide();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleMessageGetOnPostExecute)
                .onCancelled(this::handleErrorsInTask)
                .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                .build().execute();
    }

    @Override
    public void onRecentChatListFragmentInteraction(NamesByChatId item, String username) throws JSONException {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_getall)).build();

        JSONObject msg = mCredentials.asJSONObject();
        msg.put("chatid", item.getmChatId());
        msg.put("username", username);

        mChatId = item.getmChatId();
        mMyUsername = username;

        Log.wtf("CREDS", msg.toString());
        if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
            onWaitFragmentInteractionHide();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleMessageGetOnPostExecute)
                .onCancelled(this::handleErrorsInTask)
                .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                .build().execute();
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
            } else {
                args.putSerializable("send_username", mMyUsername);
                args.putSerializable("send_chat_id", mChatId);
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

//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
    public void onRequestSentListFragmentInteraction(View v, Request item) {


        Button b = (Button) findViewById(v.getId());
        Log.wtf("WTF", "b: " + b.getText().toString());
        Log.wtf("WTF", "b: " + item.getContactName());


    }

    /**
     *
     * @param chatid Carried over from the chat which the user wants to add another user in.
     */
    @Override
    public void onAddChatMemberClicked(int chatid) {
        Bundle args = new Bundle();
        args.putSerializable("chatid", chatid);
        args.putSerializable("addmember", true);
        NewContactBlankFragment newContactBlankFragment = new NewContactBlankFragment();
        newContactBlankFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, newContactBlankFragment).addToBackStack(null);
        transaction.commit();
    }

//    @Override
//    public void onFragmentInteraction(Uri uri) {
//
//    }
//
//    @Override
//    public void onConfirmClicked(JSONObject msg) {
//
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
                if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
                    onWaitFragmentInteractionHide();
                }
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
     * Gets the users pending friend requests from the DB
     * @param result is list users friend requests.
     */
    private void handleRequestGetOnPostExecute(final String result) {

        Log.wtf("REQUEST_RESULT", result);
        try {
            JSONObject response = new JSONObject(result);
//            Bundle args = new Bundle();
            boolean success = response.getBoolean("success");
            if (success) {

                JSONArray data = response.getJSONArray("message");

                mRequestsRecieved = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonMessage = data.getJSONObject(i);
                    mRequestsRecieved.add(new Request.Builder(jsonMessage.getString("email"),
                            jsonMessage.getString(getString(R.string.keys_json_request_first_name)),
                            jsonMessage.getString(getString(R.string.keys_json_request_last_name)),
                            jsonMessage.getString("memberid_a"),
                            jsonMessage.getString("memberid_b"))
                            .addRequestType("received")
                            .build());
                }


                Uri uri = new Uri.Builder()
                        .scheme("https")
                        .appendPath(getString(R.string.ep_base_url))
                        .appendPath(getString(R.string.ep_contacts_base))
                        .appendPath(getString(R.string.ep_contacts_getconnreq))
                        .appendQueryParameter("email", mCredentials.getEmail())
                        .appendQueryParameter("pending", "1")
                        .build();

                JSONObject creds = mCredentials.asJSONObject();
                if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
                    onWaitFragmentInteractionHide();
                }
                new GetAsyncTask.Builder(uri.toString())
                        .onPreExecute(this::onWaitFragmentInteractionShow)
                        .onPostExecute(this::handleRequestSentGetOnPostExecute)
                        .build().execute();
            } else {
                Log.wtf("ERROR", "no data in array");
                onWaitFragmentInteractionHide();
                Uri uri = new Uri.Builder()
                        .scheme("https")
                        .appendPath(getString(R.string.ep_base_url))
                        .appendPath(getString(R.string.ep_contacts_base))
                        .appendPath(getString(R.string.ep_contacts_getconnreq))
                        .appendQueryParameter("email", mCredentials.getEmail())
                        .appendQueryParameter("pending", "1")
                        .build();

                JSONObject creds = mCredentials.asJSONObject();
                if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
                    onWaitFragmentInteractionHide();
                }
                new GetAsyncTask.Builder(uri.toString())
                        .onPreExecute(this::onWaitFragmentInteractionShow)
                        .onPostExecute(this::handleRequestSentGetOnPostExecute)
                        .build().execute();

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.wtf("ERROR", e.getMessage());
            onWaitFragmentInteractionHide();
        }

    }

    /**
     * Get the user's sent friend requests and sends
     * both sent and received to the container.
     *  bundles the arg to the Request Container
     *  Returns JSON
     * @param result
     */
    private void handleRequestSentGetOnPostExecute(String result){

        Log.wtf("REQUEST_RESULT", result);
        try {
            JSONObject response = new JSONObject(result);
            Bundle args = new Bundle();

            boolean success = response.getBoolean("success");
            if (success) {

                JSONArray data = response.getJSONArray("message");

                mRequestsSent = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonMessage = data.getJSONObject(i);
                    mRequestsSent.add(new Request.Builder(jsonMessage.getString("email"),
                            jsonMessage.getString(getString(R.string.keys_json_request_first_name)),
                            jsonMessage.getString(getString(R.string.keys_json_request_last_name)),
                            jsonMessage.getString("memberid_a"),
                            jsonMessage.getString("memberid_b"))

                            .addRequestType("sent")
                            .build());
                }

                args.putSerializable(RequestContainer.ARG_SENT_REQUEST,(Serializable) mRequestsSent);
                if(null != mRequestsRecieved){
                    args.putSerializable(RequestContainer.ARG_RECEIVED_REQUEST, (Serializable) mRequestsRecieved);
                }
                args.putSerializable(RequestContainer.ARG_CREDS, mCredentials);
                args.putSerializable(RequestContainer.ARG_JWT, mJwToken);

                Fragment frag = new RequestContainer();
                frag.setArguments(args);
                onWaitFragmentInteractionHide();
//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                loadFragment(frag);


            } else {
                Log.wtf("ERROR", "no data in array");
                onWaitFragmentInteractionHide();
                args.putSerializable(RequestContainer.ARG_RECEIVED_REQUEST, (Serializable) mRequestsRecieved);
                args.putSerializable(RequestContainer.ARG_CREDS, mCredentials);
                Fragment frag = new RequestContainer();
                frag.setArguments(args);
                onWaitFragmentInteractionHide();
//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                loadFragment(frag);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.wtf("ERROR", e.getMessage());
            onWaitFragmentInteractionHide();
        }

    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR", result);
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
     * Get and set notification counts.
     */
    public void setNotification(){
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath("notifications")
                .appendPath("getcount")
                .build();

        JSONObject msg = new JSONObject();

        try {
            msg.put("email_b", mCredentials.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.wtf("NOTIFICATION", msg.toString());
        if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
            onWaitFragmentInteractionHide();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleSetNotificationPost)
                .onCancelled(this::handleErrorsInTask)
                .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                .build().execute();
    }

    /**
     * Save notification counts to member variables and displays them by calling initializeCountDrawer().
     * @param result
     */
    public void handleSetNotificationPost(String result) {
        Log.wtf("NOTIFICATION_COUNTS", result);
        try {
            JSONObject response = new JSONObject(result);
            Bundle args = new Bundle();
            List<ChatCount> msgCounts = new ArrayList<>();
            if (response.has("success")){
                if (response.getBoolean("success")) {
                    args.putSerializable("connCount", (Serializable) response.getInt("connCount"));
                    mConnCount = response.getInt("connCount");
                    args.putSerializable("convoCount", (Serializable) response.getInt("convoCount"));
                    mConvoCount = response.getInt("convoCount");
                    JSONArray msgCountJsonArray = response.getJSONArray("msgCount");
                    int totalChatCount = 0;
                    for (int i = 0; i < msgCountJsonArray.length(); i++) {
                        JSONObject jsonObject = msgCountJsonArray.getJSONObject(i);
                        totalChatCount += jsonObject.getInt("count");
                        msgCounts.add(new ChatCount(jsonObject.getInt("chatid"), jsonObject.getInt("count")));
                    }
                    ChatCount[] msgCountsArray = new ChatCount[msgCounts.size()];
                    msgCountsArray = msgCounts.toArray(msgCountsArray);
                    args.putSerializable("chatCountList", msgCountsArray);
                    // TODO: a field to save the list of counts.
                    args.putSerializable("totalChatCount", totalChatCount);
                    mTotalChatCount = totalChatCount;
                    mTotalNotificationCount = mConnCount + mConvoCount + mTotalChatCount;
                }

                initializeCountDrawer();

                if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
                    onWaitFragmentInteractionHide();
                }
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

    /**
     * Clears the in-app notification of specified type.
     * @param type can be 'msg', 'connreq', or 'convoreq'
     */
    public void clearNotification(String type, Integer chatid){
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath("notifications")
                .appendPath("clearnotification")
                .build();

        JSONObject msg = new JSONObject();

        try {
            msg.put("email_b", mCredentials.getEmail());
            msg.put("notetype", type);
            if (chatid != null) {
                msg.put("chatid", chatid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.wtf("NOTIFICATION", msg.toString());
        if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
            onWaitFragmentInteractionHide();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleClearNotificationPost)
                .onCancelled(this::handleErrorsInTask)
                .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                .build().execute();
    }

    /**
     * Clear in-app notifications based on type. If new count < 0, remove in-app notifications.
     * @param result
     */
    public void handleClearNotificationPost(String result) {
        Log.wtf("NOTIFICATION_COUNTS", result);
        try {
            JSONObject response = new JSONObject(result);
            if (response.has("success")) {
                if (response.getBoolean("success")) {
                    if (response.getString("type") == "msg") {
                        mTotalChatCount = 0;
                    } else {
                        mConnCount = 0;
                        mConvoCount = 0;
                    }
                    mTotalNotificationCount = mTotalChatCount + mConnCount + mConvoCount;
                    setNotification();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive broadcast and perform actions.
     */
    private class PushMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setNotification();
        }
    }

    private void loadHomeWidgets(){
        /**
         * Start the get query to return all requests from potential contacts.
         */
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts_base))
                .appendPath(getString(R.string.ep_contacts_getconnreq))
                .appendQueryParameter("email", mCredentials.getEmail())
                .build();

        JSONObject creds = mCredentials.asJSONObject();
        if (getSupportFragmentManager().findFragmentByTag("WAIT") != null) {
            onWaitFragmentInteractionHide();
        }
        new GetAsyncTask.Builder(uri.toString())
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleHomeRequestGetOnPostExecute)
                .build().execute();

    }

    private void handleHomeRequestGetOnPostExecute(String result) {
        Log.wtf("REQUEST_RESULT", result);
        try {
            JSONObject response = new JSONObject(result);
            Bundle args = new Bundle();
            boolean success = response.getBoolean("success");
            if (success) {

                JSONArray data = response.getJSONArray("message");

                mRequestsRecieved = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonMessage = data.getJSONObject(i);
                    mRequestsRecieved.add(new Request.Builder(jsonMessage.getString("email"),
                            jsonMessage.getString(getString(R.string.keys_json_request_first_name)),
                            jsonMessage.getString(getString(R.string.keys_json_request_last_name)),
                            jsonMessage.getString("memberid_a"),
                            jsonMessage.getString("memberid_b"))
                            .addRequestType("received")
                            .build());
                }
                if(null != mRequestsRecieved){
                    args.putSerializable(HomeFragment.ARG_RECEIVED_REQUEST, (Serializable) mRequestsRecieved);
                }
                args.putSerializable(HomeFragment.ARG_CREDS, mCredentials);
                args.putSerializable(HomeFragment.ARG_JWT, mJwToken);

                Fragment frag = new HomeFragment();
                frag.setArguments(args);
                onWaitFragmentInteractionHide();
//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                loadFragment(frag);
            } else {
                loadFragment(new HomeFragment());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.wtf("ERROR", e.getMessage());
            onWaitFragmentInteractionHide();
        }
    }

    /** vvv Orphan methods vvv */

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
    public void onChangePasswordClicked() {
        Bundle args = new Bundle();
        args.putSerializable("credentials", mCredentials);
        Fragment frag = new ChangePasswordFragment();
        frag.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onConversationFragmentInteraction(Uri uri) {

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
    public void onMessageListFragmentInteraction(Message item) {

    }

    @Override
    public void onNewContactListFragmentInteraction(NewContact item, boolean addmember) {

    }

    @Override
    public void onNoResults() { // TODO: Remove this.
    }


    @Override
    public void onRequestReceivedListFragmentInteraction(Request item) {

    }

    @Override
    public void onRequestContainerFragmentInteraction(View View) {

    }
}
