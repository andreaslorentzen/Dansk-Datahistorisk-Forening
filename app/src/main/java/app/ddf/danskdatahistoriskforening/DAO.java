package app.ddf.danskdatahistoriskforening;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DAO implements IDAO {
    private static final String API = "http://78.46.187.172:4019";

    public int saveItemToDB(Context context, Item item){
        if(item.getItemHeadline() == null || item.getItemHeadline().isEmpty()){
            return  1;
        }

        if(!isConnected(context)){
            return 2;
        }

        Log.d("fejl", item.getItemHeadline());
        (new HttpPostItem()).execute(item);

        return -1;
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

    public String getDataFromBackend(){
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new URL(API + "/items").openStream()));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    //create new entry in database
    private class HttpPostItem extends AsyncTask<Item, Void, String> {
        @Override
        protected String doInBackground(Item... params) {
            Log.d("fejl", "start post");
            Item item = params[0];
            Log.d("fejl", item.getItemHeadline());
            String requestUrl = API + "/items?itemheadline=" + item.getItemHeadline() + "&itemdescription=" +  item.getItemHeadline();
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
