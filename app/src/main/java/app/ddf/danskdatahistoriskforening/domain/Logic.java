package app.ddf.danskdatahistoriskforening.domain;

import java.util.List;

public class Logic {

    public static Logic instance;
    public UserSelection userSelection;

    public List<ListItem> items;

    public Logic() {
        this.userSelection = new UserSelection();
    }
}
