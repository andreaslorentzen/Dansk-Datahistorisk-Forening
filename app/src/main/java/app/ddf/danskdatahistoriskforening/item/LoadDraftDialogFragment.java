package app.ddf.danskdatahistoriskforening.item;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import app.ddf.danskdatahistoriskforening.dal.Item;

public class LoadDraftDialogFragment extends DialogFragment {
    Item draft;
    ConfirmDraftLoadListener mListener;

    public interface ConfirmDraftLoadListener {
        void onDialogPositiveClick(Item draft);

        void onDialogNegativeClick();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (ConfirmDraftLoadListener) activity;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("draft", draft);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            draft = savedInstanceState.getParcelable("draft");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Der findes en gemt skabelon på enheden. Vil du indlæse de tidligere udfyldte felter?")
                .setTitle("Vil du fortsætte sidste registrering?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClick(draft);
                    }
                })
                .setNegativeButton("Nej", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick();
                    }
                });

        return builder.create();
    }

    public void setDraft(Item draft) {
        this.draft = draft;
    }
}
