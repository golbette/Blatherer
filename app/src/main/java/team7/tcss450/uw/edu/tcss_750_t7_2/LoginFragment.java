package team7.tcss450.uw.edu.tcss_750_t7_2;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import me.pushy.sdk.Pushy;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
import team7.tcss450.uw.edu.tcss_750_t7_2.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    public final static String TAG = "LoginFrag";
    private OnLoginFragmentInteractionListener mListener;
    private Credentials mCredentials;
    private String mJwt;
    private Boolean mRememberVal;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        // Says Good Morning when time is AM and Good Afternoon when time is PM
        TextView greetMessage = getActivity().findViewById(R.id.login_tv_greet_msg);
        if (Calendar.getInstance().get(Calendar.AM_PM) == Calendar.AM) {
            greetMessage.setText(getString(R.string.login_tv_morning));
        } else {
            greetMessage.setText(getString(R.string.login_tv_afternoon));
        }

        EditText emailMessage = getActivity().findViewById(R.id.login_et_email);
        EditText passwordMessage = getActivity().findViewById(R.id.login_et_password);

        Switch remember = (Switch) getActivity().findViewById(R.id.login_switch_remember);
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);

        mRememberVal = remember.isChecked();
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    if (prefs.contains(getString(R.string.keys_prefs_stay_logged_in))) {
                        prefs.edit().remove(getString(R.string.keys_prefs_stay_logged_in)).apply();
//                        prefs.edit().remove(getString(R.string.keys_prefs_username)).apply();
                        prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();
                        prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
                    }
                }
                Log.wtf("REMEMBER", isChecked + " (switch listener)");
            }
        });

        if (prefs.contains(getString(R.string.keys_prefs_email))
                && prefs.contains(getString(R.string.keys_prefs_password))
                && prefs.contains(getString(R.string.keys_prefs_stay_logged_in))) {
            final String email = prefs.getString(getString(R.string.keys_prefs_email), "");
//            final String username = prefs.getString(getString(R.string.keys_prefs_username), "");
            final String password = prefs.getString(getString(R.string.keys_prefs_password), "");
            final Boolean rememberVal = prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in), false);
            emailMessage.setText(email);
            passwordMessage.setText(password);
            remember.setChecked(rememberVal);

            boolean loggedOutByUser = (boolean) getActivity().getIntent().getBooleanExtra(getString(R.string.keys_logged_out_by_user), false);
            Log.wtf("LOGGED OUT", loggedOutByUser + " (onStart)");
            if (!loggedOutByUser) {
                doLogin(new Credentials.Builder(email, password).build());
            }
        }

        if (getArguments() != null) {
            Credentials creds = (Credentials) getArguments().getSerializable(getString(R.string.credential_key));
            emailMessage.setText(creds.getEmail());
            passwordMessage.setText(creds.getPassword());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Button butt = (Button) view.findViewById(R.id.login_butt_sign_in);
        butt.setOnClickListener(this::login);

        butt = (Button) view.findViewById(R.id.login_butt_register);
        butt.setOnClickListener(this::register);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement OnLoginFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void login(View view) {
        if (mListener != null) {
            boolean pass = true;
            EditText email = (EditText) getActivity().findViewById(R.id.login_et_email);
            EditText password = (EditText) getActivity().findViewById(R.id.login_et_password);
            String emailMessage = email.getText().toString();
            String passwordMessage = password.getText().toString();
//            if (!emailMessage.contains("@")) {
//                email.setError("Must enter a valid email address");
//                pass = false;
//            }
            if (emailMessage.isEmpty()) {
                email.setError("This field cannot be empty");
                pass = false;
            }
            if (passwordMessage.isEmpty()) {
                password.setError("This field cannot be empty");
                pass = false;
            }
            if (pass == true){
                doLogin(new Credentials.Builder(emailMessage, passwordMessage).build());
            }
            // This is the builder pattern and it's good for constructor that takes a lot of parameters.
        }
    }

    /**
     * Automatically logs user in if credentials are saved when the login fragment is in the foreground.
     * @param credentials
     */
    private void doLogin(Credentials credentials) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .build();

        JSONObject msg = credentials.asJSONObject();
        mCredentials = credentials;
        Log.d("JSON Credentials", msg.toString());
        // Instantiate and execute the AsyncTask.
        // Feel free to add a handler for onPreExecution so that a progress bar is displayed or maybe disable buttons.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::handleLoginOnPre)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Goes to the Register Fragment.
     * @param view The Register button
     */
    public void register(View view) {
        if (mListener != null) {
            mListener.onRegisterClicked();
        }
    }

    /**
     * Saves credentials as Shared Preferences.
     * @param credentials
     */
    private void saveCredentials(final Credentials credentials) {
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        prefs.edit().putString(getString(R.string.keys_prefs_email), credentials.getEmail()).apply();
//        prefs.edit().putString(getString(R.string.keys_prefs_username), credentials.getUsername()).apply();
        prefs.edit().putString(getString(R.string.keys_prefs_password), credentials.getPassword()).apply();
        prefs.edit().putBoolean(getString(R.string.keys_prefs_stay_logged_in), mRememberVal).apply();
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provided from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR", result);
    }

    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleLoginOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Handle onPostExecute of the AsyncTask. The result from our webservice is a JSON formatted String.
     * Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        Log.wtf("JWTLOGIN", result);

        Switch remember = getActivity().findViewById(R.id.login_switch_remember);
        Boolean rememberVal = remember.isChecked();
        mRememberVal = rememberVal;
//        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
//        if (!mRememberVal && prefs.contains(getString(R.string.keys_prefs_stay_logged_in))) {
//            prefs.edit().remove(getString(R.string.keys_prefs_stay_logged_in)).apply();
//        }

        Log.wtf("REMEMBER", rememberVal.toString() + " (handle login on post)");

        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean(getString(R.string.keys_json_login_success));
            String message = resultsJSON.getString("message");
            if (success) {
                mJwt = resultsJSON.getString(getString(R.string.keys_json_login_jwt));
                if (mRememberVal) {
                    saveCredentials(mCredentials);
                    Log.wtf("CREDS", mCredentials.getUsername());
                }
//                mListener.onLoginSuccess(mCredentials, mJwt);
                new RegisterForPushNotificationsAsync().execute();
                return;
            } else {
                // Login was unsuccessful. Don't switch fragments and inform the user
                ((TextView) getView().findViewById(R.id.login_et_email)).setError("Login Unsuccessful: " + message);
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            // It appears that the web service did not return a JSON formatted String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result + System.lineSeparator() + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.login_et_email)).setError("Login Unsuccessful");
        }
    }

    public interface OnLoginFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener{
        void onLoginSuccess(Credentials credentials, String jwt);
        void onRegisterClicked();
    }

    private void handlePushyTokenOnPost(String result) {
        try {
            Log.d("JSON result", result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                saveCredentials(mCredentials);
                mListener.onLoginSuccess(mCredentials, mJwt);
                return;
            } else {
                // Saving the token wrong. Don't switch fragments and inform the user
                ((TextView) getView().findViewById(R.id.login_et_email)).setError("Login Unsuccessful");
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            // It appears that the web service didn't return a JSON formatted String or it didn't have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result + System.lineSeparator() + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.login_et_email)).setError("Login Unsuccessful");
        }
    }

    private class RegisterForPushNotificationsAsync extends AsyncTask<Void, String, String> {
        protected String doInBackground(Void... params) {
            String deviceToken = "";
            try {
                // Assign a unique token to this device
                deviceToken = Pushy.register(getActivity().getApplicationContext());
                // Subscribe to a topic (this is a blocking call)
                Pushy.subscribe("all", getActivity().getApplicationContext());
            } catch (Exception e) {
                cancel(true);
                // Return e to onCancelled
                return e.getMessage();
            }
            // Success
            return deviceToken;
        }

        @Override
        protected void onCancelled(String errorMsg) {
            super.onCancelled(errorMsg);
            Log.d("Blatherer", "Error getting Pushy device token: " + errorMsg);
        }

        @Override
        protected void onPostExecute(String deviceToken) {
            // Log it for debugging purposes
            Log.d("Blatherer", "Pushy device token: " + deviceToken);
            Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_pushy))
                    .appendPath(getString(R.string.ep_token))
                    .build();
            JSONObject msg = mCredentials.asJSONObject();
            Log.wtf("MSG", msg.toString());
            try {
                msg.put("token", deviceToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(LoginFragment.this::handlePushyTokenOnPost)
                    .onCancelled(LoginFragment.this::handleErrorsInTask)
                    .addHeaderField("authorization", mJwt)
                    .build().execute();
//            saveCredentials(mCredentials);
//            mListener.onLoginSuccess(mCredentials, mJwt);
        }
    }
}
