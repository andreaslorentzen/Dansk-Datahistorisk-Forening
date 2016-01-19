package app.ddf.danskdatahistoriskforening.helper;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.ddf.danskdatahistoriskforening.dal.IDAO;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.dal.TempDAO;
import app.ddf.danskdatahistoriskforening.domain.ListItem;
import app.ddf.danskdatahistoriskforening.domain.Logic;
import app.ddf.danskdatahistoriskforening.domain.UserSelection;

public class Model {

    public IDAO dao = new TempDAO();
    private AsyncTask<String, Void, Item> currentFetchTask;

    public void fetchSelectedListItem() {
        final String uri = Logic.instance.userSelection.selectedListItem.details;
        if (uri == null)
            return;

        currentFetchTask = new AsyncTask<String, Void, Item>() {
            @Override
            protected Item doInBackground(String... params) {
                return dao.getDetailsFromBackEnd(params[0]);
            }

            @Override
            protected void onPostExecute(Item data) {
                if (data != null) {
                    Logic.instance.userSelection.setSelectedItem(data);

                }
            }

        };
        currentFetchTask.execute(uri);

    }

    public void cancelFetch() {
        if (currentFetchTask != null) {
            currentFetchTask.cancel(true);
            currentFetchTask = null;
        }

        dao.cancelDownload();
    }

    public void updateItemList() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return Logic.instance.model.dao.getOverviewFromBackend();
            }

            @Override
            protected void onPostExecute(String data) {
                if (data != null) {
                    System.out.println("data = " + data);

                    try {
                        JSONArray jsonItems = new JSONArray(data);

                        List<ListItem> listItems = new ArrayList<>();


                        for (int n = 0; n < jsonItems.length(); n++) {
                            JSONObject item = jsonItems.getJSONObject(n);

                            ListItem listItem = new ListItem();
                            listItem.details = item.optString("detailsuri");
                            listItem.id = item.optInt("itemid");
                            listItem.title = item.optString("itemheadline", "(ukendt)");
                            listItem.image = item.getString("defaultimage");
                            listItems.add(listItem);
                        }

                        Logic.instance.items = listItems;
                        Logic.instance.searchedItems = Logic.instance.searchManager.search(Logic.instance.userSelection.searchQuery);

                        for (UserSelection.SearchListener observator : Logic.instance.userSelection.searchListeners) {
                            observator.onSearchChange();
                        }

                        Logic.setListUpdated(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }
}
