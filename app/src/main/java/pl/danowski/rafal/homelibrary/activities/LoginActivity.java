package pl.danowski.rafal.homelibrary.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.exceptions.NoNetworkConnectionException;
import pl.danowski.rafal.homelibrary.network.BaseAsyncTask;
import pl.danowski.rafal.homelibrary.services.UserService;
import pl.danowski.rafal.homelibrary.utiities.enums.IntentExtras;
import pl.danowski.rafal.homelibrary.utiities.password.PasswordEncrypter;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;
import pl.danowski.rafal.homelibrary.utiities.toast.NoNetworkConnectionToast;

/**
 * A login screen that offers login via login/password.
 */
public class LoginActivity extends AppCompatActivity {

    private UserService mUserService;

    private UserLoginTask mAuthTask = null;

    private EditText mLoginView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private CheckBox mCheckBox;

    private List<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (AsyncTask task : tasks) {
            if (task == null)
                continue;
            AsyncTask.Status status = task.getStatus();
            if (status.equals(AsyncTask.Status.PENDING) || status.equals(AsyncTask.Status.RUNNING)) {
                task.cancel(true);
            }
        }
    }

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

            mUserService = UserService.getInstance();

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
            mAuthTask = new UserLoginTask(login, encryptedPassword, this);
            mAuthTask.execute((Void) null);
            tasks.add(mAuthTask);
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

    private class UserLoginTask extends BaseAsyncTask<Void, Void, Boolean> {

        private final String login;
        private final String mPassword;

        UserLoginTask(String login, String password, Context context) {
            super(context);
            this.login = login;
            this.mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            super.doInBackground(params);
            boolean loginResult = false;
            try {
                loginResult = mUserService.attemptLogin(mContext, login, mPassword);
            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
            }
            return loginResult;
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

