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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import app.ddf.danskdatahistoriskforening.App;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.domain.Logic;
import app.ddf.danskdatahistoriskforening.domain.UserSelection;
import app.ddf.danskdatahistoriskforening.helper.DraftManager;
import app.ddf.danskdatahistoriskforening.item.ItemActivity;
import app.ddf.danskdatahistoriskforening.item.LoadDraftDialogFragment;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener, MenuItemCompat.OnActionExpandListener, LoadDraftDialogFragment.ConfirmDraftLoadListener, DraftManager.OnDraftLoaded {

    private static final int REGISTER_REQUEST = 12;
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

            Logic.instance.model.updateItemList();
            // is an edit
            if(Logic.instance.userSelection.selectedListItem != null){

                Logic.instance.userSelection.setSelectedItem(null);
                Logic.instance.model.fetchSelectedListItem();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.setCurrentActivity(this);
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

        searchView.setQuery(Logic.instance.userSelection.searchQuery, false);

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    protected void onResume() {
        System.out.println(App.isConnected());
        if (!App.isConnected()) {
            findViewById(R.id.internetConnBar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.internetConnBar).setVisibility(View.GONE);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(App.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        if (!Logic.isListUpdated() && App.isConnected()) {
            Logic.instance.model.updateItemList();
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
            Logic.instance.draftManager.loadDraft(this);
        }
    }

    @Override
    public void onDraftLoaded(Item item) {
        if (item != null && item.hasContent()) {
            //load draft dialog
            LoadDraftDialogFragment dialog = new LoadDraftDialogFragment();
            dialog.setDraft(item);
            dialog.show(getSupportFragmentManager(), "LoadDraftDialog");
        } else {
            startRegisterDraft(null);
        }
    }

    private void startRegisterDraft(Item draft){
        Intent i = new Intent(this, ItemActivity.class);
        if(draft != null) {
            i.putExtra("item", (Parcelable) draft);
            Logic.instance.editItem = draft;
        }
        else{
            Logic.instance.editItem = new Item();
        }
        i.putExtra("isNewRegistration", true);
        startActivityForResult(i, REGISTER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGISTER_REQUEST) {
            if(resultCode == RESULT_OK && data.hasExtra("saved")){
                Logic.instance.draftManager.deleteDraft();
            }
        }
    }

    @Override
    public void onDialogPositiveClick(Item draft) {
        Logic.instance.draftManager.deleteDraft();
        startRegisterDraft(draft);
    }

    @Override
    public void onDialogNegativeClick() {
        Logic.instance.draftManager.deleteDraft();
        startRegisterDraft(null);
    }

    public void setFragmentList() {
        if (!App.isConnected() && Logic.instance.items == null) {
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
        if (!App.isConnected()) {
            Toast.makeText(this, "Detaljer kan ikke hentes, da der ikke er internet", Toast.LENGTH_LONG).show();
            return;
        }

        if (Logic.instance.userSelection.selectedListItem.details == null) {
            Toast.makeText(this, "Kan ikke hente detaljer: fejl i liste data", Toast.LENGTH_LONG).show();
            return;
        }

        Logic.instance.userSelection.setSelectedItem(null);

        Logic.instance.model.fetchSelectedListItem();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, new ItemShowFragment())
                .addToBackStack(null)
                .commit();
        boolean expanded = isSearchExpanded();
        String query = Logic.instance.userSelection.searchQuery;
        setSearchButtonVisible(false);
        updateSearchVisibility();
        setSearchExpanded(expanded);
        Logic.instance.userSelection.searchQuery = query;

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
        Log.d("Search",""+newText);
        Logic.instance.userSelection.searchQuery = newText;

        Logic.instance.searchedItems = Logic.instance.searchManager.search(newText);

        for (UserSelection.SearchObservator observator : Logic.instance.userSelection.searchObservators) {
            observator.onSearchChange();
        }

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

                Logic.instance.editItem = Logic.instance.userSelection.getSelectedItem().clone();
                startActivity(new Intent(this, ItemActivity.class));

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
        Log.d("Main", "" + getSupportFragmentManager().getBackStackEntryCount());


        switch (getSupportFragmentManager().getBackStackEntryCount()) {
            case 0:

                break;
            case 1:
                setSearchButtonVisible(true);
                updateSearchVisibility();
                searchView.setQuery(Logic.instance.userSelection.searchQuery, true);
                Logic.instance.model.cancelFetch();
                Logic.instance.userSelection.selectedListItem = null;
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
                if (!Logic.isListUpdated()) {
                    Logic.instance.model.updateItemList();
                //    updateItemList();
                }
            } else
                iBar.setVisibility(View.VISIBLE);
        }
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
