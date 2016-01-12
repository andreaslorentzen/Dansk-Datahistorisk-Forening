package app.ddf.danskdatahistoriskforening;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import app.ddf.danskdatahistoriskforening.item.ItemActivity;
import app.ddf.danskdatahistoriskforening.main.MainActivity;

/**
 * Created by mathias on 12/01/16.
 */
public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        Model.setIsConnected(isConnected);

        if(Model.getCurrentActivity() instanceof MainActivity)
            ((MainActivity)Model.getCurrentActivity()).updateInternet(isConnected);
        else if(Model.getCurrentActivity() instanceof ItemActivity)
            ((ItemActivity)Model.getCurrentActivity()).updateInternet(isConnected);
    }
}
