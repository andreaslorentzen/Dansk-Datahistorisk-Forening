package app.ddf.danskdatahistoriskforening;

import android.Manifest;
import android.app.Activity;
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

/**
 * Created by mathias on 12/01/16.
 */
public class App extends Application {
    //TODO calculate acceptable thumbnail dimensions based on screensize or available space
    public static final int MAX_THUMBNAIL_WIDTH = 150;
    public static final int MAX_THUMBNAIL_HEIGHT = 250;

    public static boolean hasRecordAudioPermission(AppCompatActivity activity) {
        return Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED;
    }

    public static void hideKeyboard(FragmentActivity activity, View view){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    @Override
    public void onCreate(){
        System.out.println("Inside the APP");
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

        Model.setIsConnected(activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        super.onCreate();
    }
}
