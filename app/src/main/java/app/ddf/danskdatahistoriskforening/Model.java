package app.ddf.danskdatahistoriskforening;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

import app.ddf.danskdatahistoriskforening.dal.IDAO;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.dal.TempDAO;
import app.ddf.danskdatahistoriskforening.helper.SearchManager;

/**
 * Created by mathias on 05/01/16.
 */
public class Model {
    private static Model ourInstance;
    private static IDAO dao = new TempDAO();
    private static boolean itemListUpdated = false;
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private static boolean isConnected;
    private static AppCompatActivity currentActivity;
    private static SearchManager sm = new SearchManager();
    public static final String BROADCAST_ACTION = "com.datahistoriskforening.android.backgroundservice.BROADCAST";

    public static Model getInstance() {
        if (ourInstance == null) {
            ourInstance = new Model();
        }
        return ourInstance;
    }

    private Model() {
    }

    public static boolean isListUpdated(){return itemListUpdated;}

    public static void setListUpdated(boolean listUpdated){itemListUpdated = listUpdated;}

    public static IDAO getDAO(){
        return dao;
    }

    public static SimpleDateFormat getFormatter(){return formatter;}

    private List<JSONObject> items;
    private List<String> itemTitles;
    private String currentDetailsURI;
    private Item currentItem;

    public static boolean isConnected() {
        return isConnected;
    }

    public static void setIsConnected(boolean isConnected) {
        Model.isConnected = isConnected;
    }

    public static AppCompatActivity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(AppCompatActivity currentActivity) {
        Model.currentActivity = currentActivity;
    }

    public List<String> getItemTitles() {
        return itemTitles;
    }

    public void setItemTitles(List<String> itemTitles) {
        this.itemTitles = itemTitles;
    }

    public List<JSONObject> getItems() {
        return items;
    }

    public void setItems(List<JSONObject> items) {
        this.items = items;
    }

    public Item getCurrentItem(){
        return currentItem;
    }
    public void setCurrentItem(Item currentItem) {
        this.currentItem = currentItem;
        if(currentItemChangeListener != null){
            currentItemChangeListener.onCurrentItemChange(this.currentItem);
        }
    }

    public String getCurrentDetailsURI() {
        return currentDetailsURI;
    }

    public void setCurrentDetailsURI(String currentDetailsURI) {
        this.currentDetailsURI = currentDetailsURI;
    }

    public SearchManager getSearchManager() {
        return sm;
    }

    private AsyncTask<String, Void, Item> currentFetchTask;
    public void fetchCurrentItem() {
        final String uri = Model.getInstance().getCurrentDetailsURI();
        if(uri == null)
            return;

        currentFetchTask = new AsyncTask<String, Void, Item>() {
            @Override
            protected Item doInBackground(String... params) {
                return Model.getDAO().getDetailsFromBackEnd(params[0]);
            }

            @Override
            protected void onPostExecute(Item data) {
                Log.d("hello","execute");
                if (data != null) {
                    Model.getInstance().setCurrentItem(data);
                }
                currentFetchTask = null;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                Log.d("hello","cnacled");
            }
        };
        currentFetchTask.execute(uri);

    }

    private OnCurrentItemChangeListener currentItemChangeListener;

    public void setOnCurrentItemChangeListener(OnCurrentItemChangeListener currentItemChangeListener) {
        this.currentItemChangeListener = currentItemChangeListener;
    }

    public void cancelFetch() {
        if(currentFetchTask != null)
            currentFetchTask.cancel(true);

        Model.getDAO().cancelDownload();
    }

    public interface OnCurrentItemChangeListener {
        void onCurrentItemChange(Item currentItem);
    }
}
