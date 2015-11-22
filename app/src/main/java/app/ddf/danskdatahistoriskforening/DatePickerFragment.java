package app.ddf.danskdatahistoriskforening;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    DateFormat formater;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceBundle){


        formater = DateFormat.getDateInstance();
        Date chosenDate;
        try {
            chosenDate = formater.parse(RegisterDetailsFragment.currentDateField.getText().toString());
        } catch (ParseException e){
            e.printStackTrace();
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(chosenDate);
        System.out.println(formater.format(chosenDate));
        return new DatePickerDialog(getActivity(), this, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        System.out.println("inside the onDateSet");
        try {
            SimpleDateFormat stringToDateFormater = new SimpleDateFormat("yyyy-MM-dd");
            RegisterDetailsFragment.currentDateField.setText(formater.format(stringToDateFormater.parse("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth)));
        } catch (ParseException e){
            e.printStackTrace();
        }
    }
}
