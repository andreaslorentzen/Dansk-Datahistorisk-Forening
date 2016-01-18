package app.ddf.danskdatahistoriskforening.domain;

import java.util.ArrayList;
import java.util.List;

import app.ddf.danskdatahistoriskforening.dal.Item;

public class UserSelection {

    public String searchQuery;

    public List<SearchObservator> searchObservators = new ArrayList<>();

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item selectedItem) {
        this.selectedItem = selectedItem;
        for (UserSelection.OnSelectItemListener listener : Logic.instance.userSelection.selectItemListeners) {
            listener.OnSelectItem();
        }
    }

    public interface SearchObservator {
        void onSearchChange();
    }

    public ListItem selectedListItem;


    private Item selectedItem;

    public List<OnSelectItemListener> selectItemListeners = new ArrayList<>();

    public interface OnSelectItemListener {
        void OnSelectItem();
    }

}
