package app.ddf.danskdatahistoriskforening;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;


public class RegisterDetailsFragment extends Fragment implements View.OnClickListener{

    TextView dateFrom;
    TextView dateTo;
    TextView dateReceive;

    LinearLayout dateFromWrapper;
    LinearLayout dateToWrapper;
    LinearLayout dateReceiveWrapper;

    public static TextView currentDateField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_register_details, container, false);

        dateFrom = (TextView) layout.findViewById(R.id.DateFrom);
        dateTo = (TextView) layout.findViewById(R.id.DateTo);
        dateReceive = (TextView) layout.findViewById(R.id.ReceiveDate);
        dateReceiveWrapper = (LinearLayout) layout.findViewById(R.id.RecieveDateWrapper);
        dateFromWrapper = (LinearLayout) layout.findViewById(R.id.DateFromWrapper);
        dateToWrapper = (LinearLayout) layout.findViewById(R.id.DateToWrapper);

        DateFormat format = DateFormat.getDateInstance();

        dateFrom.setText(format.format(new Date()));
        dateTo.setText(format.format(new Date()));
        dateReceive.setText(format.format(new Date()));

        dateFromWrapper.setOnClickListener(this);
        dateToWrapper.setOnClickListener(this);
        dateReceiveWrapper.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View v) {
        if(v == dateReceiveWrapper || v == dateFromWrapper || v == dateToWrapper){
            if(v == dateReceiveWrapper)
                currentDateField = dateReceive;
            else if(v == dateFromWrapper)
                currentDateField = dateFrom;
            else
                currentDateField = dateTo;

            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getActivity().getSupportFragmentManager(), "datePicker");
        }
    }

}
