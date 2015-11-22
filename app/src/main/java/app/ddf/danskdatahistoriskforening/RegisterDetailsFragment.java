package app.ddf.danskdatahistoriskforening;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class RegisterDetailsFragment extends Fragment implements View.OnClickListener{

    TextView dateFrom;
    TextView dateTo;
    TextView receiveDate;
    LinearLayout recieveDateWrapper;
    public static TextView currentDateField;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_register_details, container, false);

        dateFrom = (TextView) layout.findViewById(R.id.DateFrom);
        dateTo = (TextView) layout.findViewById(R.id.DateTo);
        receiveDate = (TextView) layout.findViewById(R.id.ReceiveDate);
        recieveDateWrapper = (LinearLayout) layout.findViewById(R.id.RecieveDateWrapper);

        DateFormat format = DateFormat.getDateInstance();

        dateFrom.setText(format.format(new Date()));
        dateTo.setText(format.format(new Date()));
        receiveDate.setText(format.format(new Date()));

        dateFrom.setOnClickListener(this);
        dateTo.setOnClickListener(this);
        recieveDateWrapper.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View v) {
        if(v == recieveDateWrapper){
            currentDateField = receiveDate;
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getActivity().getSupportFragmentManager(), "datePicker");
        }
        else if(v == dateFrom || v == dateTo) {
            currentDateField = (TextView) v;
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getActivity().getSupportFragmentManager(), "datePicker");
        }
    }

    public static String convertToDateString(int day, int month, int year){
        return "" + day + "-" + month + "-" + year;
    }
}
