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

import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
import team7.tcss450.uw.edu.tcss_750_t7_2.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    private Credentials mCredentials;
    private OnRegisterFragmentInteractionListener mListener;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        Button butt = view.findViewById(R.id.register_butt_register);
        butt.setOnClickListener(this::register);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterFragmentInteractionListener) {
            mListener = (OnRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRegisterFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void register(View view) {
        if (mListener != null) {
            boolean pass = true;
            EditText fname = (EditText) getActivity().findViewById(R.id.register_et_fname);
            EditText lname = (EditText) getActivity().findViewById(R.id.register_et_lname);
            EditText username = (EditText) getActivity().findViewById(R.id.register_et_username);
            EditText email = (EditText) getActivity().findViewById(R.id.register_et_email);
            EditText password = (EditText) getActivity().findViewById(R.id.register_et_password);
            EditText retypepassword = (EditText) getActivity().findViewById(R.id.register_et_retype_password);

            String fnameMessage = fname.getText().toString();
            String lnameMessage = lname.getText().toString();
            String usernameMessage = username.getText().toString();
            String emailMessage = email.getText().toString();
            String passwordMessage = password.getText().toString();
            String retypepasswordMessage = retypepassword.getText().toString();

            if (fnameMessage.isEmpty()) {
                fname.setError("This field cannot be empty");
                pass = false;
            }
            if (lnameMessage.isEmpty()) {
                lname.setError("This field cannot be empty");
                pass = false;
            }
            if (usernameMessage.isEmpty()) {
                username.setError("This field cannot be empty");
                pass = false;
            }
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
            } else if (passwordMessage.length() <= 5) {
                password.setError("Password must be 6 characters or longer");
                pass = false;
            }

            if (retypepasswordMessage.isEmpty()) {
                retypepassword.setError("This field cannot be empty");
                pass = false;
            } else if (retypepasswordMessage.length() <= 5) {
                retypepassword.setError("Password must be 6 characters or longer");
                pass = false;
            }

            if (!retypepasswordMessage.equals(passwordMessage)) {
                retypepassword.setError("Passwords do not match");
                pass = false;
            }

            if (pass == true) {
                //mListener.onRegisterSuccess(new Credentials.Builder(emailMessage, passwordMessage).build());
                Credentials credentials = new Credentials.Builder(passwordMessage)
                        .addEmail(emailMessage)
                        .addFirstName(fnameMessage)
                        .addLastName(lnameMessage)
                        .addUsername(usernameMessage)
                        .build();

                // Build the web service URL
                Uri uri = new Uri.Builder()
                        .scheme("https")
                        .appendPath(getString(R.string.ep_base_url))
                        .appendPath(getString(R.string.ep_register))
                        .build();

                // Build the JSONObject
                JSONObject msg = credentials.asJSONObject();
                mCredentials = credentials;

                // Instantiate and execute the AsyncTask.
                new SendPostAsyncTask.Builder(uri.toString(), msg)
                        .onPreExecute(this::handleRegisterOnPre)
                        .onPostExecute(this::handleRegisterOnPost)
                        .onCancelled(this::handleErrorsInTask)
                        .build().execute();
            }
            // This is the builder pattern and it's good for constructor that takes a lot of parameters.
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
    private void handleRegisterOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Handle onPostExecute of the AsyncTask. The result from our webservice is a JSON formatted String.
     * Parse it for success or failure.
     * @param result the JSON formatted String response from the web service.
     */
    private void handleRegisterOnPost(String result) {
        Log.wtf("JWTREG", result);
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean(getString(R.string.keys_json_register_success));
            if (success) {
                // Registration was successful. Switch to the loadSuccessFragment.
                mListener.onRegisterSuccess(mCredentials);
                return;
            } else {
                // Registration was unsuccessful. Don't switch fragments and inform the user
                JSONObject errorJSON = new JSONObject(resultsJSON.getString(getString(R.string.keys_json_register_error)));
                String detail = errorJSON.getString(getString(R.string.keys_json_register_detail));
                ((TextView) getView().findViewById(R.id.register_et_fname)).setError("Registration Unsuccessful. Reason: " + detail);
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            // It appears that the web service did not return a JSON formatted String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result + System.lineSeparator() + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.register_et_fname)).setError("Registration Unsuccessful. Please contact IT specialist. (Parse Error) Reason: " + e.getMessage());
        }
    }

    public interface OnRegisterFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener{
        void onRegisterSuccess(Credentials credentials);
    }
}
