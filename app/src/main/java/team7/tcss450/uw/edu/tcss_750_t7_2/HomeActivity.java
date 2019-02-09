package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import team7.tcss450.uw.edu.tcss_750_t7_2.dummy.DummyContent;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;

/**
 * Container for fragments after user is successfully
 * logged into messaging app.
 */
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoginFragment.OnLoginFragmentInteractionListener,
        RegisterFragment.OnRegisterFragmentInteractionListener,
        MessageFragment.OnMessageListFragmentInteractionListener,
        WeatherFragment.OnWeatherFragmentInteractionListener,
        WeatherOptionsFragment.OnWeatherOptionsFragmentInteractionListener{

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
    }

    /**
     * On start will load the weather
     * for the users current location.
     */
    @Override
    public void onStart(){
        super.onStart();
        loadFragment(new WeatherFragment());
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

        if (id == R.id.nav_message_activity_home) {
            loadFragment(new MessageFragment());
            // Handle the camera action
        } else if (id == R.id.nav_weather_activity_home) {
            loadFragment(new WeatherOptionsFragment());

        }

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
        SharedPreferences prefs = getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();
        prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();

        // Close the app
//        finishAndRemoveTask();

        // Or close this activity and bring back the Login
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        // End this Activity and remove it from the Activity back stack
        finish();
    }

    /**
     * Interaction listener for Message Fragment
     * @param item
     */
    @Override
    public void onMessageListFragmentInteraction(DummyContent.DummyItem item) {

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

    }

    /**
     * Wait fragments handles time between calls
     * to get or post.
     */
    @Override
    public void onWaitFragmentInteractionHide() {

    }

    /**
     * Weather options fragments listener
     * @param uri
     */
    @Override
    public void onWeatherOptionsFragmentInteraction(Uri uri) {

    }
}
