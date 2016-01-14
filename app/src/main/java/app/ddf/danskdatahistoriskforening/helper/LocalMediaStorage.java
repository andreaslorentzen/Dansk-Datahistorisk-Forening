package app.ddf.danskdatahistoriskforening.helper;

//http://developer.android.com/guide/topics/media/camera.html#saving-media

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.main.MainActivity;

public class LocalMediaStorage {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_AUDIO = 2;
    public static final int MEDIA_TYPE_AUDIO_TEMP = 3;
    public static final int MEDIA_TYPE_AUDIO_NEW = 4;
    private static Context context;

    /** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri(String filename, int type){
        File file = getOutputMediaFile(filename, type);

        return Uri.fromFile(file);
    }

    public static File getOutputMediaFile(String filename, int type){
        File folder = getOutputMediaFolder();

        if(folder == null){
            return null;
        }

        File file = getOutputMediaFile(filename, type, folder);

        if(file == null){
            return null;
        }
        return file;
    }

    public static Uri getOutputMediaFileUri(int type){
        File file = getOutputMediaFile(type);

        return Uri.fromFile(file);
    }

    public static File getOutputMediaFile(int type){
        File folder = getOutputMediaFolder();

        if(folder == null){
            return null;
        }

        File file = getOutputMediaFile(type, folder);

        if(file == null){
            return null;
        }
        return file;
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFolder(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d("DDF", "SD card not mounted");
            return null;
        }

        File mediaStorageDir = Model.getCurrentActivity().getExternalFilesDir(null);
        if(mediaStorageDir == null){
            return null;
        }
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("DDF", "Failed to create folder"+mediaStorageDir.getPath());
                return null;
            }
        }
        return mediaStorageDir;
    }

    private static File getOutputMediaFile(String filename, int type, File mediaStorageDir){
        // Create a media file name
        String timeStamp = ""+(new Date().getTime());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    filename);
        } else if(type == MEDIA_TYPE_AUDIO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    filename);
        } else if(type == MEDIA_TYPE_AUDIO_TEMP) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    filename);
        } else if(type == MEDIA_TYPE_AUDIO_NEW) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    filename);
        } else {
            return null;
        }

        return mediaFile;
    }

    private static File getOutputMediaFile(int type, File mediaStorageDir){
        // Create a media file name
        String timeStamp = ""+(new Date().getTime());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
            Log.d("DDF",mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_AUDIO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ "timeStamp" + ".mp4");
        } else if(type == MEDIA_TYPE_AUDIO_TEMP) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ "timeStamp_temp" + ".mp4");
        } else if(type == MEDIA_TYPE_AUDIO_NEW) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ "timeStamp_new" + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    public static void setContext(MainActivity mainActivity) {
        context = mainActivity;
    }
}

