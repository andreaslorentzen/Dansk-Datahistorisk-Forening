package app.ddf.danskdatahistoriskforening.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.item.ItemActivity;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener, MenuItemCompat.OnActionExpandListener {

    Toolbar mainToolbar;

    MenuItem searchButton;
    MenuItem editButton;
    SearchView searchView;

    private static final String URL = "http://78.46.187.172:4019/items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new FrontFragment())
                    .commit();


        }
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mainToolbar.setNavigationIcon(null);
        setSupportActionBar(mainToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        editButton = menu.findItem(R.id.editModeItem);
        editButton.setOnMenuItemClickListener(this);

        searchButton = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchButton, this);

        searchView = (SearchView) MenuItemCompat.getActionView(searchButton);
        searchView.setIconifiedByDefault(false);

        updateSearchVisibility();

        searchView.setQuery(Model.getInstance().getCurrentSearch(), false);

        searchView.setOnQueryTextListener(this);

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
                            List<JSONObject> items;
                            List<String> itemTitles;
                            itemTitles = new ArrayList<>();
                            items = new ArrayList<>();
                            JSONArray jsonItems = new JSONArray(data);

                            for (int n = 0; n < jsonItems.length(); n++) {
                                JSONObject item = jsonItems.getJSONObject(n);
                                itemTitles.add(item.optString("itemheadline", "(ukendt)"));
                                items.add(item);
                            }
                            Model.getInstance().setItemTitles(itemTitles);
                            Model.getInstance().setItems(items);
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
        if(getSupportFragmentManager().getBackStackEntryCount() == 0){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new ItemListFragment())
                    .addToBackStack("list")
                    .commit();
        }
    }

    public void setFragmentDetails(int position) {
        try {
            String detailsURI = Model.getInstance().getItems().get(position).getString("detailsuri");
            if(detailsURI == null)
                // Maybe throw exception
                return;
            Model.getInstance().setCurrentDetailsURI(detailsURI);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new ItemShowFragment())
                    .addToBackStack(null)
                    .commit();
            boolean expanded = Model.getInstance().isSearchExpanded();
            String query = Model.getInstance().getCurrentSearch();
            Model.getInstance().setSearchButtonVisible(false);
            updateSearchVisibility();
            Model.getInstance().setSearchExpanded(expanded);
            Model.getInstance().setCurrentSearch(query);
        } catch(JSONException e){
            e.printStackTrace();
            return;
            //TODO DO SOMETHING USEFULL
        }
    }

    private void updateSearchVisibility(){
        boolean isSerSearchVisible = Model.getInstance().isSearchButtonVisible();
        searchButton.setVisible(isSerSearchVisible);
        editButton.setVisible(!isSerSearchVisible);

        if(!isSerSearchVisible){
            MenuItemCompat.collapseActionView(searchButton);
        }
        else{
            if(Model.getInstance().isSearchExpanded()) {
                if (!MenuItemCompat.isActionViewExpanded(searchButton))
                    MenuItemCompat.expandActionView(searchButton);
            }
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Model.getInstance().setCurrentSearch(newText);
        if(getSupportFragmentManager().getBackStackEntryCount()>0){
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            ((ItemListFragment)fragments.get(1)).searchItemList();
        }

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item == editButton){
            Intent i = new Intent(this, ItemActivity.class);
            i.putExtra("item", Model.getInstance().getCurrentItem());
            startActivity(i);
        }
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        Model.getInstance().setSearchExpanded(true);
        setFragmentList();
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        Model.getInstance().setSearchExpanded(false);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        switch (getSupportFragmentManager().getBackStackEntryCount()){
            case 0:

                break;
            case 1:
                Model.getInstance().setSearchButtonVisible(true);
                String query = Model.getInstance().getCurrentSearch();
                updateSearchVisibility();
                searchView.setQuery(query, false);
                break;
        }
    }
}
