package app.ddf.danskdatahistoriskforening;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    JSONArray items;

    Fragment startFragment;
    Fragment listFragment;
    Fragment detailsFragment;

    private static final String URL = "http://78.46.187.172:4019/items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mainToolbar.setTitle("Registrede genstande");
        setSupportActionBar(mainToolbar);

        startFragment = new FrontFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, startFragment)
                .addToBackStack(null)
                .commit();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void ... params) {
                BufferedReader br;
                try {
                    br = new BufferedReader(new InputStreamReader(new URL(URL).openStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line).append("\n");
                        line = br.readLine();
                    }
                    return sb.toString();
                } catch (IOException e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String data) {
                if (data != null){
                    System.out.println("data = " + data);

                    try {

                        items = new JSONArray(data);
                        for (int n = 0; n<items.length(); n++) {
                            JSONObject item = items.getJSONObject(n);
                        //    itemTitles.add(item.optString("itemheadline", "(ukendt)"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                //    updateItemList();
                }
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {

    }

}
