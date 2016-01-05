package app.ddf.danskdatahistoriskforening;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ItemDetails extends Fragment {

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
    protected Item currentItem;

    public ItemDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_item_details, container, false);

        itemheadlineView = (TextView) layout.findViewById(R.id.itemheadline);
        imageScrollView = (HorizontalScrollView) layout.findViewById(R.id.imageScrollView);
        itemdescriptionView = (TextView) layout.findViewById(R.id.itemdescription);
        receivedView = (TextView) layout.findViewById(R.id.received);
        datingFromView = (TextView) layout.findViewById(R.id.datingFrom);
        datingToView = (TextView) layout.findViewById(R.id.datingTo);
        donatorView = (TextView) layout.findViewById(R.id.donator);
        producerView = (TextView) layout.findViewById(R.id.producer);
        postNummerView = (TextView) layout.findViewById(R.id.postNummer);

        return layout;
    }

    public void setDetailsURI(String detailsURI){
        this.detailsURI = detailsURI;

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String ... params) {
                return new DAO().getDetailsFromBackEnd(params[0]);
            }

            @Override
            protected void onPostExecute(String data) {
                if (data != null){
                    System.out.println("data = " + data);
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    JSONObject item;

                    try {
                        data = data.substring(1, data.length()-1);
                        item = new JSONObject(data);
                        currentItem = new Item(
                                Integer.parseInt(item.getString("itemid")),
                                item.getString("itemheadline"),
                                item.getString("itemdescription"),
                                ((item.getString("itemreceived") == null || item.getString("itemreceived").equals("")) ? null : formatter.parse(item.getString("itemreceived"))), //TODO ændre til at handle date (Venter på claus backend)
                                ((item.getString("datingfrom") == null || item.getString("datingfrom").equals("")) ? null : formatter.parse(item.getString("datingfrom"))), //TODO ændre til at handle date (Venter på claus backend)
                                ((item.getString("datingto") == null || item.getString("datingto").equals("")) ? null : formatter.parse(item.getString("datingto"))), //TODO ændre til at handle date (Venter på claus backend)
                                item.getString("donator"),
                                item.getString("producer"),
                                item.getString("postnummer")
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    } catch (ParseException e){
                        e.printStackTrace();
                        return;
                    }

                    // udfyld felterne
                    itemheadlineView.setText(currentItem.getItemHeadline());
                    // TODO handle billeder og lyd
                    itemdescriptionView.setText(currentItem.getItemDescription());
                    receivedView.setText(formatter.format(currentItem.getItemRecieved()));
                    datingFromView.setText(formatter.format(currentItem.getItemDatingFrom()));
                    datingToView.setText(formatter.format(currentItem.getItemDatingTo()));
                    donatorView.setText(currentItem.getDonator());
                    producerView.setText(currentItem.getProducer());
                    postNummerView.setText(currentItem.getPostalCode());
                }
            }
        }.execute(detailsURI);

    }

    public String getDetailsURI(){
        return this.detailsURI;
    }
}
