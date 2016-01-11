package app.ddf.danskdatahistoriskforening.image;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

//http://developer.android.com/guide/topics/ui/dialogs.html
public class ConfirmDeletionDialogFragment extends DialogFragment {
    private String title = "";
    private int index;

    ConfirmDeletionListener mListener;

    public interface ConfirmDeletionListener{
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (ConfirmDeletionListener) activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            index = savedInstanceState.getInt("index");
            title = savedInstanceState.getString("title");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Er du sikker på at du vil slette det valgte billede?")
                .setTitle(title)
                .setPositiveButton("Slet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClick(ConfirmDeletionDialogFragment.this);
                    }
                })
                .setNegativeButton("Fortryd", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(ConfirmDeletionDialogFragment.this);
                    }
                });

        return builder.create();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("title", title);
        outState.putInt("index", index);
    }
}