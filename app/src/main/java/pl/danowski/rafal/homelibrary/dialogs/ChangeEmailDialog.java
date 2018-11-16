package pl.danowski.rafal.homelibrary.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.controllers.UserController;
import pl.danowski.rafal.homelibrary.utiities.PasswordEncrypter;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;
import pl.danowski.rafal.homelibrary.utiities.validators.Validator;

public class ChangeEmailDialog extends DialogFragment {

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
                    boolean exit = true;

                    View focusView = null;
                    UserController userController = new UserController();

                    EditText mEmail = d.findViewById(R.id.email);
                    EditText mPassword = d.findViewById(R.id.password);

                    String email = mEmail.getText().toString();
                    String password = mPassword.getText().toString();

                    String login = SharedPreferencesUtilities.getLogin(getActivity().getApplicationContext());

                    if (TextUtils.isEmpty(email)) {
                        focusView = mEmail;
                        mEmail.setError("Pole wymagane");
                        exit = false;
                    } else if (TextUtils.isEmpty(password)) {
                        focusView = mPassword;
                        mPassword.setError("Pole wymagane");
                        exit = false;
                    } else if (!userController.checkPasswordForLogin(getActivity().getBaseContext(), login, PasswordEncrypter.md5(email))) {
                        focusView = mPassword;
                        mPassword.setError("Niepoprawne hasło");
                        exit = false;
                    } else if(!Validator.isValidEmailFormat(email)) {
                        focusView = mEmail;
                        mEmail.setError(getString(R.string.rule_email));
                        exit = false;
                    }

                    if (exit) {
                        userController.updateUserEmail(getActivity().getBaseContext(), login, email);
                        d.dismiss();
                    } else {
                        focusView.requestFocus();
                    }
                }
            });
        }
    }
}
