package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Request;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnRequestSentListFragmentInteractionListener}
 * interface.
 */
public class RequestSentListFragment extends Fragment {
    public static final String ARG_REQUEST_LIST = "requests_list";
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_CREDENTIALS = "credentials";
    private static final String ARG_JWT = "jwt";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnRequestSentListFragmentInteractionListener mListener;
    private List<Request> mRequestSent;
    private Credentials mCredentials;
    private String mJwToken;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RequestSentListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RequestSentListFragment newInstance(int columnCount) {
        RequestSentListFragment fragment = new RequestSentListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mRequestSent = new ArrayList<Request>(
                    Arrays.asList((Request[]) getArguments().
                            getSerializable(RequestSentListFragment.ARG_REQUEST_LIST)));
            mCredentials = (Credentials) getArguments().getSerializable(RequestSentListFragment.ARG_CREDENTIALS);
            mJwToken = (String) getArguments().getSerializable(RequestSentListFragment.ARG_JWT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_sent_list, container, false);



        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

            }
            recyclerView.setAdapter(new MyRequestSentRecyclerViewAdapter(mRequestSent, mListener,
                    mCredentials, mJwToken));

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRequestSentListFragmentInteractionListener) {
            mListener = (OnRequestSentListFragmentInteractionListener) context;
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
    public interface OnRequestSentListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRequestSentListFragmentInteraction(View v, Request item);
    }

}
