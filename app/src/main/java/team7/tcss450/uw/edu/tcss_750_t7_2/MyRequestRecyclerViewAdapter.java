package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import team7.tcss450.uw.edu.tcss_750_t7_2.RequestFragment.OnRequestListFragmentInteractionListener;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Request;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link} and makes a call to the
 * specified {@link}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRequestRecyclerViewAdapter extends RecyclerView.Adapter<MyRequestRecyclerViewAdapter.ViewHolder> {

    private final List<Request> mValues;
    private final OnRequestListFragmentInteractionListener mListener;

    public MyRequestRecyclerViewAdapter(List<Request> items, OnRequestListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mRequestInitials.setText(mValues.get(position).getInitials());
        holder.mRequestContactName.setText(mValues.get(position).getContactName());
        holder.mRequestType.setText(mValues.get(position).getmRequestType());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onRequestListFragmentInteraction(holder.mItem);
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
        public final TextView mRequestInitials;
        public final TextView mRequestContactName;
        public final TextView mRequestType;
        public Request mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mRequestInitials = (TextView) view.findViewById(R.id.request_initials);
            mRequestContactName = (TextView) view.findViewById(R.id.request_contact_name);
            mRequestType = (TextView) view.findViewById(R.id.request_type);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mRequestType.getText() + "'";
        }
    }
}
