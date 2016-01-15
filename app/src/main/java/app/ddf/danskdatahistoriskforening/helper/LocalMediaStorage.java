package app.ddf.danskdatahistoriskforening.helper;

//http://developer.android.com/guide/topics/media/camera.html#saving-media

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.ddf.danskdatahistoriskforening.main.MainActivity;

public class LocalMediaStorage {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_AUDIO = 2;
    public static final int MEDIA_TYPE_AUDIO_RECORD = 3;
    public static final int MEDIA_TYPE_AUDIO_RECORD_TEMP = 4;
    public static final int MEDIA_TYPE_AUDIO_RECORD_MERGED = 5;
    private static Context context;

    /** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri(String filename, int type){
        File file = getOutputMediaFile(filename, type);
        return Uri.fromFile(file);
    }

    public static File getOutputMediaFile(String filename, int type){
        File folder = getOutputMediaFolder();
        if(folder == null)
            return null;
        File file = getOutputMediaFile(filename, type, folder);
        if(file == null)
            return null;
        return file;
    }


    /** Create a File for saving an image or video */
    public static File getOutputMediaFolder(){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d("DDF", "SD card not mounted");
            return null;
        }
        File mediaStorageDir = new File(context.getExternalFilesDir(null).getPath());
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("DDF", "Failed to create folder"+mediaStorageDir.getPath());
                return null;
            }
        }
        return mediaStorageDir;
    }

    private static File getOutputMediaFile(String filename, int type, File mediaStorageDir){
        System.out.println(filename +" " + type);
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);
        else if(type == MEDIA_TYPE_AUDIO)
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);
        else if(type == MEDIA_TYPE_AUDIO_RECORD)
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "recorded.mp4");
        else if(type == MEDIA_TYPE_AUDIO_RECORD_TEMP)
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "recorded_temp.mp4");
        else if (type == MEDIA_TYPE_AUDIO_RECORD_MERGED)
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "recorded_merged.mp4");
        else
            return null;

        return mediaFile;
    }


    public static void setContext(MainActivity mainActivity) {
        context = mainActivity;
    }
}

