package app.ddf.danskdatahistoriskforening;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by mathias on 12/01/16.
 */
public class App extends Application {

    @Override
    public void onCreate(){
        System.out.println("Inside the APP");
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

        Model.setIsConnected(activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        super.onCreate();
    }
}
