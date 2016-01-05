package app.ddf.danskdatahistoriskforening;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    JSONArray items;
    List<String> itemTitles;
    Fragment startFragment;
    ItemListFragment listFragment;
    Fragment detailsFragment;

    private static final String URL = "http://78.46.187.172:4019/items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mainToolbar.setTitle("DDF");
        mainToolbar.setTitleTextColor(-1); // #FFF
        setSupportActionBar(mainToolbar);

        mainToolbar.setNavigationIcon(null);

    //    ActionBar ab = getSupportActionBar();
    //    ab.setDisplayHomeAsUpEnabled(true);





        startFragment = new FrontFragment();
        listFragment = new ItemListFragment();
        detailsFragment = new ItemDetails();

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.frame, startFragment)
            .commit();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void ... params) {
                return Model.getDAO().getOverviewFromBackend();
            }

            @Override
            protected void onPostExecute(String data) {
                if (data != null){
                    System.out.println("data = " + data);

                    try {
                        itemTitles = new ArrayList<String>();
                        items = new JSONArray(data);
                        for (int n = 0; n<items.length(); n++) {
                            JSONObject item = items.getJSONObject(n);
                            itemTitles.add(item.optString("itemheadline", "(ukendt)"));
                        }
                        listFragment.updateItemList(itemTitles);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                //    updateItemList();
                }
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public void startRegister() {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    public void setFragmentList(){
        listFragment.updateItemList(itemTitles);
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
            detailsURI = items.getJSONObject(position).getString("detailsuri");
        } catch(JSONException e){
            e.printStackTrace();
            return;
            //TODO DO SOMETHING USEFULL
        }
        ((ItemDetails) detailsFragment).setDetailsURI(detailsURI);
    }
}
