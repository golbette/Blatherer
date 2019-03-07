package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import team7.tcss450.uw.edu.tcss_750_t7_2.NewContactFragment.OnNewContactListFragmentInteractionListener;
import team7.tcss450.uw.edu.tcss_750_t7_2.dummy.DummyContent.DummyItem;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.NewContact;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyNewContactRecyclerViewAdapter extends RecyclerView.Adapter<MyNewContactRecyclerViewAdapter.ViewHolder> {

    private final List<NewContact> mValues;
    private final OnNewContactListFragmentInteractionListener mListener;

    public MyNewContactRecyclerViewAdapter(List<NewContact> items, OnNewContactListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_new_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNewContactName.setText(mValues.get(position).getContactName());
        holder.mNewContactInitials.setText(mValues.get(position).getInitials());
        holder.mNewContactEmail.setText(mValues.get(position).getEmail());
        holder.mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRequestSent(mValues.get(position).getEmail());
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onNewContactListFragmentInteraction(holder.mItem);
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
        public final TextView mNewContactName;
        public final TextView mNewContactInitials;
        public final TextView mNewContactEmail;
        public final Button mSendButton;

        public NewContact mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNewContactName = (TextView) view.findViewById(R.id.new_contact_contact_name);
            mNewContactInitials = (TextView) view.findViewById(R.id.new_contact_contact_initials);
            mNewContactEmail = (TextView) view.findViewById(R.id.new_contact_email);
            mSendButton = (Button) view.findViewById(R.id.new_contact_butt_send_request);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNewContactName.getText() + "'";
        }
    }
}
