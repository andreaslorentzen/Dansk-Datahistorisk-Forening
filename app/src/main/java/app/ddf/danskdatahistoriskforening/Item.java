package app.ddf.danskdatahistoriskforening;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class Item {
    private int itemId;

    public String itemHeadline;
    public String itemDescription;
    public Date itemRecieved;
    public Date itemDatingFrom;
    public Date getItemDatingTo;
    public String donator;
    public String producer;
    public String postalCode;

    public Item(){}


}
