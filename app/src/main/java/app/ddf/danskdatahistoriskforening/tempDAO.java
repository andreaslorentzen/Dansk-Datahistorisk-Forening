package app.ddf.danskdatahistoriskforening;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by mathias on 30/12/15.
 */
public class tempDAO implements IDAO {
    private static final String API = "http://msondrup.dk/api/v1";
    private static final String userIDString = "?userID=56837dedd2d76438906140";

    @Override
    public int saveItemToDB(Context context, Item item) {
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

    private boolean isConnected(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public String getOverviewFromBackend() {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new URL(API + "/items" + userIDString).openStream()));
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

    @Override
    public Item getDetailsFromBackEnd(String detailsURI) {
        BufferedReader br;
        StringBuilder sb;
        try {
            br = new BufferedReader(new InputStreamReader(new URL(API + detailsURI + userIDString).openStream()));
            sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
            }
            sb.toString();
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
        String data = sb.toString();

        System.out.println("data = " + data);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject item;

        try {
            data = data.substring(1, data.length()-1);
            item = new JSONObject(data);
            Item currentItem = new Item(
                    Integer.parseInt(item.getString("itemid")),
                    item.getString("itemheadline"),
                    item.getString("itemdescription"),
                    ((item.getString("itemreceived") == null || item.getString("itemreceived").equals("")) ? null : formatter.parse(item.getString("itemreceived"))),
                    ((item.getString("datingfrom") == null || item.getString("datingfrom").equals("")) ? null : formatter.parse(item.getString("datingfrom"))),
                    ((item.getString("datingto") == null || item.getString("datingto").equals("")) ? null : formatter.parse(item.getString("datingto"))),
                    item.getString("donator"),
                    item.getString("producer"),
                    item.getString("postnummer")
            );
            return currentItem;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int updateItem(Context context, Item item) {
        if (item.getItemId()== 0){
            return 1;
        }
        if(!isConnected(context)){
            return 2;
        }
        (new HttpUpdateItem()).execute(item);
        return -1;
    }

    private class HttpUpdateItem extends AsyncTask<Item, Void, String> {

        @Override
        protected String doInBackground(Item... params) {
            Item item = params[0];

            String requestURL = API + "/items/" + item.getItemId() + userIDString;

            InputStream is = null;
            String response = "";

            try{
                URL url = new URL(requestURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoInput(true);

                String requestBody = item.toJSON().toString();
                byte[] outputInBytes = requestBody.getBytes();
                OutputStream os = conn.getOutputStream();
                os.write(outputInBytes);
                os.close();

                int responseCode = conn.getResponseCode();
                // TODO handle response code
                Log.d("DDF", "The response is: " + responseCode + conn.getResponseMessage());

                is = conn.getInputStream();

                response = readIt(is, 2000);

            } catch (Exception e) {
                //TODO do something useful
                response = e.getMessage();
                Log.d("DDF", response);
            } finally {
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
        protected void onPostExecute(String result){

        }
    }

    private class HttpPostItem extends AsyncTask<Item, Void, String> {
        @Override
        protected String doInBackground(Item... params) {
            Log.d("fejl", "start post");
            Item item = params[0];
            Log.d("fejl", item.getItemHeadline());
            String requestUrl = API + "/items" + userIDString;
            //TODO build entire request from variables with stringbuilder

            InputStream is = null;
            String response = "";

            try {
                URL url = new URL(requestUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoInput(true);

                // Starts the query
                String requestBody = item.toJSON().toString();
                byte[] outputInBytes = requestBody.getBytes();
                OutputStream os = conn.getOutputStream();
                os.write(outputInBytes);
                os.close();


                int responseCode = conn.getResponseCode();
                //TODO handle response code
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

    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
