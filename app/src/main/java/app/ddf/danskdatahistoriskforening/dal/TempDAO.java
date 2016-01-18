package app.ddf.danskdatahistoriskforening.dal;

import android.content.Context;
import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;

import app.ddf.danskdatahistoriskforening.App;
import app.ddf.danskdatahistoriskforening.helper.LocalMediaStorage;

/**
 * Created by mathias on 30/12/15.
 */
public class TempDAO implements IDAO {
    private static final String API = "http://msondrup.dk/api/v1";
    private static final String userIDString = "?userID=56837dedd2d76438906140";

    @Override
    public int saveItemToDB(Context context, Item item) {
        InputStream is = null;
        String requestURL = API + "/items" + userIDString;
        int returnValue = 0;

        try{
            //setup for the query
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);

            // start the query
            String requestBody = item.toJSON().toString();
            byte[] outputInBytes = requestBody.getBytes();
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();

            int responseCode = conn.getResponseCode();


            if(responseCode == 201){
                returnValue = -1;
            } else{
                returnValue = 3;
            }

            is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
            }
            JSONObject createdItem = new JSONObject(sb.toString());
            is.close();
            int itemID = createdItem.getInt("itemid");

            if(item.getAddedPictures() != null) {
                for (Uri picture : item.getAddedPictures()) {
                    postFile(context, picture, itemID, "jpg");
                }
            }
            if(item.getAddedRecordings() != null) {
                for (Uri recording : item.getAddedRecordings()) {
                    postFile(context, recording, itemID, "mp4");
                }
            }
        } catch(MalformedURLException |ProtocolException e){
            // SHOULD NEVER HAPPEN IN PRODUCTION
            e.printStackTrace();
        } catch(IOException e){
            returnValue = 4;
        } catch(JSONException e){
            returnValue = 5;
        }finally{
            if (is != null){
                try{
                    is.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return returnValue;
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
        canceled = false;
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

        JSONObject item;

        try {
            item = new JSONObject(data);

            String itemreceived = item.getString("itemreceived");
            String itemdatingfrom = item.getString("itemdatingfrom");
            String itemdatingto = item.getString("itemdatingto");
            String donator = item.getString("donator");
            String producer = item.getString("producer");
            String postnummer = item.getString("postnummer");

            Item currentItem = new Item(
                    Integer.parseInt(item.getString("itemid")),
                    item.getString("itemheadline"),
                    item.getString("itemdescription"),
                    (isJsonNull(itemreceived) || itemreceived.equals("0000-00-00")) ? null : App.getFormatter().parse(itemreceived),
                    (isJsonNull(itemdatingfrom) || itemdatingfrom.equals("0000-00-00")) ? null : App.getFormatter().parse(itemdatingfrom),
                    (isJsonNull(itemdatingto) || itemdatingto.equals("0000-00-00")) ? null : App.getFormatter().parse(itemdatingto),
                    isJsonNull(donator) ? null : donator,
                    isJsonNull(producer) ? null : producer,
                    isJsonNull(postnummer) ? null : postnummer
            );

            JSONObject images = item.optJSONObject("images");
            if (images != null){
                JSONObject image;
                int i = 0;
                while((image = images.optJSONObject("image_" + i)) != null){
                    if(canceled) {
                        return null;
                    }
                    Uri temp = getFile(image.getString("href"), LocalMediaStorage.MEDIA_TYPE_IMAGE);
                    if(temp == null)
                        continue;;
                    currentItem.addToPictures(temp);
                    i++;
                }
            }

            JSONObject audios = item.optJSONObject("audios");
            if (audios != null){
                JSONObject audio;
                int i = 0;
                while((audio = audios.optJSONObject("audio_" + i)) != null){
                    if(canceled) {
                        return null;
                    }
                    Uri temp = getFile(audio.getString("href"), LocalMediaStorage.MEDIA_TYPE_AUDIO);
                    if(temp == null)
                        continue;
                    currentItem.addToRecordings(temp);
                    i++;
                }
            }
            return currentItem;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e){
            e.printStackTrace();
            return null;
        }
    }

    private boolean isJsonNull(String string){
        return string == null || string.equals("null") || string.equals("");
    }

    @Override
    public int updateItem(Context context, Item item) {
        String requestURL = API + "/items/" + item.getItemId() + userIDString;

        InputStream is = null;
        int returnValue = 0;

        try{
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            String requestBody = item.toJSON().toString();
            byte[] outputInBytes = requestBody.getBytes();
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();

            int responseCode = conn.getResponseCode();

            if(responseCode == 200){
                returnValue = -1;
            } else{
                returnValue = 3;
            }

            if(item.getDeletedPictures() != null){
                for(Uri picture : item.getDeletedPictures()){
                    deleteFile(picture, item.getItemId());
                }
            }

            if(item.getAddedPictures() != null){
                for(Uri picture : item.getAddedPictures()){
                    postFile(context, picture, item.getItemId(), "jpg");
                }
            }

            if(item.isRecordingsChanged()){
                for(Uri recording : item.getRecordings()){
                    deleteFile(recording, item.getItemId());
                    postFile(context, recording, item.getItemId(), "mp4");
                }
            }

        } catch(MalformedURLException |ProtocolException e){
            // SHOULD NEVER HAPPEN IN PRODUCTION
            e.printStackTrace();
        } catch(IOException e){
            returnValue = 4;
        }  finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnValue;
    }

    public void deleteFile(Uri file, int itemID){
        String requestURL = API + "/items/" + itemID + "/" + file.getLastPathSegment() + userIDString;
        HttpURLConnection conn = null;
        try{
            //setup for the query
            URL url = new URL(requestURL);
            conn = (HttpURLConnection) url.openConnection();
      //      conn.setConnectTimeout(15000);
            conn.setRequestMethod("DELETE");

            // start the query
            conn.getResponseCode();

        } catch(MalformedURLException |ProtocolException e){
            // SHOULD NEVER HAPPEN IN PRODUCTION
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    public void postFile(Context context, Uri path, int itemID, String extension){
        InputStream inputStream;
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        ByteArrayOutputStream byteBuffer = null;

        try{
            inputStream = context.getContentResolver().openInputStream(path);

            byteBuffer = new ByteArrayOutputStream();

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        String requestURL = API + "/items/" + itemID + userIDString;

        try{
            //setup for the query
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            if(extension.equals("jpg")){
                conn.setRequestProperty("Content-Type", "image/jpg");
            } else if(extension.equals("mp4")){
                conn.setRequestProperty("Content-Type", "audio/mp4");
            }
            conn.setDoInput(true);

            // start the query
            OutputStream os = conn.getOutputStream();
            os.write(byteBuffer.toByteArray());
            os.close();
            System.out.println(conn.getResponseCode());

        } catch(MalformedURLException |ProtocolException e){
            // SHOULD NEVER HAPPEN IN PRODUCTION
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    private boolean canceled;

    @Override
    public void cancelDownload() {
        canceled = true;

    }

    public Uri getFile(String filePath, int type){

        File fileToSave = null;
        try{
            URL url = new URL(filePath);
            URLConnection conn = url.openConnection();

            InputStream is = conn.getInputStream();
            BufferedInputStream input = new BufferedInputStream(is);

            fileToSave = LocalMediaStorage.getOutputMediaFile(filePath.substring(filePath.lastIndexOf("/")), type);

            if(fileToSave == null)
                return null;

            OutputStream output = new FileOutputStream(fileToSave);


            byte[] data = new byte[1024];
            int count;
            while ((count = input.read(data)) != -1) {
                if(canceled) {
                    return null;
                }
                output.write(data, 0, count);
            }

            output.flush();
            input.close();
            output.close();

        } catch(IOException e){
            e.printStackTrace();
        }
        if(fileToSave == null){
            return null;
        }else{
            return Uri.fromFile(fileToSave);
        }
    }
}