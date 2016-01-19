package app.ddf.danskdatahistoriskforening.helper;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;

import app.ddf.danskdatahistoriskforening.domain.Logic;

public class App extends Application {
    //TODO calculate acceptable thumbnail dimensions based on screensize or available space
    public static final int MAX_THUMBNAIL_WIDTH = 150;
    public static final int MAX_THUMBNAIL_HEIGHT = 250;
    public static final String BROADCAST_ACTION = "com.datahistoriskforening.android.backgroundservice.BROADCAST";

    private static boolean isConnected;
    private static AppCompatActivity currentActivity;
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public static boolean hasRecordAudioPermission(AppCompatActivity activity) {
        return Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED;
    }

    public static void hideKeyboard(FragmentActivity activity, View view) {
        if (activity != null && view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );
        }
    }

    @Override
    public void onCreate() {
        Logic.instance = new Logic();

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

        setIsConnected(activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        super.onCreate();
    }

    public static boolean isConnected() {
        return isConnected;
    }

    public static void setIsConnected(boolean isConnected) {
        App.isConnected = isConnected;
    }

    public static AppCompatActivity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(AppCompatActivity currentActivity) {
        App.currentActivity = currentActivity;
    }

    public static SimpleDateFormat getFormatter() {
        return formatter;
    }

}
