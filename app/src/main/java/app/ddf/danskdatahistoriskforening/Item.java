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
    private static final String API = "http://78.46.187.172:4019";

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

    public boolean saveItemToDB(Context context){
        if(itemHeadline == null || itemHeadline.isEmpty()){
            Toast.makeText(context, "Der er ikke angivet en titel til museumsgenstanden!", Toast.LENGTH_LONG).show();
            return  false;
        }

        if(!isConnected(context)){
            Toast.makeText(context, "Enheden er ikke forbundet til internettet!", Toast.LENGTH_LONG).show();
            return false;
        }

        (new HttpPostItem()).execute();

        return true;
    }

    //http://developer.android.com/training/basics/network-ops/connecting.html#connection
    private boolean isConnected(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    //http://developer.android.com/training/basics/network-ops/connecting.html#download

    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    //create new entry in database
    private class HttpPostItem extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... params) {
            String requestUrl = API + "/items?itemheadline=" + itemHeadline;
            //TODO build entire request from variables with stringbuilder

            InputStream is = null;
            String response = "";

            try {
                URL url = new URL(requestUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                // Starts the query
                conn.connect();
                int responseCode = conn.getResponseCode();
                Log.d("DDF", "The response is: " + responseCode + conn.getResponseMessage());
                is = conn.getInputStream();

                // Convert the InputStream into a string
                response = readIt(is, 2000);
            }
            catch (Exception e){
                //TODO do something useful maybe
                response = e.getMessage();
                Log.d("DDF", response);
            }
            finally {
                if(is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response){
            System.out.println(response);

            //TODO handle failure

            //TODO extract itemid for updates

        }
    }
}
