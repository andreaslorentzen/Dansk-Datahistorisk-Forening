package app.ddf.danskdatahistoriskforening.dal;

import android.content.Context;
import android.net.Uri;


public interface IDAO {
    int saveItemToDB(Context context, Item item);
    String getOverviewFromBackend();
    Item getDetailsFromBackEnd(String detailsURI);
    int updateItem(Context context, Item item);
    void postFile(Context context, Uri path, int itemID, String extension);

    void cancelDownload();
}
