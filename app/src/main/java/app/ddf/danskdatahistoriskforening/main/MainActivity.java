package app.ddf.danskdatahistoriskforening.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.item.ItemActivity;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener {

    JSONArray items;
    List<String> itemTitles;
    Fragment startFragment;
    ItemListFragment listFragment;
    Fragment detailsFragment;

    MenuItem searchItem;
    MenuItem editModeItem;
    SearchView searchView;

    boolean searchVisible = true;

    private static final String URL = "http://78.46.187.172:4019/items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mainToolbar.setTitle("DDF");
        mainToolbar.setTitleTextColor(-1); // #FFF
        setSupportActionBar(mainToolbar);

        mainToolbar.setNavigationIcon(null);

    //    ActionBar ab = getSupportActionBar();
    //    ab.setDisplayHomeAsUpEnabled(true);





        startFragment = new FrontFragment();
        listFragment = new ItemListFragment();
        detailsFragment = new ItemShowFragment();

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.frame, startFragment)
            .commit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        editModeItem = menu.findItem(R.id.editModeItem);
        editModeItem.setOnMenuItemClickListener(this);

        searchItem = menu.findItem(R.id.action_search);
        searchItem.setOnMenuItemClickListener(this);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        if(searchVisible){
            searchItem.setVisible(true);
            editModeItem.setVisible(false);
        }
        else{
            searchItem.setVisible(false);
            editModeItem.setVisible(true);
        }

        return true;
    }

    @Override
    protected void onResume(){
        if(!Model.isListUpdated()) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    return Model.getDAO().getOverviewFromBackend();
                }

                @Override
                protected void onPostExecute(String data) {
                    if (data != null) {
                        System.out.println("data = " + data);

                        try {
                            itemTitles = new ArrayList<String>();
                            items = new JSONArray(data);
                            for (int n = 0; n < items.length(); n++) {
                                JSONObject item = items.getJSONObject(n);
                                itemTitles.add(item.optString("itemheadline", "(ukendt)"));
                            }

                            Model.setListUpdated(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.execute();
        }
        super.onResume();
    }

    public void startRegister() {
        Intent i = new Intent(this, ItemActivity.class);
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
        ((ItemShowFragment) detailsFragment).setDetailsURI(detailsURI);
        searchItem.setVisible(false);
        editModeItem.setVisible(true);
    }

    public void setSearchVisible(boolean isSerSearchVisible){
        searchVisible = isSerSearchVisible;
        searchItem.setVisible(isSerSearchVisible);
        editModeItem.setVisible(!isSerSearchVisible);
        if(!isSerSearchVisible)
            MenuItemCompat.collapseActionView(searchItem);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        listFragment.searchItemList(newText);
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        System.out.println("menu");
        if(item == searchItem){
            System.out.println("menu");
            setFragmentList();
        }
        else if(item == editModeItem){
            Intent i = new Intent(this, ItemActivity.class);
            System.out.println(((ItemShowFragment) detailsFragment).currentItem.toJSON().toString());
            i.putExtra("item", ((ItemShowFragment) detailsFragment).currentItem);
            startActivity(i);
        }
        return true;
    }

}
