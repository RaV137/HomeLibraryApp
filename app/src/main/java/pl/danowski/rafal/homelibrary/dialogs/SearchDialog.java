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

public class SearchDialog extends DialogFragment {

    private OnMyDialogResult mDialogResult; // the callback
    private EditText mQuery;


    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle("Wyszukaj książkę")
                .setView(inflater.inflate(R.layout.search_book_dialog, null))
                .setNegativeButton("ANULUJ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("SZUKAJ", new DialogInterface.OnClickListener() {
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
                    mQuery = d.findViewById(R.id.query);
                    String query = mQuery.getText().toString();

                    if (TextUtils.isEmpty(query)) {
                        mQuery.requestFocus();
                        mQuery.setError("Wpisz zapytanie.");
                    } else {
                        if (mDialogResult != null) {
                            mDialogResult.finish(query);
                        }
                        d.dismiss();
                    }
                }
            });
        }
    }

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish(String query);
    }
}
