package pl.danowski.rafal.homelibrary.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.network.email.GMailSender;
import pl.danowski.rafal.homelibrary.network.email.SendEmailTask;
import pl.danowski.rafal.homelibrary.services.UserService;
import pl.danowski.rafal.homelibrary.utiities.PasswordEncrypter;
import pl.danowski.rafal.homelibrary.utiities.enums.IntentExtras;
import pl.danowski.rafal.homelibrary.utiities.enums.RegistrationResult;
import pl.danowski.rafal.homelibrary.utiities.validators.Validator;

public class RegisterActivity extends AppCompatActivity {

    private UserService mUserService;

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

        this.mUserService = new UserService();
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
        mProgressView = findViewById(R.id.forgot_password_progress);
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

        final String login = mLoginView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final String confirmPassword = mConfirmPasswordView.getText().toString();
        final String email = mEmailView.getText().toString();

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
        } else if (!Validator.isValidLoginFormat(login)) {
            mLoginView.setError(getString(R.string.rule_login));
            focusView = mLoginView;
            cancel = true;
        } else if (!Validator.isValidPasswordFormat(password)) {
            mPasswordView.setError(getString(R.string.rule_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!password.equals(confirmPassword)) {
            mConfirmPasswordView.setError("Hasła muszą być takie same");
            focusView = mConfirmPasswordView;
            cancel = true;
        } else if (!Validator.isValidEmailFormat(email)) {
            mEmailView.setError(getString(R.string.rule_email));
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

    private void sendAnEmailWithLoginCredentials(final String login, final String email, final String password) {
        final String body = "Witaj!\nWłaśnie zarejestrowałeś się w aplikacji HomeLibrary." +
                "\n\nTwoje dane logowania:\nLogin: " + login + "\nHasło: " + password + "" +
                "\n\nJeśli to nie Ty się rejestrowałeś w aplikacji, zignoruj tego maila.\nPozdrawiamy\nZespół HomeLibrary";
        final String subject = "Rejestracja konta w aplikacji HomeLibrary";
        SendEmailTask task = new SendEmailTask(body, subject, email, this);
        task.execute((Void) null);
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
            registrationResult = mUserService.attemptRegistration(getBaseContext(), login, email, encryptedPassword);
            return registrationResult.equals(RegistrationResult.SUCCESS);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                sendAnEmailWithLoginCredentials(login, email, password);
                showConfirmationToast();
                finish();
            } else {
                switch (registrationResult) {
                    case CONNECTION_ERROR:
//                        Toast.makeText(getBaseContext(), registrationResult.getText(), Toast.LENGTH_LONG).show();
                        break;
                    case USER_ALREADY_EXISTS:
                        mLoginView.setError(registrationResult.getText());
                        mLoginView.requestFocus();
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

    private void showConfirmationToast() {
        Toast.makeText(this, "Udało się zarejestrować użytkownika", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
