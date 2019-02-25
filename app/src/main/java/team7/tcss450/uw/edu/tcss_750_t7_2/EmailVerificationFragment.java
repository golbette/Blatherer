package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnEmailVerificationFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EmailVerificationFragment extends Fragment {

    private OnEmailVerificationFragmentInteractionListener mListener;
    private Credentials mCredentials;

    public EmailVerificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_email_verification, container, false);

        Button b = (Button) v.findViewById(R.id.button_emailVerificationFragment_continue);
        b.setOnClickListener(this::continueClicked);



        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {

            mCredentials = (Credentials) getArguments().
                    getSerializable(getString(R.string.credential_key));
            updateContent(mCredentials);



        }
    }

    public void continueClicked(View view){
       mListener.onEmailVerificationFragmentInteraction(mCredentials);
    }

    public void updateContent(Credentials credentials) {
        TextView username = getActivity()
                .findViewById(R.id.text_emailVerifcationFragment_username);
        username.setText(credentials.getUsername());


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEmailVerificationFragmentInteractionListener) {
            mListener = (OnEmailVerificationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEmailVerificationFragmentInteractionListener");
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
    public interface OnEmailVerificationFragmentInteractionListener {
        // TODO: Update argument type and name
        void onEmailVerificationFragmentInteraction(Credentials credentials);
    }
}
