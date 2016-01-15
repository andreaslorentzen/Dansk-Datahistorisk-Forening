package app.ddf.danskdatahistoriskforening.item;

import android.support.v4.app.DialogFragment;

/**
 * Created by jonas on 1/15/16.
 */
public class LoadDraftDialogFragment extends DialogFragment {
    public interface ConfirmDeletionListener{
        void onDialogPositiveClick();
        void onDialogNegativeClick();
    }
}
