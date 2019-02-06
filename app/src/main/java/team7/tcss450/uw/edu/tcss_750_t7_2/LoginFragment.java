package team7.tcss450.uw.edu.tcss_750_t7_2;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
import team7.tcss450.uw.edu.tcss_750_t7_2.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    public final static String TAG = "LoginFrag";
    private OnLoginFragmentInteractionListener mListener;
    private Credentials mCredentials;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        TextView greetMessage = getActivity().findViewById(R.id.login_tv_greet_msg);
        if (Calendar.getInstance().get(Calendar.AM_PM) == Calendar.AM) {
            greetMessage.setText(getString(R.string.login_tv_morning));
        } else {
            greetMessage.setText(getString(R.string.login_tv_afternoon));
        }

        if (getArguments() != null) {
            Credentials creds = (Credentials) getArguments().getSerializable(getString(R.string.credential_key));
            EditText emailMessage = getActivity().findViewById(R.id.login_et_email);
            EditText passwordMessage = getActivity().findViewById(R.id.login_et_password);
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
            if (!emailMessage.contains("@")) {
                email.setError("Must enter a valid email address");
                pass = false;
            }
            if (emailMessage.isEmpty()) {
                email.setError("This field cannot be empty");
                pass = false;
            }
            if (passwordMessage.isEmpty()) {
                password.setError("This field cannot be empty");
                pass = false;
            }
            if (pass == true){
                //mListener.onLoginSuccess(new Credentials.Builder(emailMessage, passwordMessage).build(), null);
                Credentials credentials = new Credentials.Builder(emailMessage, passwordMessage).build();

                // Build the web service URL
                Uri uri = new Uri.Builder()
                        .scheme("https")
                        .appendPath(getString(R.string.ep_base_url))
                        .appendPath(getString(R.string.ep_login))
                        .build();

                // Build the JSONObject
                JSONObject msg = credentials.asJSONObject();
                mCredentials = credentials;

                // Instantiate and execute the AsyncTask.
                new SendPostAsyncTask.Builder(uri.toString(), msg)
                        .onPreExecute(this::handleLoginOnPre)
                        .onPostExecute(this::handleLoginOnPost)
                        .onCancelled(this::handleErrorsInTask)
                        .build().execute();
            }
            // This is the builder pattern and it's good for constructor that takes a lot of parameters.
        }
    }

    public void register(View view) {
        if (mListener != null) {
            mListener.onRegisterClicked();
        }
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
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean(getString(R.string.keys_json_login_success));
            if (success) {
                // Login was successful. Switch to the loadSuccessFragment.
                //loginToken = resultsJSON.getString(getString(R.string.keys_json_login_jwt));
                mListener.onLoginSuccess(mCredentials, resultsJSON.getString(getString(R.string.keys_json_login_jwt)));
                return;
            } else {
                // Login was unsuccessful. Don't switch fragments and inform the user
                ((TextView) getView().findViewById(R.id.login_et_email)).setError("Login Unsuccessful");
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
}
