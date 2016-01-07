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
import java.text.SimpleDateFormat;
import java.util.Date;


public class RegisterDetailsFragment extends Fragment implements View.OnClickListener{

    TextView dateFrom;
    TextView dateTo;
    TextView dateReceive;
    TextView producer;
    TextView donator;

    LinearLayout dateFromWrapper;
    LinearLayout dateToWrapper;
    LinearLayout dateReceiveWrapper;

    public static TextView currentDateField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_register_details, container, false);

        producer = (TextView) layout.findViewById(R.id.Producer);
        donator = (TextView) layout.findViewById(R.id.Donator);

        dateFrom = (TextView) layout.findViewById(R.id.DateFrom);
        dateTo = (TextView) layout.findViewById(R.id.DateTo);
        dateReceive = (TextView) layout.findViewById(R.id.ReceiveDate);
        dateReceiveWrapper = (LinearLayout) layout.findViewById(R.id.RecieveDateWrapper);
        dateFromWrapper = (LinearLayout) layout.findViewById(R.id.DateFromWrapper);
        dateToWrapper = (LinearLayout) layout.findViewById(R.id.DateToWrapper);



        dateFrom.setText(Model.getFormatter().format(new Date()));
        dateTo.setText(Model.getFormatter().format(new Date()));
        dateReceive.setText(Model.getFormatter().format(new Date()));

        dateFromWrapper.setOnClickListener(this);
        dateToWrapper.setOnClickListener(this);
        dateReceiveWrapper.setOnClickListener(this);

        Item item = ((RegisterActivity) getActivity()).getItem();
        setDateFrom(item.getItemDatingFrom());
        setDateTo(item.getItemDatingTo());
        setDateReceive(item.getItemRecieved());
        setDonator(item.getDonator());
        setProducer(item.getProducer());

        return layout;
    }

    @Override
    public void onClick(View v) {
        if(v == dateReceiveWrapper || v == dateFromWrapper || v == dateToWrapper){
            if(v == dateReceiveWrapper)
                currentDateField = dateReceive;
            else if(v == dateFromWrapper)
                currentDateField = dateFrom;
            else if(v == dateToWrapper)
                currentDateField = dateTo;

            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getActivity().getSupportFragmentManager(), "datePicker");
        }
    }

    public void setDateFrom(Date date) {
        if(date != null)
            this.dateFrom.setText(Model.getFormatter().format(date));
    }

    public void setDateTo(Date date) {
        if(date != null)
            this.dateTo.setText(Model.getFormatter().format(date));
    }

    public void setDateReceive(Date date) {
        if(date != null)
            this.dateReceive.setText(Model.getFormatter().format(date));
    }

    public void setDonator(String donator) {
        this.donator.setText(donator);
    }

    public void setProducer(String producer) {
        this.producer.setText(producer);
    }
}
