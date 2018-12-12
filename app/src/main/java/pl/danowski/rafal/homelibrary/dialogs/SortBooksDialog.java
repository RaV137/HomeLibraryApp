package pl.danowski.rafal.homelibrary.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.utiities.enums.SortOptionEnum;

public class SortBooksDialog extends DialogFragment {

    private OnMyDialogResult mDialogResult; // the callback


    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish(SortOptionEnum sortOptionEnum);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.sort_dialog, null))
                .setNegativeButton("ANULUJ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) { }
                });
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean exit = true;

                    RadioGroup mRadioGroup = d.findViewById(R.id.Sort);
                    int id = mRadioGroup.getCheckedRadioButtonId();
                    SortOptionEnum sortOptionEnum = null;
                    switch (id) {
                        case R.id.ScoreAsc:
                            sortOptionEnum = SortOptionEnum.SORT_BY_SCORE_ASC;
                            break;
                        case R.id.ScoreDesc:
                            sortOptionEnum = SortOptionEnum.SORT_BY_SCORE_DESC;
                            break;
                        case R.id.TitleAsc:
                            sortOptionEnum = SortOptionEnum.SORT_BY_TITLE_ASC;
                            break;
                        case R.id.TitleDesc:
                            sortOptionEnum = SortOptionEnum.SORT_BY_TITLE_DESC;
                            break;
                        case R.id.AuthorAsc:
                            sortOptionEnum = SortOptionEnum.SORT_BY_AUTHOR_ASC;
                            break;
                        case R.id.AuthorDesc:
                            sortOptionEnum = SortOptionEnum.SORT_BY_AUTHOR_DESC;
                            break;
                        default:
                            exit = false;
                    }

                    if(exit) {
                        if (mDialogResult != null) {
                            mDialogResult.finish(sortOptionEnum);
                        }
                        d.dismiss();
                    }
                }
            });
        }
    }
}
