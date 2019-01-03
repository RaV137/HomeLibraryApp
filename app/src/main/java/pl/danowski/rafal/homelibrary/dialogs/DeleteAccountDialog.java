package pl.danowski.rafal.homelibrary.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import lombok.Setter;
import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.exceptions.NoNetworkConnectionException;
import pl.danowski.rafal.homelibrary.model.user.User;
import pl.danowski.rafal.homelibrary.network.email.SendEmailTask;
import pl.danowski.rafal.homelibrary.services.UserService;
import pl.danowski.rafal.homelibrary.utiities.password.PasswordEncrypter;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;
import pl.danowski.rafal.homelibrary.utiities.toast.NoNetworkConnectionToast;
import pl.danowski.rafal.homelibrary.utiities.validators.Validator;

public class DeleteAccountDialog extends DialogFragment {

    private OnMyDialogResult mDialogResult; // the callback
    private EditText mPassword;
    private View focusView = null;
    private UserService userService = UserService.getInstance();

    @Setter
    private Context mContext;

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle("Usuń konto")
                .setView(inflater.inflate(R.layout.dialog_delete_account, null))
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
                    mPassword = d.findViewById(R.id.password);

                    String password = mPassword.getText().toString();
                    String login = SharedPreferencesUtilities.getLogin(getActivity().getApplicationContext());
                    HandleDeleteAccountTask task = new HandleDeleteAccountTask(login, password, d);
                    task.execute((Void) null);
                }
            });
        }
    }

    public class HandleDeleteAccountTask extends AsyncTask<Void, Void, Boolean> {

        private String login;
        private String password;
        private AlertDialog d;

        public HandleDeleteAccountTask(String login, String password, AlertDialog d) {
            this.login = login;
            this.password = password;
            this.d = d;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean exit = true;if (TextUtils.isEmpty(password)) {
                focusView = mPassword;
                mPassword.setError("Pole wymagane");
                exit = false;
            } else {
                try {
                    if (!userService.checkPasswordForLogin(mContext, login, PasswordEncrypter.md5(password))) {
                        focusView = mPassword;
                        mPassword.setError("Niepoprawne hasło");
                        exit = false;
                    }
                } catch (NoNetworkConnectionException e) {
                    NoNetworkConnectionToast.show(mContext);
                }
            }
            return exit;
        }

        @Override
        protected void onPostExecute(Boolean exit) {
            if (exit) {
                if (mDialogResult != null) {
                    mDialogResult.finish(true);
                }
                d.dismiss();
            } else {
                focusView.requestFocus();
            }
        }
    }

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish(boolean success);
    }
}
