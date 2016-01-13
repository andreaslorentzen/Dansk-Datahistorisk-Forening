package app.ddf.danskdatahistoriskforening.dal;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import app.ddf.danskdatahistoriskforening.Model;

/**
 * Created by mathias on 13/01/16.
 */
public class BackgroundService extends IntentService {

    public BackgroundService() {
        super("mBackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplication();
        Item item = intent.getParcelableExtra("item");
        int returnValue = -1;
        if(intent.getStringExtra("event").equals("create")){
            returnValue = Model.getDAO().saveItemToDB(context, item);
        } else if(intent.getStringExtra("event").equals("update")){
            returnValue = Model.getDAO().updateItem(context, item);
        }
        Intent localIntent = new Intent();
        localIntent.setAction(Model.BROADCAST_ACTION);
        localIntent.putExtra("status", returnValue);
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }
}
