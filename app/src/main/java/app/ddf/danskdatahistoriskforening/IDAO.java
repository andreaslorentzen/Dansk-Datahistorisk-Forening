package app.ddf.danskdatahistoriskforening;

import android.content.Context;


public interface IDAO {
    int saveItemToDB(Context context, Item item);
    String getOverviewFromBackend();
    Item getDetailsFromBackEnd(String detailsURI);
    int updateItem(Context context, Item item);
}
