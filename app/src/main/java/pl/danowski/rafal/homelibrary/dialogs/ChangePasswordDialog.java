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
import pl.danowski.rafal.homelibrary.services.UserService;
import pl.danowski.rafal.homelibrary.utiities.PasswordEncrypter;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;
import pl.danowski.rafal.homelibrary.utiities.validators.Validator;

public class ChangePasswordDialog extends DialogFragment {

    private EditText mOldPassword;
    private EditText mNewPassword;
    private View focusView = null;
    private UserService userService = new UserService();
    private EditText mConfirmNewPassword;

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.change_password_dialog, null))
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
                    mOldPassword = d.findViewById(R.id.oldPassword);
                    mNewPassword = d.findViewById(R.id.newPassword);
                    mConfirmNewPassword = d.findViewById(R.id.confirmNewPassword);

                    String oldPassword = mOldPassword.getText().toString();
                    String newPassword = mNewPassword.getText().toString();
                    String confirmNewPassword = mConfirmNewPassword.getText().toString();
                    String login = SharedPreferencesUtilities.getLogin(getActivity().getApplicationContext());

                    HandleChangePasswordTask task = new HandleChangePasswordTask(oldPassword, newPassword, confirmNewPassword, login, d);
                    task.execute((Void) null);
                }
            });
        }
    }

    public class HandleChangePasswordTask extends AsyncTask<Void, Void, Boolean> {

        private String oldPassword;
        private String newPassword;
        private String confirmNewPassword;
        private String login;
        private AlertDialog d;

        public HandleChangePasswordTask(String oldPassword, String newPassword, String confirmNewPassword, String login, AlertDialog d) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
            this.confirmNewPassword = confirmNewPassword;
            this.login = login;
            this.d = d;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean exit = true;
            if (TextUtils.isEmpty(oldPassword)) {
                focusView = mOldPassword;
                mOldPassword.setError("Pole wymagane");
                exit = false;
            } else if (TextUtils.isEmpty(newPassword)) {
                focusView = mNewPassword;
                mNewPassword.setError("Pole wymagane");
                exit = false;
            } else if (TextUtils.isEmpty(confirmNewPassword)) {
                focusView = mConfirmNewPassword;
                mConfirmNewPassword.setError("Pole wymagane");
                exit = false;
            } else if (!userService.checkPasswordForLogin(getActivity().getBaseContext(), login, PasswordEncrypter.md5(oldPassword))) {
                focusView = mOldPassword;
                mOldPassword.setError("Niepoprawne hasło");
                exit = false;
            } else if (!Validator.isValidPasswordFormat(newPassword)) {
                focusView = mNewPassword;
                mNewPassword.setError(getString(R.string.rule_password));
                exit = false;
            } else if (!newPassword.equals(confirmNewPassword)) {
                focusView = mConfirmNewPassword;
                mConfirmNewPassword.setError("Oba hasła muszą być takie same");
                exit = false;
            }
            return exit;
        }

        @Override
        protected void onPostExecute(Boolean exit) {
            if (exit) {
                UpdatePasswordTask task = new UpdatePasswordTask(login, newPassword);
                task.execute((Void) null);
                Toast.makeText(getActivity().getBaseContext(), "Udało się zmienić hasło", Toast.LENGTH_SHORT).show();
                d.dismiss();
            } else {
                focusView.requestFocus();
            }
        }
    }

    public class UpdatePasswordTask extends AsyncTask<Void, Void, Void> {

        private String login;
        private String newPassword;

        public UpdatePasswordTask(String login, String newPassword) {
            this.login = login;
            this.newPassword = newPassword;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userService.updateUserPassword(getActivity().getBaseContext(), login, PasswordEncrypter.md5(newPassword));
            return null;
        }
    }
}
