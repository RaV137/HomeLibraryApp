package pl.danowski.rafal.homelibrary.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.exceptions.NoNetworkConnectionException;
import pl.danowski.rafal.homelibrary.network.BaseAsyncTask;
import pl.danowski.rafal.homelibrary.network.email.SendEmailTask;
import pl.danowski.rafal.homelibrary.services.UserService;
import pl.danowski.rafal.homelibrary.utiities.password.PasswordEncrypter;
import pl.danowski.rafal.homelibrary.utiities.password.PasswordGenerator;
import pl.danowski.rafal.homelibrary.utiities.enums.IntentExtras;
import pl.danowski.rafal.homelibrary.utiities.toast.NoNetworkConnectionToast;

public class ForgotPasswordActivity extends AppCompatActivity {


    private UserService mUserService;

    private ForgotPasswordTask mAuthTask = null;

    private EditText mLoginView;
    private EditText mEmailView;
    private View mProgressView;
    private View mForgotPasswordFormView;

    private List<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (AsyncTask task : tasks) {
            if(task == null)
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
        mUserService = UserService.getInstance();

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
            mAuthTask = new ForgotPasswordTask(login, email, this);
            mAuthTask.execute((Void) null);
            tasks.add(mAuthTask);
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

    private class ForgotPasswordTask extends BaseAsyncTask<Void, Void, Boolean> {

        private final String login;
        private final String email;

        ForgotPasswordTask(String login, String email, Context context) {
            super(context);
            this.login = login;
            this.email = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return mUserService.isUserRegistered(mContext, login, email);
            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                final String password = generateNewPassword();
                sendEmailWithNewCredentials(email, password);
                UpdatePasswordTask updatePasswordTask = new UpdatePasswordTask(login, password);
                updatePasswordTask.execute((Void) null);
                tasks.add(updatePasswordTask);
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

    private class UpdatePasswordTask extends AsyncTask<Void, Void, Void> {

        private final String login;
        private final String password;

        UpdatePasswordTask(String login, String password) {
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
        try {
            mUserService.updateUserPassword(this, login, encryptedPassword); // TODO: obsługa błędów
        } catch (NoNetworkConnectionException e) {
            NoNetworkConnectionToast.show(this);
        }
    }

    private void sendEmailWithNewCredentials(final String email, final String password) {
        final String body = "Witaj!\nWłaśnie wygenerowaliśmy Ci nowe hasło do aplikacji HomeLibrary." +
                "\n\nTwoje nowe hasło: " + password +
                "\n\nJeśli to nie Ty resetowałeś hasło w aplikacji, zignoruj tego maila.\nPozdrawiamy\nZespół HomeLibrary";
        final String subject = "Przypomnienie hasła w aplikacji HomeLibrary";
        SendEmailTask task = new SendEmailTask(body, subject, email, this);
        task.execute((Void) null);
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
