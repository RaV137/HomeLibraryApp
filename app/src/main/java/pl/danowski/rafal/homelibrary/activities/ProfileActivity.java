package pl.danowski.rafal.homelibrary.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.dialogs.ChangeEmailDialog;
import pl.danowski.rafal.homelibrary.dialogs.ChangePasswordDialog;
import pl.danowski.rafal.homelibrary.exceptions.NoNetworkConnectionException;
import pl.danowski.rafal.homelibrary.model.user.User;
import pl.danowski.rafal.homelibrary.network.BaseAsyncTask;
import pl.danowski.rafal.homelibrary.network.email.SendEmailTask;
import pl.danowski.rafal.homelibrary.services.UserService;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;
import pl.danowski.rafal.homelibrary.utiities.toast.NoNetworkConnectionToast;

public class ProfileActivity extends AppCompatActivity {

    private TextView mEmailView;
    private UserService mUserService;

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

        mUserService = UserService.getInstance();
        mEmailView = findViewById(R.id.email);

        UserCheckTask task = new UserCheckTask(login, this);
        task.execute((Void) null);

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
                        DeleteAccountTask task = new DeleteAccountTask(getBaseContext()); // TODO obsługa błędów internetu
                        task.execute((Void) null);
                        SharedPreferencesUtilities.deleteLogin(getApplicationContext());
                        SharedPreferencesUtilities.setAutologin(getApplicationContext(), false);
                        finish();
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

    private class DeleteAccountTask extends BaseAsyncTask<Void, Void, Void> {

        private DeleteAccountTask(Context context) {
            super(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String login = SharedPreferencesUtilities.getLogin(getApplicationContext());
            User user = null;
            try {
                user = mUserService.findUserByLogin(mContext, login);
            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
                return null;
            }
            String email = user.getEmail();
            sendEmailAccountDeleted(email);
            try {
                mUserService.deleteUser(mContext, login);
            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
            }
            return null;
        }
    }

    private void sendEmailAccountDeleted(final String email) {
        final String body = "Witaj!\nWłaśnie usunąłeś swoje konto w aplikacji HomeLibrary." +
                "\n\nDziękujemy Ci za czas, który poświęciłeś z naszą aplikacją, mamy nadzieję, że dobrze się bawiłeś!" +
                "\n\nPozdrawiamy\nZespół HomeLibrary";
        final String subject = "Usunięcie konta w aplikacji HomeLibrary";
        SendEmailTask task = new SendEmailTask(body, subject, email, this);
        task.execute((Void) null);
    }

    private void changeEmail() {
        ChangeEmailTask task = new ChangeEmailTask(this);
        task.execute((Void) null);
    }

    private void changePassword() {
        DialogFragment dialog = new ChangePasswordDialog();
        ((ChangePasswordDialog) dialog).setMContext(this);
        dialog.show(getFragmentManager(), "ChangePasswordDialog");
    }

    private class ChangeEmailTask extends BaseAsyncTask<Void, Void, Void> {

        ChangeEmailTask(Context context) {
            super(context);
        }

        @Override
        protected Void doInBackground(Void... params) {
            ChangeEmailDialog dialog = new ChangeEmailDialog();
            dialog.setMContext(mContext);
            dialog.setDialogResult(new ChangeEmailDialog.OnMyDialogResult() {
                public void finish(boolean success, String email) {
                    if (success) {
                        mEmailView.setText(email);
                    }
                }
            });
            dialog.show(getFragmentManager(), "ChangePasswordDialog");
            return null;
        }

    }

    private class UserCheckTask extends BaseAsyncTask<Void, Void, Void> {

        private String login;

        public UserCheckTask(String login, Context mContext) {
            super(mContext);
            this.login = login;
        }

        @Override
        protected Void doInBackground(Void... params) {
            super.doInBackground(params);
            User user = null;
            try {
                user = mUserService.findUserByLogin(mContext, login);
            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
                return null;
            }
            assert user != null : ("Brak użytkownika o danym loginie: " + login);
            mEmailView.setText(user.getEmail());
            return null;
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
