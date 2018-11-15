package pl.danowski.rafal.homelibrary.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import lombok.Setter;
import pl.danowski.rafal.homelibrary.activities.LoginActivity;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;

public class ConfirmLogOutDialog extends DialogFragment {

    @Setter
    private Context context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Czy na pewno chcesz się wylogować?")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferencesUtilities.deleteLogin(context);
                        SharedPreferencesUtilities.setAutologin(context, false);
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return builder.create();
    }


}
