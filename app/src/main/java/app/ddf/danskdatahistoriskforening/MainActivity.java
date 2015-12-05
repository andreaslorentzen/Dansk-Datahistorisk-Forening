package app.ddf.danskdatahistoriskforening;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    FloatingActionButton addActivityButton;
    ListView itemList;
    JSONArray items;
    List<String> itemTitles;
    private static final String URL = "http://78.46.187.172:4019/items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mainToolbar.setTitle("Registrede genstande");
        setSupportActionBar(mainToolbar);

        addActivityButton = (FloatingActionButton) findViewById(R.id.fab);
        addActivityButton.setOnClickListener(this);

        itemList = (ListView) findViewById(R.id.itemList);
        itemList.setOnItemClickListener(this);
        itemTitles = new ArrayList<>();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void ... params) {
                return new DAO().getOverviewFromBackend();
            }

            @Override
            protected void onPostExecute(String data) {
                if (data != null){
                    System.out.println("data = " + data);

                    try {

                        items = new JSONArray(data);
                        for (int n = 0; n<items.length(); n++) {
                            JSONObject item = items.getJSONObject(n);
                            itemTitles.add(item.optString("itemheadline", "(ukendt)"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    updateItemList();
                }
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        if(v == addActivityButton) {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        }
    }

    public void updateItemList(){
        itemList.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, itemTitles));
    }

    @Override
    public void onItemClick(AdapterView<?> aV, View v, int position, long l){
        Intent i = new Intent(this, ItemDetailsAcitivty.class);

        try {
            i.putExtra("itemTitle", items.getJSONObject(position).optString("itemheadline"));
            i.putExtra("detailsURI", items.getJSONObject(position).optString("detailsuri"));
        } catch (JSONException e){
            e.printStackTrace();
            return;
        }
        startActivity(i);
    }
}
