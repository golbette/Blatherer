package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import team7.tcss450.uw.edu.tcss_750_t7_2.NamesByChatIdFragment.OnRecentChatListFragmentInteractionListener;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.NamesByChatId;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnRecentChatListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyNamesByChatIdRecyclerViewAdapter extends RecyclerView.Adapter<MyNamesByChatIdRecyclerViewAdapter.ViewHolder> {

    private final List<NamesByChatId> mValues;
    private final OnRecentChatListFragmentInteractionListener mListener;

    public MyNamesByChatIdRecyclerViewAdapter(List<NamesByChatId> items, OnRecentChatListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_namesbychatid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContactName.setText(mValues.get(position).getmNames());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    try {
                        mListener.onRecentChatListFragmentInteraction(holder.mItem);
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
        public NamesByChatId mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContactName = (TextView) view.findViewById(R.id.chats_contact_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContactName.getText() + "'";
        }
    }
}
