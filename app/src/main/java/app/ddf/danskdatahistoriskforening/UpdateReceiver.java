package app.ddf.danskdatahistoriskforening;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import app.ddf.danskdatahistoriskforening.item.ItemActivity;
import app.ddf.danskdatahistoriskforening.main.MainActivity;

public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        App.setIsConnected(isConnected);

        if(App.getCurrentActivity() instanceof MainActivity)
            ((MainActivity) App.getCurrentActivity()).updateInternet(isConnected);
        else if(App.getCurrentActivity() instanceof ItemActivity)
            ((ItemActivity) App.getCurrentActivity()).updateInternet(isConnected);
    }
}
