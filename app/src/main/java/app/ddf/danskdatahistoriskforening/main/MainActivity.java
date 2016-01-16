package app.ddf.danskdatahistoriskforening.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.helper.LocalMediaStorage;
import app.ddf.danskdatahistoriskforening.helper.SearchManager;
import app.ddf.danskdatahistoriskforening.item.ItemActivity;
import app.ddf.danskdatahistoriskforening.item.LoadDraftDialogFragment;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener, MenuItemCompat.OnActionExpandListener, LoadDraftDialogFragment.ConfirmDraftLoadListener {

    MenuItem searchButton;
    MenuItem editButton;
    SearchView searchView;

    private boolean isSearchExpanded;
    private boolean searchButtonVisible = true;

    private boolean canEdit = false;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        //    intent.getAction()
            MainActivity.this.checkForErrors(intent.getIntExtra("status", 0));
            Model.getInstance().setCurrentItem(null);
            Log.d("Current sat til null", "" + (Model.getInstance().getCurrentItem() == null));
            Model.getInstance().fetchCurrentItem();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Model.setCurrentActivity(this);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame, new FrontFragment())
                    .commit();

        } else {
            setSearchExpanded(savedInstanceState.getBoolean("isSearchExpanded"));
            setSearchButtonVisible(savedInstanceState.getBoolean("isSearchButtonVisible", true));

        }
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
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

        searchView.setQuery(Model.getInstance().getSearchManager().getCurrentSearch(), false);

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    protected void onResume() {
        System.out.println(Model.isConnected());
        if (!Model.isConnected()) {
            findViewById(R.id.internetConnBar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.internetConnBar).setVisibility(View.GONE);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Model.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        if (!Model.isListUpdated() && Model.isConnected()) {
            updateItemList();
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
        File file = new File(getFilesDir().getPath() + "/" + "draft");

        Log.d("draft", "draft to load: " + file.exists());

        if(!file.exists()){
            startRegisterDraft(null);
        }
        else {
            (new LoadDraftTask()).execute();
        }
    }

    private void startRegisterDraft(Item draft){
        Intent i = new Intent(this, ItemActivity.class);
        if(draft != null) {
            i.putExtra("item", (Parcelable) draft);
        }
        i.putExtra("isNewRegistration", true);
        startActivity(i);
    }

    @Override
    public void onDialogPositiveClick(Item draft) {
        (new DeleteDraftTask()).execute();
        startRegisterDraft(draft);
    }

    @Override
    public void onDialogNegativeClick() {
        (new DeleteDraftTask()).execute();
        startRegisterDraft(null);
    }

    private class DeleteDraftTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            File file = new File(getFilesDir().getPath() + "/" + "draft");
            file.delete();

            return null;
        }
    }

    private class LoadDraftTask extends AsyncTask<Void, Void, Item> {

        @Override
        protected Item doInBackground(Void... params) {
            Item draft;

            try {
                FileInputStream fis = new FileInputStream(getFilesDir().getPath() + "/" + "draft");
                ObjectInputStream ois = new ObjectInputStream(fis);
                draft = (Item) ois.readObject();
                ois.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }

            return draft;
        }

        @Override
        protected void onPostExecute(Item item) {
            if(item != null && item.hasContent()){
                //load draft dialog
                LoadDraftDialogFragment dialog = new LoadDraftDialogFragment();
                dialog.setDraft(item);
                dialog.show(getSupportFragmentManager(), "LoadDraftDialog");
            }
            else {
                startRegisterDraft(null);
            }
        }
    }

    public void setFragmentList() {
        if (!Model.isConnected() && Model.getInstance().getItemTitles() == null) {
            Toast.makeText(this, "Der kan ikke hentes nogen liste uden internet", Toast.LENGTH_SHORT).show();
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new ItemListFragment())
                    .addToBackStack("list")
                    .commit();
        }
    }

    public void setFragmentDetails(int position) {
        if (!Model.isConnected()) {
            Toast.makeText(this, "Detaljer kan ikke hentes, da der ikke er internet", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            String detailsURI = Model.getCurrentJSONObjects().get(position).getString("detailsuri");
            if (detailsURI == null)
                // Maybe throw exception
                return;
            Model.getInstance().setCurrentDetailsURI(detailsURI);
            Model.getInstance().setCurrentItem(null);
            Model.getInstance().fetchCurrentItem();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new ItemShowFragment())
                    .addToBackStack(null)
                    .commit();
            boolean expanded = isSearchExpanded();
            setSearchButtonVisible(false);
            updateSearchVisibility();
            setSearchExpanded(expanded);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
            //TODO DO SOMETHING USEFULL
        }
    }

    private void updateSearchVisibility() {
        boolean isSerSearchVisible = isSearchButtonVisible();
        searchButton.setVisible(isSerSearchVisible);
        editButton.setVisible(!isSerSearchVisible && canEdit);

        if (!isSerSearchVisible) {
            MenuItemCompat.collapseActionView(searchButton);
        } else {
            if (isSearchExpanded()) {
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
        Model.getInstance().getSearchManager().searchItemList(newText);
        return true;
    }

    public void disableEdit(){
        canEdit = false;
        if(editButton != null) {
            editButton.setVisible(false);
        }
    }

    public void enableEdit(){
        canEdit = true;
        if(editButton != null) {
            editButton.setVisible(true);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item == editButton) {
            if(canEdit) {
                Intent i = new Intent(this, ItemActivity.class);
                i.putExtra("item", (Parcelable) Model.getInstance().getCurrentItem());
                startActivity(i);
            }
            else{
                Toast.makeText(this, "Vent mens genstandens oplysninger hentes", Toast.LENGTH_LONG).show();
            }
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

        switch (getSupportFragmentManager().getBackStackEntryCount()) {
            case 0:

                break;
            case 1:
                setSearchButtonVisible(true);
                String query = Model.getInstance().getSearchManager().getCurrentSearch();
                updateSearchVisibility();
                searchView.setQuery(query, false);
                Model.getInstance().cancelFetch();
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

    public void updateInternet(boolean isConnected) {
        TextView iBar = (TextView) findViewById(R.id.internetConnBar);
        if (iBar != null) {
            if (isConnected) {
                iBar.setVisibility(View.GONE);
                if (!Model.isListUpdated()) {
                    updateItemList();
                }
            } else
                iBar.setVisibility(View.VISIBLE);
        }
    }

    public void updateItemList() {
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
                        if (SearchManager.getSearchList() != null)
                            Model.getInstance().getSearchManager().searchItemList(Model.getInstance().getSearchManager().getCurrentSearch());
                        Model.setListUpdated(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    private void checkForErrors(int responseCode) {
        switch (responseCode) {
            case -1:
                Toast.makeText(this, "Genstanden blev sendt til severen", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "Enheden er ikke forbundet til internettet!", Toast.LENGTH_LONG).show();
                break;
            case 3:
                Toast.makeText(this, "Server problem", Toast.LENGTH_LONG).show();
                break;
            case 4:
                Toast.makeText(this, "Kunne ikke forbinde til serveren", Toast.LENGTH_LONG).show();
                break;
            case 5:
                Toast.makeText(this, "Server problem", Toast.LENGTH_LONG).show(); // JSON problem
            default:
                Toast.makeText(this, "Noget gik galt", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
