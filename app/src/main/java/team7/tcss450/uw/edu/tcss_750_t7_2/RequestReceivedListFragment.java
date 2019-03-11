package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Request;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnRequestReceivedListFragmentInteractionListener}
 * interface.
 */
public class RequestReceivedListFragment extends Fragment {
    public static final String ARG_REQUEST_LIST = "requests_list";
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_CREDENTIALS = "credentials";
    private static final String ARG_JWT = "jwt";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnRequestReceivedListFragmentInteractionListener mListener;
    private List<Request> mRequestReceived;
    private Credentials mCredentials;
    private String mJwToken;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RequestReceivedListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RequestReceivedListFragment newInstance(int columnCount) {
        RequestReceivedListFragment fragment = new RequestReceivedListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
           mRequestReceived  = new ArrayList<Request>(
                    Arrays.asList((Request[]) getArguments().
                            getSerializable(RequestReceivedListFragment.ARG_REQUEST_LIST)));
           mCredentials = (Credentials) getArguments()
                   .getSerializable(RequestReceivedListFragment.ARG_CREDENTIALS);
           mJwToken = (String) getArguments().getSerializable(RequestReceivedListFragment.ARG_JWT);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_received_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyRequestReceivedRecyclerViewAdapter(mRequestReceived, mListener, mCredentials, mJwToken));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRequestReceivedListFragmentInteractionListener) {
            mListener = (OnRequestReceivedListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRequestReceivedListFragmentInteractionListener");
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
    public interface OnRequestReceivedListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRequestReceivedListFragmentInteraction(Request item);
    }
}
