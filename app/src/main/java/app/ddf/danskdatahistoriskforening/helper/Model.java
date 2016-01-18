package app.ddf.danskdatahistoriskforening.helper;

import android.os.AsyncTask;

import app.ddf.danskdatahistoriskforening.dal.IDAO;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.dal.TempDAO;
import app.ddf.danskdatahistoriskforening.domain.Logic;
import app.ddf.danskdatahistoriskforening.domain.UserSelection;

public class Model {

    private IDAO dao = new TempDAO();
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
                //    Model.getInstance().setCurrentItem(data);
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
}
