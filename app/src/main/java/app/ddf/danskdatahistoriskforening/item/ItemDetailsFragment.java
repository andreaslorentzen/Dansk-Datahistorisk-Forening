package app.ddf.danskdatahistoriskforening.item;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;

import app.ddf.danskdatahistoriskforening.helper.App;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.domain.Logic;


public class ItemDetailsFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, ItemActivity.ItemUpdater, TextView.OnEditorActionListener, View.OnFocusChangeListener {

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
        View layout = inflater.inflate(R.layout.fragment_item_details, container, false);

        producer = (TextView) layout.findViewById(R.id.Producer);
        donator = (TextView) layout.findViewById(R.id.Donator);

        producer.setOnEditorActionListener(this);

        producer.setOnFocusChangeListener(this);
        donator.setOnFocusChangeListener(this);


        dateFrom = (TextView) layout.findViewById(R.id.DateFrom);
        dateTo = (TextView) layout.findViewById(R.id.DateTo);
        dateReceive = (TextView) layout.findViewById(R.id.ReceiveDate);
        dateReceiveWrapper = (LinearLayout) layout.findViewById(R.id.RecieveDateWrapper);
        dateFromWrapper = (LinearLayout) layout.findViewById(R.id.DateFromWrapper);
        dateToWrapper = (LinearLayout) layout.findViewById(R.id.DateToWrapper);

        dateFromWrapper.setOnClickListener(this);
        dateToWrapper.setOnClickListener(this);
        dateReceiveWrapper.setOnClickListener(this);

        Item item = Logic.instance.editItem;
        dateReceive.setText(item.getItemRecievedAsString() == null ? "Ikke sat" : item.getItemRecievedAsString());
        donator.setText(item.getDonator());
        producer.setText(item.getProducer());
        dateFrom.setText(item.getItemDatingFromAsString() == null ? "Ikke sat" : item.getItemDatingFromAsString());
        dateTo.setText(item.getItemDatingToAsString() == null ? "Ikke sat" : item.getItemDatingToAsString());

        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();
        updateItem(Logic.instance.editItem);
    }

    @Override
    public void updateItem(Item item) {
        item.setDonator(donator.getText().toString());
        item.setProducer(producer.getText().toString());
        try {
            item.setItemRecieved(dateReceive.getText().toString().equals("") || dateReceive.getText().toString().equals("Ikke sat") ? null : App.getFormatter().parse(dateReceive.getText().toString()));
            item.setItemDatingFrom(dateFrom.getText().toString().equals("") || dateFrom.getText().toString().equals("Ikke sat") ? null : App.getFormatter().parse(dateFrom.getText().toString()));
            item.setItemDatingTo(dateTo.getText().toString().equals("") || dateTo.getText().toString().equals("Ikke sat") ? null : App.getFormatter().parse(dateTo.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (item.getItemRecievedAsString() != null)
            System.out.println(item.getItemRecievedAsString());
    }

    @Override
    public void onClick(View v) {
        if (v == dateReceiveWrapper)
            currentDateField = dateReceive;
        else if (v == dateFromWrapper)
            currentDateField = dateFrom;
        else if (v == dateToWrapper)
            currentDateField = dateTo;
        else
            return;

        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setOnDateSetListener(this);
        datePicker.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        currentDateField.setText("" + year + "-" + (((monthOfYear + 1) < 10) ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + dayOfMonth);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        v.clearFocus();
        App.hideKeyboard(getActivity(), v);
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus)
            App.hideKeyboard(getActivity(), v);
    }
}
