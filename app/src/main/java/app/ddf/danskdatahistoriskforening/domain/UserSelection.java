package app.ddf.danskdatahistoriskforening.domain;

import java.util.ArrayList;
import java.util.List;

public class UserSelection {

    public String searchQuery;

    public List<SearchObservator> searchObservators = new ArrayList<>();

    public interface SearchObservator {
        void onSearchChange();
    }

    public ListItem selectedListItem;


}
