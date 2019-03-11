package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import team7.tcss450.uw.edu.tcss_750_t7_2.dummy.DummyContent;
//import team7.tcss450.uw.edu.tcss_750_t7_2.dummy.DummyContent.DummyItem;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.NamesByChatId;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnRecentChatListFragmentInteractionListener}
 * interface.
 */
public class NamesByChatIdFragment extends Fragment {
    public static final String ARG_RECENT_CHATS_LIST = "recent chats list";
    private List<NamesByChatId> mRecentChats;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private String mUsername;
    private OnRecentChatListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NamesByChatIdFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NamesByChatIdFragment newInstance(int columnCount) {
        NamesByChatIdFragment fragment = new NamesByChatIdFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mRecentChats = new ArrayList<NamesByChatId>(Arrays.asList((NamesByChatId[]) getArguments().getSerializable(ARG_RECENT_CHATS_LIST)));
            mUsername = getArguments().getString("username");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_namesbychatid_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.RecentsList);

        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
//            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyNamesByChatIdRecyclerViewAdapter(mRecentChats, mListener, mUsername));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecentChatListFragmentInteractionListener) {
            mListener = (OnRecentChatListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecentChatListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRecentChatListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRecentChatListFragmentInteraction(NamesByChatId item, String username) throws JSONException;
    }
}
