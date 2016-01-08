package app.ddf.danskdatahistoriskforening.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.R;


public class ItemShowFragment extends Fragment {

    private String detailsURI;
    private TextView itemheadlineView;
    private HorizontalScrollView imageScrollView;
    private TextView itemdescriptionView;
    private TextView receivedView;
    private TextView datingFromView;
    private TextView datingToView;
    private TextView donatorView;
    private TextView producerView;
    private TextView postNummerView;

    public ItemShowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_item_show, container, false);

        itemheadlineView = (TextView) layout.findViewById(R.id.itemheadline);
        imageScrollView = (HorizontalScrollView) layout.findViewById(R.id.imageScrollView);
        itemdescriptionView = (TextView) layout.findViewById(R.id.itemdescription);
        receivedView = (TextView) layout.findViewById(R.id.received);
        datingFromView = (TextView) layout.findViewById(R.id.datingFrom);
        datingToView = (TextView) layout.findViewById(R.id.datingTo);
        donatorView = (TextView) layout.findViewById(R.id.donator);
        producerView = (TextView) layout.findViewById(R.id.producer);
        postNummerView = (TextView) layout.findViewById(R.id.postNummer);

    //    ((MainActivity) getActivity()).updateSearchVisibility();

        setDetailsURI(Model.getInstance().getCurrentDetailsURI());

        return layout;
    }

    public void setDetailsURI(String detailsURI){
        this.detailsURI = detailsURI;

        new AsyncTask<String, Void, Item>() {
            @Override
            protected Item doInBackground(String ... params) {
                return Model.getDAO().getDetailsFromBackEnd(params[0]);
            }

            @Override
            protected void onPostExecute(Item data) {

                if (data != null){
                    Model.getInstance().setCurrentItem(data);
                    Item currentItem = data;
                    Log.d("itemdetails", data.toJSON().toString());

                    // felterne udfyld felterne
                    itemheadlineView.setText(currentItem.getItemHeadline());
                    // TODO handle billeder og lyd
                    itemdescriptionView.setText(currentItem.getItemDescription());
                    receivedView.setText(((currentItem.getItemRecievedAsString() == null) ? null : currentItem.getItemRecievedAsString()));
                    datingFromView.setText(((currentItem.getItemDatingFromAsString() == null) ? null : currentItem.getItemDatingFromAsString()));
                    datingToView.setText(((currentItem.getItemDatingToAsString() == null) ? null : currentItem.getItemDatingToAsString()));

                    donatorView.setText(currentItem.getDonator());
                    producerView.setText(currentItem.getProducer());
                    postNummerView.setText(currentItem.getPostalCode());
                } else {
                    Log.d("itemdetails", "else");
                    //TODO ERROR HANDLING FOR data = null
                }
            }
        }.execute(detailsURI);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public String getDetailsURI(){
        return this.detailsURI;
    }
}
