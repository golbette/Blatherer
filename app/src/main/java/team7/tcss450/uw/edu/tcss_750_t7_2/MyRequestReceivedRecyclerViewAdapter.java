package team7.tcss450.uw.edu.tcss_750_t7_2;


import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import team7.tcss450.uw.edu.tcss_750_t7_2.RequestReceivedListFragment.OnRequestReceivedListFragmentInteractionListener;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Request;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
import team7.tcss450.uw.edu.tcss_750_t7_2.utils.SendPostAsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 *Fragment handles the incoming contact request for the user.
 * The options are either to accept or deny the friend requests.
 */
public class MyRequestReceivedRecyclerViewAdapter extends RecyclerView.Adapter<MyRequestReceivedRecyclerViewAdapter.ViewHolder> {

    private final List<Request> mValues;
    private final OnRequestReceivedListFragmentInteractionListener mListener;
    private Credentials mCredentials;
    private String mJwToken;

    public MyRequestReceivedRecyclerViewAdapter(List<Request> items, OnRequestReceivedListFragmentInteractionListener listener,
                                                Credentials credentials, String jwt) {
        if(null ==items){
            items = new ArrayList<>();
        }
        mValues = items;
        mListener = listener;
        mCredentials = credentials;
        mJwToken = jwt;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_request_received_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mRequestContactName.setText(mValues.get(position).getContactName());

        /**Approve Contact request  */
        holder.mApproveReq.setOnClickListener(v -> {
            /**Mark the contact as verified in the contacts table in the backend */
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath("blatherer-service.herokuapp.com")
                    .appendPath("contacts")
                    .appendPath("connApprove")
                    .appendQueryParameter("email_a", holder.mItem.getmOtherEmail())
                    .appendQueryParameter("email_b", mCredentials.getEmail())
                    .build();
            new GetAsyncTask.Builder(uri.toString())
                    .onPostExecute(this::handleRequestSentAcceptGetOnPostExecute)
                    .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                    .build().execute();

            Log.wtf("Approved Friend request", mCredentials.getEmail() + " "+ holder.mItem.getmOtherEmail());
            /**Removed from the front end list */
            mValues.remove(position);
            this.notifyItemRemoved(position);
        });

        /**Deny contact request */
        holder.mDenyReq.setOnClickListener(v -> {
            /**Delete the request in backend */
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath("blatherer-service.herokuapp.com")
                    .appendPath("contacts")
                    .appendPath("cancel")
                    .appendQueryParameter("memberid_a", mValues.get(position).getmMemberId_b())
                    .appendQueryParameter("memberid_b", mValues.get(position).getmMemberId_a())
                    .build();
            new GetAsyncTask.Builder(uri.toString())
                    .onPostExecute(this::handleRequestSentDenyGetOnPostExecute)
                    .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                    .build().execute();
            Log.wtf("Denied Friend request", mCredentials.getEmail() + " " + holder.mItem.getmOtherEmail());
            Log.wtf("Denied Friend request", mValues.get(position).getmMemberId_b() + " "+ mValues.get(position).getmMemberId_a());

            /**
             * Remove the item in the front end.
             */
            mValues.remove(position);
            this.notifyItemRemoved(position);

        });


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onRequestReceivedListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mRequestContactName;
        public Request mItem;
        public Button mApproveReq;
        public Button mDenyReq;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mRequestContactName = (TextView) view.findViewById(R.id.request_contact_name);
            mApproveReq = view.findViewById(R.id.button_request_received_accept);
            mDenyReq = view.findViewById(R.id.button_request_received_deny);

        }

    }



    private void handleRequestSentAcceptGetOnPostExecute(String result){

        try{
            JSONObject response = new JSONObject(result);

            if(response.getBoolean("success")) {
                Log.wtf("Approved", "REQUEST");
            }

        } catch(JSONException e){
            Log.wtf("FAILED TO Approve", "Received REQUEST");
        }
    }

    private void handleRequestSentDenyGetOnPostExecute(String result) {


        try{
            JSONObject response = new JSONObject(result);

            if(response.getBoolean("success")) {
                Log.wtf("Denied", "REQUEST");
            }

        } catch(JSONException e){
            Log.wtf("FAILED TO Deny", "Received REQUEST");
        }
    }


}
