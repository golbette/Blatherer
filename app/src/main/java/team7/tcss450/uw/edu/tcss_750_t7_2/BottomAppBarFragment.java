package team7.tcss450.uw.edu.tcss_750_t7_2;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;



/**
 * A simple {@link Fragment} subclass.
 */
public class BottomAppBarFragment extends Fragment {
    private OnBottomNavFragmentInteractionListener mListener;

    public BottomAppBarFragment() {
        // Required empty public constructor
    }


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }


    @Override
    public void onStart() {
        super.onStart();
        ImageButton butt = (ImageButton) getActivity().findViewById(R.id.bottom_bar_recents);
        Log.wtf("BUTTONTAG", butt.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_bottom_app_bar, container, false);

        ImageButton butt = (ImageButton) getActivity().findViewById(R.id.bottom_bar_recents);
//        Log.wtf("BUTTONTAG", butt.toString());
        butt.setOnClickListener(this::showRecent);

        butt = (ImageButton) getActivity().findViewById(R.id.bottom_bar_contacts);
        butt.setOnClickListener(this::showContact);

        butt = (ImageButton) getActivity().findViewById(R.id.bottom_bar_requests);
        butt.setOnClickListener(this::showRequest);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBottomNavFragmentInteractionListener) {
            mListener = (OnBottomNavFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBottomNavFragmentInteractionListener");
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
    public interface OnBottomNavFragmentInteractionListener {
        // TODO: Update argument type and name
        void recentClicked();
        void contactClicked();
        void requestClicked();
    }

    private void showContact(View view) {
        if (mListener != null) {
            mListener.contactClicked();
        }
    }

    private void showRecent(View view) {
        if (mListener != null) {
            mListener.recentClicked();
        }
    }

    private void showRequest(View view) {
        if (mListener != null) {
            mListener.requestClicked();
        }
    }
}
