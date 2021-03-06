package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;
import team7.tcss450.uw.edu.tcss_750_t7_2.ContactFragment.OnContactListFragmentInteractionListener;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Contact;
/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnContactListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyContactRecyclerViewAdapter extends RecyclerView.Adapter<MyContactRecyclerViewAdapter.ViewHolder> {

    private final List<Contact> mValues;
    private final OnContactListFragmentInteractionListener mListener;

    public MyContactRecyclerViewAdapter(List<Contact> items, OnContactListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContactName.setText(mValues.get(position).getContactName());
        holder.mContactInitials.setText(mValues.get(position).getInitials());
        holder.mContactUsername.setText(mValues.get(position).getmUsername());
        holder.mContactEmail.setText(mValues.get(position).getEmail());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    try {
                        mListener.onContactListFragmentInteraction(holder.mItem);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        public final TextView mContactName;
        public final TextView mContactEmail;
        public final TextView mContactInitials;
        public final TextView mContactUsername;
        public Contact mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContactName = (TextView) view.findViewById(R.id.contact_contact_name);
            mContactInitials = (TextView) view.findViewById(R.id.contact_contact_initials);
            mContactEmail = (TextView) view.findViewById(R.id.contact_email);
            mContactUsername = (TextView) view.findViewById(R.id.contact_username);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContactName.getText() + "'";
        }
    }
}
