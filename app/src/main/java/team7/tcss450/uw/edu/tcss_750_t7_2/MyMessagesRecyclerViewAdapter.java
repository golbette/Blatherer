package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import team7.tcss450.uw.edu.tcss_750_t7_2.MessageFragment.OnMessageListFragmentInteractionListener;
import team7.tcss450.uw.edu.tcss_750_t7_2.dummy.DummyContent.DummyItem;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Message;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link MessageFragment.OnMessageListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMessagesRecyclerViewAdapter extends RecyclerView.Adapter<MyMessagesRecyclerViewAdapter.ViewHolder> {

    private final List<Message> mValues;
    private final MessageFragment.OnMessageListFragmentInteractionListener mListener;

    public MyMessagesRecyclerViewAdapter(List<Message> items, OnMessageListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_messages, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.mUsername.setText(mValues.get(position).getUsername());
//        holder.mMessage.setText(mValues.get(position).getMessage());
//        holder.mTimestamp.setText(mValues.get(position).getTimestamp());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onMessageListFragmentInteraction(holder.mItem);
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
//        public final TextView mUsername;
//        public final TextView mMessage;
//        public final TextView mTimestamp;
        public Message mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
//            mUsername = (TextView) view.findViewById(R.id.msg_contact_name);
//            mMessage = (TextView) view.findViewById(R.id.msg_contact_initials);
//            mTimestamp = (TextView) view.findViewById(R.id.msg_latest);
        }

        @Override
        public String toString() {
            return super.toString() + " '";// + mTimestamp.getText() + "'";
        }
    }
}
