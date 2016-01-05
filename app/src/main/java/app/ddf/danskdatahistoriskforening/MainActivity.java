package app.ddf.danskdatahistoriskforening;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    JSONArray items;

    Fragment startFragment;
    ListFragment listFragment;
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
        listFragment = new ListFragment();
        detailsFragment = new ItemDetails();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, startFragment)
                .addToBackStack(null)
                .commit();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void ... params) {
                return new tempDAO().getOverviewFromBackend();
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
                    }
                //    updateItemList();
                }
            }
        }.execute();
    }


    public void startRegister() {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    public void setFragmentList(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, listFragment)
                .addToBackStack(null)
                .commit();
    }

    public void setFragmentDetails(int position) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, detailsFragment)
                .addToBackStack(null)
                .commit();
        String detailsURI;
        try {
            detailsURI = items.getJSONObject(position).getString("detailsURI");
        } catch(JSONException e){
            e.printStackTrace();
            return;
            //TODO DO SOMETHING USEFULL
        }
        ((ItemDetails) detailsFragment).setDetailsURI(detailsURI);
    }
}
