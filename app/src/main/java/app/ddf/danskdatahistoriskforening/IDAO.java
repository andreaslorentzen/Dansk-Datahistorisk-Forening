package app.ddf.danskdatahistoriskforening;

import android.content.Context;

public interface IDAO {
    int saveItemToDB(Context context, Item item);
    String getOverviewFromBackend();
    String getDetailsFromBackEnd(String detailsURI);

}
