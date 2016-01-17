package app.ddf.danskdatahistoriskforening.domain;

import java.util.ArrayList;
import java.util.List;

import app.ddf.danskdatahistoriskforening.dal.Item;

public class UserSelection {

    public String searchQuery;

    public List<SearchObservator> searchObservators = new ArrayList<>();

    public interface SearchObservator {
        void onSearchChange();
    }

    public ListItem selectedListItem;


    public Item selectedItem;

    public List<OnSelectItemListener> selectItemListeners = new ArrayList<>();

    public interface OnSelectItemListener {
        void OnSelectItem();
    }

}
