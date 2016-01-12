package app.ddf.danskdatahistoriskforening;

import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

import app.ddf.danskdatahistoriskforening.dal.IDAO;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.dal.TempDAO;

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
    }

    public String getCurrentDetailsURI() {
        return currentDetailsURI;
    }

    public void setCurrentDetailsURI(String currentDetailsURI) {
        this.currentDetailsURI = currentDetailsURI;
    }

    private String currentSearch;

    public String getCurrentSearch() {
        return currentSearch;
    }

    public void setCurrentSearch(String currentSearch) {
        this.currentSearch = currentSearch;
    }







}
