package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.content.Context;
import android.net.Credentials;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChangePasswordFragment.OnChangePasswordFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChangePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePasswordFragment extends Fragment {

    private OnChangePasswordFragmentInteractionListener mListener;
    private team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials mCredentials;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCredentials = (team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials) getArguments().getSerializable("credentials");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        Button confirmButt = view.findViewById(R.id.change_pw_confirm_butt);
        confirmButt.setOnClickListener(this::resetConfirm);

        return view;
    }

    public void resetConfirm(View view) {
        boolean pass = true;

        EditText oldpassword = (EditText) getActivity().findViewById(R.id.change_pw_et_old_pw);
        EditText newpassword = (EditText) getActivity().findViewById(R.id.change_pw_et_new_pw);
        EditText repeatnewpassword = (EditText) getActivity().findViewById(R.id.change_pw_et_repeat_pw);

        String oldpwstr = oldpassword.getText().toString();
        String newpwstr = newpassword.getText().toString();
        String repeatnewpwstr = repeatnewpassword.getText().toString();

        if (oldpwstr.isEmpty()) {
            oldpassword.setError("This field cannot be empty");
            pass = false;
        } else if (!oldpwstr.equals(mCredentials.getPassword())) {
            oldpassword.setError("Old password entered is incorrect");
            pass = false;
        }

        if (newpwstr.isEmpty()) {
            newpassword.setError("This field cannot be empty");
            pass = false;
        } else if (newpassword.length() <= 5) {
            newpassword.setError("Password must be 6 characters or longer");
            pass = false;
        }

        if (repeatnewpwstr.isEmpty()) {
            repeatnewpassword.setError("This field cannot be empty");
            pass = false;
        } else if (repeatnewpwstr.length() <= 5) {
            repeatnewpassword.setError("Password must be 6 characters or longer");
            pass = false;
        }

        if (!repeatnewpwstr.equals(newpwstr)) {
            repeatnewpassword.setError("Passwords do not match");
            pass = false;
        }

        if (pass == true) {
            mListener.onResetPassword(oldpwstr, newpwstr);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChangePasswordFragmentInteractionListener) {
            mListener = (OnChangePasswordFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnChangePasswordFragmentInteractionListener {
        void onResetPassword(String oldpassword, String newpassword);
    }
}
