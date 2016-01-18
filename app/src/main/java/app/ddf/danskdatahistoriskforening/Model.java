package app.ddf.danskdatahistoriskforening;

import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

import app.ddf.danskdatahistoriskforening.dal.IDAO;
import app.ddf.danskdatahistoriskforening.dal.TempDAO;

public class Model {
    private static Model ourInstance;
    private static IDAO dao = new TempDAO();
    private static boolean itemListUpdated = false;
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private static boolean isConnected;
    private static AppCompatActivity currentActivity;

    public static final String BROADCAST_ACTION = "com.datahistoriskforening.android.backgroundservice.BROADCAST";

    public static Model getInstance() {
        if (ourInstance == null) {
            ourInstance = new Model();
        }
        return ourInstance;
    }

    private Model() {

    }

    public static boolean isListUpdated() {
        return itemListUpdated;
    }

    public static void setListUpdated(boolean listUpdated) {
        itemListUpdated = listUpdated;
    }

    public static IDAO getDAO() {
        return dao;
    }

    public static SimpleDateFormat getFormatter() {
        return formatter;
    }

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

}
