package app.ddf.danskdatahistoriskforening;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by mathias on 12/01/16.
 */
public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

        Model.setIsConnected(activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }
}
