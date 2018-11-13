package pl.danowski.rafal.homelibrary.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.controllers.LoginRegistrationController;
import pl.danowski.rafal.homelibrary.controllers.interfaces.ILoginRegistrationController;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.loginRegistrationController = new LoginRegistrationController();
        String login = getIntent().getStringExtra(IntentExtras.LOGIN.getName());

        mLoginView = findViewById(R.id.textLogin);
        mPasswordView = findViewById(R.id.textPassword);
        mConfirmPasswordView = findViewById(R.id.confirmPassword);
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
    }

    private void attemptRegistration() {
        if (mAuthTask != null) {
            return;
        }

        mLoginView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
        mEmailView.setError(null);
    }

    public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {

        private final String login;
        private final String email;
        private final String encryptedPassword;

        private RegistrationResult registrationResult;

        UserRegistrationTask(String login, String email, String encryptedPassword) {
            this.login = login;
            this.email = email;
            this.encryptedPassword = encryptedPassword;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            registrationResult = loginRegistrationController.attemptRegistration(login, email, encryptedPassword);

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
//            showProgress(false);

            if (success) {
                finish();
            } else {
                switch (registrationResult) {
                    case CONNECTION_ERROR:
                        Toast.makeText(getBaseContext(), registrationResult.getText(), Toast.LENGTH_LONG).show();
                        break;
                    case LOGIN_ALREADY_EXISTS:
//                        mLoginView.setError(getString(R.string.error_login_already_exists));
                        mLoginView.setError(registrationResult.getText());
                        mLoginView.requestFocus();
                        break;
                    case EMAIL_ALREADY_EXISTS:
                        mEmailView.setError(registrationResult.getText());
                        mEmailView.requestFocus();
                        break;
                    default:
                        Toast.makeText(getBaseContext(), registrationResult.getText(), Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
//            showProgress(false);
        }
    }
}
