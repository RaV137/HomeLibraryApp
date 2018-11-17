package pl.danowski.rafal.homelibrary.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.controllers.UserController;
import pl.danowski.rafal.homelibrary.dialogs.ChangeEmailDialog;
import pl.danowski.rafal.homelibrary.dialogs.ChangePasswordDialog;
import pl.danowski.rafal.homelibrary.model.User;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;

public class ProfileActivity extends AppCompatActivity {

    private TextView mEmailView;
    private UserController mUserController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null : "ActionBar is null!";
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Home library");

        String login = SharedPreferencesUtilities.getLogin(getApplicationContext());
        TextView mLoginView = findViewById(R.id.login);
        mLoginView.setText(login);

        mUserController = new UserController();

        mEmailView = findViewById(R.id.email);
        User user = mUserController.findUserByLogin(this, login);
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
                        String login = SharedPreferencesUtilities.getLogin(getApplicationContext());
                        mUserController.deleteUser(getBaseContext(), login);
                        SharedPreferencesUtilities.deleteLogin(getApplicationContext());
                        SharedPreferencesUtilities.setAutologin(getApplicationContext(), false);
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
        ChangeEmailDialog dialog = new ChangeEmailDialog();
        dialog.setDialogResult(new ChangeEmailDialog.OnMyDialogResult(){
            public void finish(boolean success, String email){
                if (success) {
                    mEmailView.setText(email);
                }
            }
        });
        dialog.show(getFragmentManager(), "ChangePasswordDialog");
//        mEmailView.setText(mUserController.findUserByLogin(getBaseContext(), SharedPreferencesUtilities.getLogin(getApplicationContext())).getEmail());
    }

    private void changePassword() {
        DialogFragment dialog = new ChangePasswordDialog();
        dialog.show(getFragmentManager(), "ChangePasswordDialog");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
