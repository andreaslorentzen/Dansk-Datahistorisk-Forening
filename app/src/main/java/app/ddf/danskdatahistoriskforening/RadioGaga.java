package app.ddf.danskdatahistoriskforening;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.Console;
import java.io.IOException;

public class RadioGaga {
    boolean isRecording;
    private MediaRecorder mRecorder;
    private String mFileName = "TEST FOR NOW";

    public void execute() {
        if (!isRecording)
            startRecord();
        else
            stopRecord();
    }

    private void startRecord() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        System.out.println("START RECORDING FILE: mFileName");
        try {
            mRecorder.prepare();
        } catch (IOException e) {
        }
        mRecorder.start();
    }

    private void stopRecord() {
        if(mRecorder==null)
            return;
        System.out.println("STOP RECORDING");
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public void requestStop() {
        if (isRecording)
            stopRecord();
    }
}
