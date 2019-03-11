package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.content.Context;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import team7.tcss450.uw.edu.tcss_750_t7_2.messaging.Request;
import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnRequestContainerFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RequestContainer extends Fragment {
    public static final String ARG_RECEIVED_REQUEST = "received";
    public static final String ARG_SENT_REQUEST = "sent";
    public static final String ARG_CREDS = "credentials";
    public static final String ARG_JWT = "jwt";

    private OnRequestContainerFragmentInteractionListener mListener;
    private RequestReceivedListFragment.OnRequestReceivedListFragmentInteractionListener mReceivedListener;
    private RequestSentListFragment.OnRequestSentListFragmentInteractionListener mSentListener;

    private List<Request> mReceivedRequests;
    private List<Request> mSentRequests;
    private Credentials mCredentials;
    private String mJwtToken;

    private RecyclerView mReceivedReqRecyclerView;
    private RecyclerView mSentReqRecyclerView;

    public RequestContainer(){}

    public static RequestContainer newInstance(int columnCount){
        RequestContainer requestContainer = new RequestContainer();
        return requestContainer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mReceivedRequests = (ArrayList<Request>) getArguments()
                    .getSerializable(ARG_RECEIVED_REQUEST);
            mSentRequests = (ArrayList<Request>) getArguments()
                    .getSerializable(ARG_SENT_REQUEST);
            mCredentials = (Credentials) getArguments().getSerializable(ARG_CREDS);
            mJwtToken = (String) getArguments().getSerializable(ARG_JWT);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_request_container, container, false);
        Context context = v.getContext();

        mReceivedReqRecyclerView = v.findViewById(R.id.recycler_fragment_request_received);
        mSentReqRecyclerView = v.findViewById(R.id.recycler_fragment_request_sent);

        RecyclerView.LayoutManager receivedReqLayoutManager = new LinearLayoutManager(context);
        RecyclerView.LayoutManager sentReqLayoutManager = new LinearLayoutManager(context);

        mReceivedReqRecyclerView.setLayoutManager(receivedReqLayoutManager);
        mSentReqRecyclerView.setLayoutManager(sentReqLayoutManager);

        MyRequestReceivedRecyclerViewAdapter requestReceivedRecyclerViewAdapter
                = new MyRequestReceivedRecyclerViewAdapter(mReceivedRequests, mReceivedListener, mCredentials, mJwtToken);

        MyRequestSentRecyclerViewAdapter sentReceivedRecyclerViewAdapter
                = new MyRequestSentRecyclerViewAdapter(mSentRequests, mSentListener, mCredentials, mJwtToken);

        mReceivedReqRecyclerView.setAdapter(requestReceivedRecyclerViewAdapter);
        mSentReqRecyclerView.setAdapter(sentReceivedRecyclerViewAdapter);


        return v;
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRequestContainerFragmentInteractionListener) {
            mListener = (OnRequestContainerFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRequestContainerFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRequestContainerFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRequestContainerFragmentInteraction(View View);
    }
}
