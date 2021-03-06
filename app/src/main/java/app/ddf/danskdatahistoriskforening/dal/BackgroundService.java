package app.ddf.danskdatahistoriskforening.dal;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import app.ddf.danskdatahistoriskforening.helper.App;
import app.ddf.danskdatahistoriskforening.domain.Logic;

public class BackgroundService extends IntentService {

    public BackgroundService() {
        super("mBackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplication();
        Item item = Logic.instance.editItem;
        int returnValue;
        if(Logic.instance.isNewRegistration()){
            returnValue = Logic.instance.model.dao.saveItemToDB(context, item);
        } else {
            returnValue = Logic.instance.model.dao.updateItem(context, item);
        }
        Intent localIntent = new Intent();
        localIntent.setAction(App.BROADCAST_ACTION);
        localIntent.putExtra("status", returnValue);
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }
}
