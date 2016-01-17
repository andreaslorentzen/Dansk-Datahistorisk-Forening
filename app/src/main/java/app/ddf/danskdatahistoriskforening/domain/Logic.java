package app.ddf.danskdatahistoriskforening.domain;

import java.util.List;

import app.ddf.danskdatahistoriskforening.helper.Model;
import app.ddf.danskdatahistoriskforening.helper.SearchManager;

public class Logic {

    public static Logic instance;
    public UserSelection userSelection;

    public List<ListItem> items;
    public List<ListItem> searchedItems;
    public SearchManager searchManager;
    public Model model;

    public Logic() {
        this.userSelection = new UserSelection();
        this.searchManager = new SearchManager();
        this.model = new Model();
    }
}
