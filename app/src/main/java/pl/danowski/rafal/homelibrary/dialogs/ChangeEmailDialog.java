package pl.danowski.rafal.homelibrary.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.model.user.User;
import pl.danowski.rafal.homelibrary.network.email.SendEmailTask;
import pl.danowski.rafal.homelibrary.services.UserService;
import pl.danowski.rafal.homelibrary.utiities.PasswordEncrypter;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;
import pl.danowski.rafal.homelibrary.utiities.validators.Validator;

public class ChangeEmailDialog extends DialogFragment {

    private OnMyDialogResult mDialogResult; // the callback
    private EditText mEmail;
    private EditText mPassword;
    private View focusView = null;
    private UserService userService = new UserService();

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.change_email_dialog, null))
                .setNegativeButton("ANULUJ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEmail = d.findViewById(R.id.newEmail);
                    mPassword = d.findViewById(R.id.password);

                    String email = mEmail.getText().toString();
                    String password = mPassword.getText().toString();

                    String login = SharedPreferencesUtilities.getLogin(getActivity().getApplicationContext());
                    HandleChangeEmailTask task = new HandleChangeEmailTask(login, email, password, d);
                    task.execute((Void) null);
                }
            });
        }
    }

    public class HandleChangeEmailTask extends AsyncTask<Void, Void, Boolean> {

        private String login;
        private String email;
        private String password;
        private AlertDialog d;

        public HandleChangeEmailTask(String login, String email, String password, AlertDialog d) {
            this.login = login;
            this.email = email;
            this.password = password;
            this.d = d;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean exit = true;
            if (TextUtils.isEmpty(email)) {
                focusView = mEmail;
                mEmail.setError("Pole wymagane");
                exit = false;
            } else if (TextUtils.isEmpty(password)) {
                focusView = mPassword;
                mPassword.setError("Pole wymagane");
                exit = false;
            } else if (!userService.checkPasswordForLogin(login, PasswordEncrypter.md5(password))) {
                focusView = mPassword;
                mPassword.setError("Niepoprawne hasło");
                exit = false;
            } else if (!Validator.isValidEmailFormat(email)) {
                focusView = mEmail;
                mEmail.setError(getString(R.string.rule_email));
                exit = false;
            }
            return exit;
        }

        @Override
        protected void onPostExecute(Boolean exit) {
            if (exit) {
                UpdateEmailTask task = new UpdateEmailTask(login, email);
                task.execute((Void) null);
                Toast.makeText(getActivity().getBaseContext(), "Udało się zmienić adres email", Toast.LENGTH_SHORT).show();
                if (mDialogResult != null) {
                    mDialogResult.finish(true, email);
                }
                d.dismiss();
            } else {
                focusView.requestFocus();
            }
        }
    }

    private void sendEmailEmailChanged(final String email, final String newEmail) {
        final String body = "Witaj!\nWłaśnie zmieniłeś swój adres email w aplikacji HomeLibrary." +
                "\n\nTwój nowy email to: " + newEmail +
                "\n\nJeśli to nie Ty się dokonałeś tej zmiany, skontaktuj się z nami.\nPozdrawiamy\nZespół HomeLibrary";
        final String subject = "Zmiana adresu email w aplikacji HomeLibrary";
        SendEmailTask task = new SendEmailTask(body, subject, email, getActivity().getBaseContext());
        task.execute((Void) null);
    }


    public class UpdateEmailTask extends AsyncTask<Void, Void, Void> {

        private String login;
        private String email;

        public UpdateEmailTask(String login, String email) {
            this.login = login;
            this.email = email;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            User user = userService.findUserByLogin(getActivity().getBaseContext(), login);
            String oldEmail = user.getEmail();
            sendEmailEmailChanged(oldEmail, email);
            userService.updateUserEmail(getActivity().getBaseContext(), login, email);
            return null;
        }
    }

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish(boolean success, String email);
    }
}
