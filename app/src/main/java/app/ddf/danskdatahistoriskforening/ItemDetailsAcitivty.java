package app.ddf.danskdatahistoriskforening;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by mathias on 05/12/15.
 */
public class ItemDetailsAcitivty extends AppCompatActivity implements View.OnClickListener {

    EditText titleView;
    EditText descriptionView;
    Toolbar itemDetailsBar;
    Item chosenItem;
    String detailsURI;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Intent i = getIntent();
        detailsURI = i.getStringExtra("detailsURI");
        String itemHeadLine = i.getStringExtra("itemheadline");

        //Fill out Toolbar
        itemDetailsBar = (Toolbar) findViewById(R.id.item_details_toolbar);
        itemDetailsBar.setTitle(itemHeadLine);

        //fill out title
        titleView = (EditText) findViewById(R.id.titleView);
        titleView.setText(itemHeadLine);

        //descriptionView = (EditText) findViewById(R.id.descriptionView);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void ... params) {
                return new DAO().getDetailsFromBackEnd(detailsURI);
            }

            @Override
            protected void onPostExecute(String data) {
                if (data != null){
                    System.out.println("data = " + data);
                    JSONObject item;
                    try {
                        data = data.substring(1, data.length()-1);
                        item = new JSONObject(data);
                        chosenItem = new Item(
                                Integer.parseInt(item.optString("itemid")),
                                item.optString("itemheadline"),
                                item.optString("itemdescription"),
                                null, //TODO ændre til at handle date (Venter på claus backend)
                                null, //TODO ændre til at handle date (Venter på claus backend)
                                null, //TODO ændre til at handle date (Venter på claus backend)
                                item.optString("donator"),
                                item.optString("producer"),
                                item.optString("postnummer")
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    descriptionView.setText(chosenItem.getItemDescription());
                }
            }
        }.execute();
    }

    @Override
    public void onClick(View v){

    }
}
