package pl.danowski.rafal.homelibrary.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.controllers.UserController;
import pl.danowski.rafal.homelibrary.controllers.interfaces.IUserController;
import pl.danowski.rafal.homelibrary.model.User;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;

public class ProfileActivity extends AppCompatActivity {

    private TextView mEmailView;
    private IUserController mUserController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null : "ActionBar is null!";
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Home library");

        String login = SharedPreferencesUtilities.getLogin(this);
        TextView mLoginView = findViewById(R.id.login);
        mLoginView.setText(login);

        mUserController = new UserController();

        mEmailView = findViewById(R.id.email);
        User user = mUserController.findUserByLogin(this, login, isOnline());
        assert user != null : ("Brak użytkownika o danym loginie: " + login);
        mEmailView.setText(user.getEmail());

        Button mChangePasswordButton = findViewById(R.id.changePassword);
        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
        Button mChangeEmailButton = findViewById(R.id.changeEmail);
        mChangeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeEmail();
            }
        });
        Button mDeleteAccountButton = findViewById(R.id.deleteAccount);
        mDeleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount();
            }
        });
    }

    private void deleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Usuń konto")
                .setMessage("Czy na pewno chcesz usunąć konto? Tej decyzji nie można cofnąć.")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String login = SharedPreferencesUtilities.getLogin(getBaseContext());
                        mUserController.deleteUser(getBaseContext(), login, isOnline());
                        SharedPreferencesUtilities.deleteLogin(getBaseContext());
                        SharedPreferencesUtilities.setAutologin(getBaseContext(), false);
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .create()
                .show();
    }

    private void changeEmail() {

    }

    private void changePassword() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr != null ? connMgr.getActiveNetworkInfo() : null;
        return (networkInfo != null && networkInfo.isConnected());
    }
}
