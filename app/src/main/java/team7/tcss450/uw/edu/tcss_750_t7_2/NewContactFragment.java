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
import android.widget.ImageButton;
import android.widget.TextView;

import team7.tcss450.uw.edu.tcss_750_t7_2.dummy.DummyContent;
import team7.tcss450.uw.edu.tcss_750_t7_2.dummy.DummyContent.DummyItem;
import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.NewContact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnNewContactListFragmentInteractionListener}
 * interface.
 */
public class NewContactFragment extends Fragment {
    public static final String ARG_NEW_CONTACT_LIST = "new contacts lists";
    private List<NewContact> mNewContacts;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnNewContactListFragmentInteractionListener mListener;
    private boolean mSearch = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewContactFragment() {
    }

    @SuppressWarnings("unused")
    public static NewContactFragment newInstance(int columnCount) {
        NewContactFragment fragment = new NewContactFragment();
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
            mNewContacts = new ArrayList<NewContact>(Arrays.asList((NewContact[]) getArguments().getSerializable(ARG_NEW_CONTACT_LIST)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_contact_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.new_contact_list);

        ImageButton imageButton = view.findViewById(R.id.new_contact_search);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSearchClicked();
            }
        });

//        if (mNewContacts.isEmpty()) {
//            mListener.onNoResults();
//        } else {
            // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyNewContactRecyclerViewAdapter(mNewContacts, mListener));
        }
//        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewContactListFragmentInteractionListener) {
            mListener = (OnNewContactListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRequestSentListFragmentInteractionListener");
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
    public interface OnNewContactListFragmentInteractionListener {
        void onNewContactListFragmentInteraction(NewContact item);
        void onSearchClicked();
        void onNoResults();
        void onRequestSent(String email);
    }
}
