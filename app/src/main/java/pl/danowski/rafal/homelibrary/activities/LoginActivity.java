package pl.danowski.rafal.homelibrary.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.controllers.UserController;
import pl.danowski.rafal.homelibrary.utiities.PasswordEncrypter;
import pl.danowski.rafal.homelibrary.utiities.enums.IntentExtras;
import pl.danowski.rafal.homelibrary.utiities.enums.LoginResult;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;

/**
 * A login screen that offers login via login/password.
 */
public class LoginActivity extends AppCompatActivity {

    private UserController mUserController;

    private UserLoginTask mAuthTask = null;

    private EditText mLoginView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mLoginView = findViewById(R.id.textLogin);
        mPasswordView = findViewById(R.id.textPassword);
        mCheckBox = findViewById(R.id.rememberMe);

        if (SharedPreferencesUtilities.isUserLoggedIn(getApplicationContext())) {
            successfulLogin(SharedPreferencesUtilities.getLogin(getApplicationContext()));
        } else {
            setTitle("Logowanie");

            mUserController = new UserController();

            Button mSignInButton = findViewById(R.id.loginButton);
            mSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            Button mRegisterButton = findViewById(R.id.registerButton);
            mRegisterButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToRegister();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.forgot_password_progress);

            TextView mForgotPasswordView = findViewById(R.id.forgotPassword);
            mForgotPasswordView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    forgotPassword();
                }
            });
        }
    }

    private void forgotPassword() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        String login = mLoginView.getText().toString();
        intent.putExtra(IntentExtras.LOGIN.getName(), login);
        mLoginView.setError(null);
        mLoginView.setText("");
        mPasswordView.setError(null);
        mPasswordView.setText("");
        startActivity(intent);
    }

    private void goToRegister() {
        mPasswordView.setError(null);
        mPasswordView.setText("");
        Intent intent = new Intent(this, RegisterActivity.class);
        String login = mLoginView.getText().toString();
        intent.putExtra(IntentExtras.LOGIN.getName(), login);
        startActivity(intent);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mLoginView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(login)) {
            mLoginView.setError("Pole jest wymagane");
            focusView = mLoginView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Pole jest wymagane");
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            final String encryptedPassword = PasswordEncrypter.md5(password);
            mAuthTask = new UserLoginTask(login, encryptedPassword);
            mAuthTask.execute((Void) null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String login;
        private final String mPassword;

        UserLoginTask(String login, String password) {
            this.login = login;
            this.mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            LoginResult loginResult = mUserController.attemptLogin(getBaseContext(), login, mPassword);

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            return loginResult.equals(LoginResult.SUCCESS);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                if (mCheckBox.isChecked()) {
                    SharedPreferencesUtilities.setAutologin(getApplicationContext(), true);
                }
                successfulLogin(login);
            } else {
                mPasswordView.setError("Niepoprawny login lub hasło");
                mPasswordView.setText("");
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void successfulLogin(String login) {
        mLoginView.setText("");
        mPasswordView.setText("");
        mCheckBox.setActivated(false);
        SharedPreferencesUtilities.setLogin(getApplicationContext(), login);
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}

