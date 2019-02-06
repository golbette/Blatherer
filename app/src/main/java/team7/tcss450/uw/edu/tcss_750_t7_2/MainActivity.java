package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.Serializable;

import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;

/**
 * Maurice was here. Pull request sent.
 */
public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener, RegisterFragment.OnRegisterFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
            Gio was here, still trying to figure out
            how to commit to my own branch instead of master
         */
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            if (findViewById(R.id.activity_main_container) != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_container, new LoginFragment()).commit();
            }
        }
    }

    @Override
    public void onLoginSuccess(Credentials credentials, String jwt) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(getString(R.string.keys_intent_credentials), (Serializable) credentials);
        intent.putExtra(getString(R.string.keys_intent_jwt), jwt);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRegisterClicked() {
        RegisterFragment registerFragment = new RegisterFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_container, registerFragment).addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onRegisterSuccess(Credentials credentials) {
        LoginFragment loginFragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.credential_key), credentials);
        loginFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_container, loginFragment);
        transaction.commit();
    }


    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_container, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }
}
/**
*Luke -> how did we get upgraded to a 700 level class lol
*/