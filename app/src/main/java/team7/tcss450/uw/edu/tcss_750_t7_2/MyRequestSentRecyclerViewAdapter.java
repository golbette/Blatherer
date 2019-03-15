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

import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Request;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;


import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link} and makes a call to the
 * specified {@link RequestSentListFragment.OnRequestSentListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRequestSentRecyclerViewAdapter extends RecyclerView.Adapter<MyRequestSentRecyclerViewAdapter.ViewHolder> {

    private final List<Request> mValues;
    private final RequestSentListFragment.OnRequestSentListFragmentInteractionListener mListener;
    private Credentials mCredentials;
    private String mJwToken;


    public MyRequestSentRecyclerViewAdapter(List<Request> items,
        RequestSentListFragment.OnRequestSentListFragmentInteractionListener listener,
                                           Credentials credentials, String jwt) {
        if(null == items){
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
                .inflate(R.layout.fragment_request_sent_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mRequestContactName.setText(mValues.get(position).getContactName());

        holder.mCancel.setOnClickListener(v -> {
            /**Delete the request in backend */
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath("blatherer-service.herokuapp.com")
                    .appendPath("contacts")
                    .appendPath("cancel")
                    .appendQueryParameter("memberid_b", holder.mItem.getmMemberId_a())
                    .appendQueryParameter("memberid_a", holder.mItem.getmMemberId_b())
                    .build();
            new GetAsyncTask.Builder(uri.toString())
                    .onPostExecute(this::handleRequestSentCancelGetOnPostExecute)
                    .addHeaderField("authorization", mJwToken) // Add the JWT as a header
                    .build().execute();
            Log.wtf("Cancelled Friend request", mCredentials.getEmail() + " "+ holder.mItem.getmOtherEmail());

            /**
             * Remove the item in the front end.
             */
            if(position == 1 && getItemCount() == 1) {
              mValues.remove(0);

            } else {
                mValues.remove(position);
            }
            this.notifyItemRemoved(position);







                });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onRequestSentListFragmentInteraction(v, holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(null == mValues){
            return 0;
        }
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mRequestContactName;
        public Request mItem;
        public Button mCancel;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mRequestContactName = (TextView) view.findViewById(R.id.request_contact_name);
            mCancel =  view.findViewById(R.id.button_request_sent_fragment_cancel);
        }
    }

    private void handleRequestSentCancelGetOnPostExecute(String result){

        try{
            JSONObject response = new JSONObject(result);

            if(response.getBoolean("success")) {
                Log.wtf("REMOVED", "REQUEST");
            }

        } catch(JSONException e){
            Log.wtf("FAILED TO CANCEL", "SENT REQUEST" + " " + e);
        }

    }


}
