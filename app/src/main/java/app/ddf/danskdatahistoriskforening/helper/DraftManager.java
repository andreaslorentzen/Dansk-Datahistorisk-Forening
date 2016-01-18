package app.ddf.danskdatahistoriskforening.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import app.ddf.danskdatahistoriskforening.App;
import app.ddf.danskdatahistoriskforening.domain.Logic;

public class DraftManager {
    public void saveDraft(){
        new AsyncTask<Void, Void, Void> (){

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    FileOutputStream fos = App.getCurrentActivity().openFileOutput("draft", Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);

                    oos.writeObject(Logic.instance.editItem);
                    oos.flush();
                    oos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File file = new File(App.getCurrentActivity().getFilesDir().getPath() + "/" + "draft");

                Log.d("draft", "draft saved: " + file.exists());

                return null;
            }
        }.execute();
    }
    public void deleteDraft(){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void ... params) {
                File file = new File(App.getCurrentActivity().getFilesDir().getPath() + "/" + "draft");
                file.delete();

                return null;
            }
        }.execute();
    }
}
