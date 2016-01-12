package app.ddf.danskdatahistoriskforening.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.helper.LocalMediaStorage;
import app.ddf.danskdatahistoriskforening.item.ItemActivity;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener, MenuItemCompat.OnActionExpandListener {

    Toolbar mainToolbar;

    TextView internetBar;

    MenuItem searchButton;
    MenuItem editButton;
    SearchView searchView;

    private boolean isSearchExpanded;
    private boolean searchButtonVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Model.setCurrentActivity(this);
        internetBar = (TextView) findViewById(R.id.internetConnBar);
        setContentView(R.layout.activity_main);
        LocalMediaStorage.setContext(this);
        if(savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new FrontFragment())
                    .commit();
        }
        else{
            setSearchExpanded(savedInstanceState.getBoolean("isSearchExpanded"));
            setSearchButtonVisible(savedInstanceState.getBoolean("isSearchButtonVisible", true));

        }
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mainToolbar.setNavigationIcon(null);
        setSupportActionBar(mainToolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("menu new");
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
        System.out.println(Model.isConnected());
        if(!Model.isConnected()){
            findViewById(R.id.internetConnBar).setVisibility(View.VISIBLE);
        } else{
            findViewById(R.id.internetConnBar).setVisibility(View.GONE);
        }
        if(!Model.isListUpdated() && Model.isConnected()) {
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSearchExpanded", isSearchExpanded());
        outState.putBoolean("isSearchButtonVisible", isSearchButtonVisible());
    }

    public void startRegister() {
        Intent i = new Intent(this, ItemActivity.class);
        startActivity(i);
    }

    public void setFragmentList(){
        // TODO Add check for is connected and if the list allready exists.
        if(getSupportFragmentManager().getBackStackEntryCount() == 0){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new ItemListFragment())
                    .addToBackStack("list")
                    .commit();
        }
    }

    public void setFragmentDetails(int position) {
        if(!Model.isConnected()){
            Toast.makeText(this, "Detaljer kan ikke hentes, da der ikke er internet", Toast.LENGTH_LONG).show();
            return;
        }
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
            boolean expanded = isSearchExpanded();
            String query = Model.getInstance().getCurrentSearch();
            setSearchButtonVisible(false);
            updateSearchVisibility();
            setSearchExpanded(expanded);
            Model.getInstance().setCurrentSearch(query);
        } catch(JSONException e){
            e.printStackTrace();
            return;
            //TODO DO SOMETHING USEFULL
        }
    }

    private void updateSearchVisibility(){
        boolean isSerSearchVisible = isSearchButtonVisible();
        searchButton.setVisible(isSerSearchVisible);
        editButton.setVisible(!isSerSearchVisible);

        if(!isSerSearchVisible){
            MenuItemCompat.collapseActionView(searchButton);
        }
        else{
            if(isSearchExpanded()) {
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
        setSearchExpanded(true);
        setFragmentList();
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        setSearchExpanded(false);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        switch (getSupportFragmentManager().getBackStackEntryCount()){
            case 0:

                break;
            case 1:
                setSearchButtonVisible(true);
                String query = Model.getInstance().getCurrentSearch();
                updateSearchVisibility();
                searchView.setQuery(query, false);
                break;
        }
    }

    public boolean isSearchExpanded() {
        return isSearchExpanded;
    }

    public void setSearchExpanded(boolean searchExpanded) {
        this.isSearchExpanded = searchExpanded;
    }

    public boolean isSearchButtonVisible() {
        return searchButtonVisible;
    }

    public void setSearchButtonVisible(boolean searchButtonVisible) {
        this.searchButtonVisible = searchButtonVisible;
    }

    public void updateInternet(boolean isConnected){
        if(internetBar != null){
            if(isConnected)
                internetBar.setVisibility(View.GONE);
            else
                internetBar.setVisibility(View.VISIBLE);
        }
    }
}
