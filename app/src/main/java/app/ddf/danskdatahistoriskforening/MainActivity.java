package app.ddf.danskdatahistoriskforening;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
        setSupportActionBar(mainToolbar);

        itemList = (ListView) findViewById(R.id.itemList);
        itemTitles = new ArrayList<String>();

        new AsyncTask() {
            protected Object doInBackground(Object... arg0) {
                BufferedReader br;
                try {
                    br = new BufferedReader(new InputStreamReader(new URL(URL).openStream()));
                    StringBuilder sb = new StringBuilder();
                    String linje = br.readLine();
                    while (linje != null) {
                        sb.append(linje + "\n");
                        linje = br.readLine();
                    }
                    return sb.toString();
                } catch (IOException e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Object resultat) {
                if (resultat != null){
                    String data = (String) resultat;
                    System.out.println("data = " + data);

                    try {
                        items = new JSONArray(data);

                        for (int n = 0; n<items.length(); n++) {
                            JSONObject item = items.getJSONObject(n);
                            itemTitles.add(item.optString("itemheadline", "(ukendt)"));

                            System.out.println("ihead = " + item.optString("itemheadline"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    updateItemList();
                }
            }
        }.execute();

        addActivityButton = (FloatingActionButton) findViewById(R.id.fab);
        addActivityButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == addActivityButton){
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        }
    }

    public void updateItemList(){
        itemList.setAdapter(new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, itemTitles));
    }
}
