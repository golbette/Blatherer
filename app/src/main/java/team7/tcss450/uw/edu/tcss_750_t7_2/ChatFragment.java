package team7.tcss450.uw.edu.tcss_750_t7_2;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Message;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
import team7.tcss450.uw.edu.tcss_750_t7_2.utils.PushReceiver;
import team7.tcss450.uw.edu.tcss_750_t7_2.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements WaitFragment.OnFragmentInteractionListener{
    private String mJwt;
    private Credentials mCredentials;
    private static final String TAG = "CHAT_FRAG";
    private Integer mChatid;
    private String mUsername;
    private TextView mMessageOutputTextView;
    private EditText mMessageInputEditText;
    private String mSendUrl;
    private String mGetUrl;
    private PushMessageReceiver mPushMessageReceiver;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            mChatid = (Integer) getArguments().getSerializable("send_chat_id");
            mUsername = (String) getArguments().getSerializable("send_username");
        }

        Intent intent = getActivity().getIntent();
        Bundle args = new Bundle();
        args = intent.getExtras();
        mJwt = args.getString(getString(R.string.keys_intent_jwt), "");
        mCredentials = (Credentials) args.getSerializable(getString(R.string.keys_intent_credentials));
        Log.wtf("CREDS", "JWT: " + mJwt + ", Email: " + mCredentials.getEmail());
        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_send))
                .build()
                .toString();
        mGetUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_getall))
                .build()
                .toString();

//        Log.wtf("LOADCHAT", mSendUrl + "(send url)");
//        Log.wtf("LOADCHAT", mGetUrl + "(get url)");
        loadChatHistory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        mMessageOutputTextView = view.findViewById(R.id.text_chat_message_display);
        mMessageOutputTextView.setMovementMethod(new ScrollingMovementMethod());
        mMessageInputEditText = view.findViewById(R.id.edit_chat_message_display);
        ImageButton butt = (ImageButton) view.findViewById(R.id.button_chat_send);
        butt.setOnClickListener(this::handleSendClick);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReceiver != null) {
            getActivity().unregisterReceiver(mPushMessageReceiver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReceiver == null) {
            mPushMessageReceiver = new PushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        getActivity().registerReceiver(mPushMessageReceiver, iFilter);
    }

    private void handleSendClick(final View theButton) {
        String msg = mMessageInputEditText.getText().toString();
        JSONObject messageJson = new JSONObject();
        Log.wtf("SEND", mCredentials.toString());
        try {
            messageJson.put("username", mUsername);
            messageJson.put("message", msg);
            messageJson.put("chatid", mChatid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mSendUrl, messageJson).onPostExecute(this::endOfsendMsgTask)
                .onCancelled(error -> Log.e(TAG, error))
                .addHeaderField("authorization", mJwt)
                .build().execute();
    }

    private void endOfsendMsgTask(final String result) {
        try {
            // This is the result from the web service
            JSONObject res = new JSONObject(result);
            Log.wtf("SEND", result);
            if (res.has("success") && res.getBoolean("success")) {
                // The web service got our message. Time to clear out the input in EditText
                mMessageInputEditText.setText("");
                // It's up to me to decide if I want to send the message to the output here or wait for the message to come back from the web service.
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadChatHistory() {
        if (getArguments() != null) {
            Log.wtf("ARG", getArguments().toString());

            List<Message> messages = new ArrayList<Message>(Arrays.asList((Message[]) getArguments().getSerializable(MessageFragment.ARG_MESSAGE_LIST)));

            Log.wtf("ARG", messages.toString());

            for (int i = 0; i < messages.size(); i++) {
                mMessageOutputTextView.append(messages.get(i).getUsername() + ": " + messages.get(i).getMessage());
                mMessageOutputTextView.append(System.lineSeparator());
                mMessageOutputTextView.append(System.lineSeparator());
            }
        }
    }

//    private void loadChatTask(final String result) {
//        Log.wtf("LOADCHAT", "in loadChatTask");
//        try {
//            // This is the result from the web service
//            JSONObject res = new JSONObject(result);
//            Log.wtf("LOADCHAT", res.toString());
//            if (res.has("message")) {
//                JSONArray ja = (JSONArray) res.get("message");
//                for (int i = 0; i < ja.length(); i++) {
//                    JSONObject msg = ja.getJSONObject(i);
//                    mMessageOutputTextView.append(msg.getString("username") + ": " + msg.getString("message"));
//                    mMessageOutputTextView.append(System.lineSeparator());
//                    mMessageOutputTextView.append(System.lineSeparator());
//                }
//            }
//            onWaitFragmentInteractionHide();
//        } catch (JSONException e) {
//            e.printStackTrace();
//            onWaitFragmentInteractionHide();
//        }
//    }

    @Override
    public void onWaitFragmentInteractionShow() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new WaitFragment(), "WAIT")
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .remove(getActivity().getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }

    private class PushMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("SENDER") && intent.hasExtra("MESSAGE")) {
                String sender = intent.getStringExtra("SENDER");
                String messageText = intent.getStringExtra("MESSAGE");
                mMessageOutputTextView.append(sender + ": " + messageText);
                mMessageOutputTextView.append(System.lineSeparator());
                mMessageOutputTextView.append(System.lineSeparator());
            }
        }
    }
}
