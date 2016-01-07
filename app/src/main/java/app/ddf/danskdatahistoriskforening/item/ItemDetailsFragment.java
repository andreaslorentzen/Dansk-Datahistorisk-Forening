package app.ddf.danskdatahistoriskforening.item;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.R;


public class ItemDetailsFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener{

    TextView dateFrom;
    TextView dateTo;
    TextView dateReceive;
    TextView producer;
    TextView donator;

    LinearLayout dateFromWrapper;
    LinearLayout dateToWrapper;
    LinearLayout dateReceiveWrapper;

    public static TextView currentDateField;
    private String currentlyChanging;

    private boolean receivedChanged = false;
    private boolean dateFromChanged = false;
    private boolean dateToChanged = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_item_details, container, false);

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

        Item item = ((ItemActivity) getActivity()).getItem();
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
            if(v == dateReceiveWrapper) {
                currentDateField = dateReceive;
                currentlyChanging = "dateReceive";
            }else if(v == dateFromWrapper) {
                currentDateField = dateFrom;
                currentlyChanging = "dateFrom";
            }else if(v == dateToWrapper) {
                currentDateField = dateTo;
                currentlyChanging = "dateTo";
            }
            DatePickerFragment datePicker = new DatePickerFragment();
            datePicker.setOnDateSetListener(this);
            datePicker.show(getActivity().getSupportFragmentManager(), "datePicker");
        }
    }

    public void dateChanged(){
        System.out.println("It WORKS");
        if(currentlyChanging.equals("dateReceive"))
            receivedChanged = true;
        else if(currentlyChanging.equals("dateFrom"))
            dateFromChanged = true;
        else if(currentlyChanging.equals("dateTo"))
            dateToChanged = true;
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        currentDateField.setText("" + year + "-" + (((monthOfYear + 1) < 10) ? "0"+(monthOfYear+1) : (monthOfYear+1)) + "-" + dayOfMonth);
        dateChanged();
    }

    public boolean hasReceiveChanged(){
        return receivedChanged;
    }

    public boolean hasDateFromChanged(){
        return dateFromChanged;
    }

    public boolean hasDateToChanged(){
        return dateToChanged;
    }
}