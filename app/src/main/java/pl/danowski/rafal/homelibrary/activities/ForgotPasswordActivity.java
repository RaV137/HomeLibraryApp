package pl.danowski.rafal.homelibrary.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.controllers.UserController;
import pl.danowski.rafal.homelibrary.network.email.GMailSender;
import pl.danowski.rafal.homelibrary.utiities.PasswordEncrypter;
import pl.danowski.rafal.homelibrary.utiities.PasswordGenerator;
import pl.danowski.rafal.homelibrary.utiities.enums.IntentExtras;

public class ForgotPasswordActivity extends AppCompatActivity {


    private UserController mUserController;

    private ForgotPasswordTask mAuthTask = null;

    private EditText mLoginView;
    private EditText mEmailView;
    private View mProgressView;
    private View mForgotPasswordFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setTitle("Przypomnienie hasła");

        String login = getIntent().getStringExtra(IntentExtras.LOGIN.getName());
        mLoginView = findViewById(R.id.textLogin);
        mEmailView = findViewById(R.id.textEmail);
        if (login != null && login.length() > 0) {
            mLoginView.setText(login);
        }

        mForgotPasswordFormView = findViewById(R.id.forgot_password_form);
        mProgressView = findViewById(R.id.forgot_password_progress);
        mUserController = new UserController();

        Button mForgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        mForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });
    }

    private void forgotPassword() {
        if (mAuthTask != null) {
            return;
        }

        mLoginView.setError(null);
        mEmailView.setError(null);

        final String login = mLoginView.getText().toString();
        final String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(login)) {
            mLoginView.setError("Pole wymagane");
            focusView = mLoginView;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Pole wymagane");
            focusView = mEmailView;
            cancel = true;
        } else if (!isValidEmailFormat(email)) {
            mEmailView.setError("Zły format adresu email");
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new ForgotPasswordTask(login, email);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isValidEmailFormat(final String email) {
        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public class ForgotPasswordTask extends AsyncTask<Void, Void, Boolean> {

        private final String login;
        private final String email;

        ForgotPasswordTask(String login, String email) {
            this.login = login;
            this.email = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = mUserController.isUserRegistered(getBaseContext(), login, email);

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                final String password = generateNewPassword();
                SendEmailTask sendEmailTask = new SendEmailTask(email, password);
                sendEmailTask.execute((Void) null);
                UpdatePasswordTask updatePasswordTask = new UpdatePasswordTask(login, password);
                updatePasswordTask.execute((Void) null);
                showConfirmationToast();
                finish();
            } else {
                mEmailView.setError("Brak konta z danym loginem i adresem email");
                mEmailView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void showConfirmationToast() {
        Toast.makeText(this, "Udało się zresetować hasło użytkownika", Toast.LENGTH_SHORT).show();
    }

    public class UpdatePasswordTask extends AsyncTask<Void, Void, Void> {

        private final String login;
        private final String password;

        public UpdatePasswordTask(String login, String password) {
            this.login = login;
            this.password = password;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            updatePassword(login, password);
            return null;
        }
    }

    private void updatePassword(final String login, final String password) {
        final String encryptedPassword = PasswordEncrypter.md5(password);
        mUserController.updateUserPassword(this, login, encryptedPassword);
    }

    public class SendEmailTask extends AsyncTask<Void, Void, Void> {

        private final String email;
        private final String password;

        public SendEmailTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            sendEmailWithNewCredentials(email, password);
            return null;
        }
    }

    private void sendEmailWithNewCredentials(final String email, final String password) {
        try {
            GMailSender sender = new GMailSender();
            sender.sendMail("Przypomnienie hasła w aplikacji HomeLibrary",
                    "Witaj!\nWłaśnie wygenerowaliśmy Ci nowe hasło do aplikacji HomeLibrary." +
                            "\n\nTwoje nowe hasło: " + password +
                            "\n\nJeśli to nie Ty resetowałeś hasło w aplikacji, zignoruj tego maila.\nPozdrawiamy\nZespół HomeLibrary",
                    "home.library.dev@gmail.com",
                    email);
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong, contact me with email home.library.dev@gmail.com", Toast.LENGTH_LONG).show();
            Log.e("SendMail", e.getMessage(), e);
            throw new RuntimeException("sendAnEmailWithLoginCredentials: " + e.getMessage());
        }
    }

    private String generateNewPassword() {
        return PasswordGenerator.generate(12);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mForgotPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mForgotPasswordFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mForgotPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
