package pl.danowski.rafal.homelibrary.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.FormatException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.controllers.LoginRegistrationController;
import pl.danowski.rafal.homelibrary.controllers.interfaces.ILoginRegistrationController;
import pl.danowski.rafal.homelibrary.network.email.GMailSender;
import pl.danowski.rafal.homelibrary.utiities.PasswordEncrypter;
import pl.danowski.rafal.homelibrary.utiities.enums.IntentExtras;
import pl.danowski.rafal.homelibrary.utiities.enums.RegistrationResult;

public class RegisterActivity extends AppCompatActivity {

    private ILoginRegistrationController loginRegistrationController;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegistrationTask mAuthTask = null;

    private EditText mLoginView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;

    private View mProgressView;
    private View mRegistrationFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("Rejestracja");

        this.loginRegistrationController = new LoginRegistrationController();
        String login = getIntent().getStringExtra(IntentExtras.LOGIN.getName());

        mLoginView = findViewById(R.id.textLogin);
        mPasswordView = findViewById(R.id.textPassword);
        mConfirmPasswordView = findViewById(R.id.textConfirmPassword);
        mEmailView = findViewById(R.id.textEmail);

        if (login != null && login.length() > 0) {
            mLoginView.setText(login);
        }

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        mRegistrationFormView = findViewById(R.id.registration_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRegistrationFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private void attemptRegistration() {
        if (mAuthTask != null) {
            return;
        }

        mLoginView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
        mEmailView.setError(null);

        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();
        String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(login)) {
            mLoginView.setError("Pole wymagane");
            focusView = mLoginView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Pole wymagane");
            focusView = mPasswordView;
            cancel = true;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            mConfirmPasswordView.setError("Pole wymagane");
            focusView = mConfirmPasswordView;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Pole wymagane");
            focusView = mEmailView;
            cancel = true;
        } else if (!password.equals(confirmPassword)) {
            mConfirmPasswordView.setError("Hasła muszą być takie same");
            focusView = mConfirmPasswordView;
            cancel = true;
        } else if (!isValidEmail(email)) {
            mEmailView.setError("Zły format adresu email");
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserRegistrationTask(login, email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isValidEmail(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    private void sendAnEmailWithLoginCredentials(final String login, final String email, final String password) {
        try {
            GMailSender sender = new GMailSender();
            sender.sendMail("Rejestracja konta w aplikacji HomeLibrary",
                    "Witaj!\nWłaśnie zarejestrowałeś się w aplikacji HomeLibrary." +
                            "\n\nTwoje dane logowania:\nLogin: " + login + "\nHasło: " + password + "" +
                            "\n\nJeśli to nie Ty się rejestrowałeś w aplikacji, zignoruj tego maila.\nPozdrawiamy\nZespół HomeLibrary",
                    "home.library.dev@gmail.com",
                    email);
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
            throw new RuntimeException("sendAnEmailWithLoginCredentials: " + e.getMessage());
        }
    }

    public class SendEmailTask extends AsyncTask<Void, Void, Void> {

        private final String login;
        private final String email;
        private final String password;

        SendEmailTask(String login, String email, String password) {
            this.login = login;
            this.email = email;
            this.password = password;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            sendAnEmailWithLoginCredentials(login, email, password);

            return null;
        }
    }

    public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {

        private final String login;
        private final String email;
        private final String password;
        private final String encryptedPassword;

        private RegistrationResult registrationResult;

        UserRegistrationTask(String login, String email, String password) {
            this.login = login;
            this.email = email;
            this.password = password;
            encryptedPassword = PasswordEncrypter.md5(password);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            registrationResult = loginRegistrationController.attemptRegistration(getBaseContext(), login, email, encryptedPassword, isOnline());

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            return registrationResult.equals(RegistrationResult.SUCCESS);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                SendEmailTask task = new SendEmailTask(login, email, password);
                task.execute((Void) null);

//                sendAnEmailWithLoginCredentials(login, email, password);
                finish();
            } else {
                switch (registrationResult) {
                    case CONNECTION_ERROR:
//                        Toast.makeText(getBaseContext(), registrationResult.getText(), Toast.LENGTH_LONG).show();
                        break;
                    case LOGIN_ALREADY_EXISTS:
                        mLoginView.setError(registrationResult.getText());
                        mLoginView.requestFocus();
                        break;
                    case EMAIL_ALREADY_EXISTS:
                        mEmailView.setError(registrationResult.getText());
                        mEmailView.requestFocus();
                        break;
                    default:
//                        Toast.makeText(getBaseContext(), registrationResult.getText(), Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr != null ? connMgr.getActiveNetworkInfo() : null;
        return (networkInfo != null && networkInfo.isConnected());
    }
}
