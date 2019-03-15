package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import me.pushy.sdk.Pushy;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
import team7.tcss450.uw.edu.tcss_750_t7_2.utils.SendPostAsyncTask;

/**
 * This Activity is the host of Login and Registration Fragment.
 */
public class MainActivity extends AppCompatActivity implements
        LoginFragment.OnLoginFragmentInteractionListener,
        RegisterFragment.OnRegisterFragmentInteractionListener,
        EmailVerificationFragment.OnEmailVerificationFragmentInteractionListener,
        ForgotPasswordFragment.OnForgotFragmentInteractionListener{

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

    @Override
    public void onForgotPasswordClicked() {
//        SharedPreferences prefs = getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
//
//        prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();
//        prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();

        Fragment frag = new ForgotPasswordFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_container, frag).addToBackStack(null);
        transaction.commit();
    }

    /**
     * Actions for when the reset button is clicked.
     * @param email passed from the forgot password fragment and is the email that the
     *              user entered into the edit text (where you would want your temp password
     *              sent to).
     */
    @Override
    public void onResetClicked(String email) {
        Log.wtf("RESET_EMAIL", email);

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath("login")
                .appendPath("forgotpw")
                .build();

        JSONObject msg = new JSONObject();
        try {
            msg.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleForgotPasswordPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /** Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR", result);
    }

    /**
     * Shows a dialog informing the user that an email has been sent to them.
     * @param result returned from server
     */
    private void handleForgotPasswordPost(String result) {
        Log.e("ASYNC_TASK_ERROR", result);
        onWaitFragmentInteractionHide();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("An email with a temporary password has been sent to the email address you've provided. Please remember to login and reset your password.")
                .setTitle("Reset Success");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Fragment frag = new LoginFragment();
                FragmentTransaction transaction = getSupportFragmentManager().
                        beginTransaction().replace(R.id.activity_main_container, frag);
                transaction.commit();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
