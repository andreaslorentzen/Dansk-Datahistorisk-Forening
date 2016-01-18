package app.ddf.danskdatahistoriskforening.domain;

import android.net.Uri;

import java.util.List;

import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.helper.DraftManager;
import app.ddf.danskdatahistoriskforening.helper.Model;
import app.ddf.danskdatahistoriskforening.helper.SearchManager;

public class Logic {

    public static Logic instance;
    private static boolean itemListUpdated = false;
    public UserSelection userSelection;

    public List<ListItem> items;
    public List<ListItem> searchedItems;
    public SearchManager searchManager;
    public Model model;
    public Item editItem;
    public DraftManager draftManager;
    public Uri tempUri;

    public Logic() {
        this.userSelection = new UserSelection();
        this.searchManager = new SearchManager();
        this.draftManager = new DraftManager();
        this.model = new Model();
    }

    public static boolean isListUpdated() {
        return itemListUpdated;
    }

    public static void setListUpdated(boolean listUpdated) {
        itemListUpdated = listUpdated;
    }

    public boolean isNewRegistration() {
        return editItem == null || editItem.getItemId() == 0;
    }
}
