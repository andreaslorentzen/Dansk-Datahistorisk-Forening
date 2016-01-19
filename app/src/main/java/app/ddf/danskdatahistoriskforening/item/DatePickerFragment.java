package app.ddf.danskdatahistoriskforening.item;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import app.ddf.danskdatahistoriskforening.helper.App;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DatePickerDialog.OnDateSetListener externalListener;

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        this.externalListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceBundle) {
        Date chosenDate;
        if (ItemDetailsFragment.currentDateField.getText().toString().equals("Ikke sat") || ItemDetailsFragment.currentDateField.getText().toString().equals("")) {
            chosenDate = new Date();
        } else {
            try {
                chosenDate = App.getFormatter().parse(ItemDetailsFragment.currentDateField.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(chosenDate);
        return new DatePickerDialog(getActivity(), this, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (externalListener != null) {
            externalListener.onDateSet(view, year, monthOfYear, dayOfMonth);
        }
    }
}
