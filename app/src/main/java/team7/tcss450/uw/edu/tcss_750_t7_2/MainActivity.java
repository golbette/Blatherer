package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;

import me.pushy.sdk.Pushy;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;

/**
 * This Activity is the host of Login and Registration Fragment.
 */
public class MainActivity extends AppCompatActivity implements
        LoginFragment.OnLoginFragmentInteractionListener,
        RegisterFragment.OnRegisterFragmentInteractionListener,
        EmailVerificationFragment.OnEmailVerificationFragmentInteractionListener{

    /**
     * Tag used in Log statements.
     */
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Saves the state of the Remember Me switch.
     */
    private Boolean mRememberVal;

    /**
     * True if user launches the app from the notification when app is NOT visible.
     * When status bar is clicked, take user to the chat fragment.
     */
    private boolean mLoadFromChatNotification = false;

    /**
     * True if user launches the app from the notification when app is NOT visible.
     * When status bar is clicked, takes user to the request fragment.
     */
    private boolean mLoadFromRequest = false;

    /**
     * The sender's username passed in from the push notification.
     */
    private String mMyUsername;

    /**
     * The chatid to launch when push notification is clicked.
     */
    private int mChatId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Invoke Pushy.listen(this) in your launcher activity's onCreate() method so that Pushy's internal notification listening service will restart itself, if necessary.
        Pushy.listen(this);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("type")) {
                if (getIntent().getExtras().getSerializable("type").equals("msg")) {
                    mLoadFromChatNotification = getIntent().getExtras().getSerializable("type").equals("msg");
                    mChatId = (int) getIntent().getExtras().getSerializable("chatid");
                    mMyUsername = (String) getIntent().getExtras().getSerializable("receiver");
                } else if (getIntent().getExtras().getSerializable("type").equals("conn")) {
                    // TODO: In case launching request fragment is implemented
                } else if (getIntent().getExtras().getSerializable("type").equals("conv")) {
                    // TODO: In case launching request fragment is implemented
                }
            }
        }

        if (savedInstanceState == null) {
            if (findViewById(R.id.activity_main_container) != null) {
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.activity_main_container, new LoginFragment()).commit();
            }
        }
    }

    @Override
    public void onLoginSuccess(Credentials credentials, String jwt) {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        if (prefs.contains(getString(R.string.keys_prefs_stay_logged_in))) {
            mRememberVal = prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in), false);
        }

        Log.wtf("REMEMBER", mRememberVal + " (on login success)");
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(getString(R.string.keys_intent_credentials), (Serializable) credentials);
        intent.putExtra(getString(R.string.keys_intent_jwt), jwt);
        intent.putExtra(getString(R.string.login_switch_remember_val), mRememberVal);
        intent.putExtra("chatid", mChatId);
        intent.putExtra("username", mMyUsername);
        intent.putExtra(getString(R.string.keys_intent_notification_msg), mLoadFromChatNotification);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRegisterClicked() {
        RegisterFragment registerFragment = new RegisterFragment();
        FragmentTransaction transaction = getSupportFragmentManager().
                beginTransaction().
                replace(R.id.activity_main_container,
                        registerFragment).addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onRegisterSuccess(Credentials credentials) {
        EmailVerificationFragment emailVerf = new EmailVerificationFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.credential_key), credentials);
        emailVerf.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().
                beginTransaction().replace(R.id.activity_main_container, emailVerf);
        transaction.commit();
    }


    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_container, new WaitFragment(), "WAIT")
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

    @Override
    public void onEmailVerificationFragmentInteraction(Credentials credentials) {
        LoginFragment loginFragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.credential_key), credentials);
        loginFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().
                beginTransaction().replace(R.id.activity_main_container, loginFragment);
        transaction.commit();

    }
}
