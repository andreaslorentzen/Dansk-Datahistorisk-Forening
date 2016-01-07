package app.ddf.danskdatahistoriskforening;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;

public class RecordingActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton pauseButton;
    TextView time_text;
    VoiceRecorder vr;
    Handler timerHandler = new Handler();
    long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        timerHandler = new Handler();
        vr = new VoiceRecorder();
        pauseButton = (ImageButton) this.findViewById(R.id.voice_pause_button);
        time_text = (TextView) this.findViewById(R.id.time_text);
        pauseButton.setOnClickListener(this);
        /*
        // IGNORE CODE BELOW - unnecessary/irrelevant feedback to the user
        File audioFile = new File(LocalMediaStorage.getOutputMediaFileUri(2).getPath());
        if (audioFile.exists()){
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, Uri.fromFile(audioFile));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillisec = -Long.parseLong(time);
            startTime = System.currentTimeMillis() + timeInMillisec;
        } else*/
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        vr.execute();
    }

    @Override
    public void onClick(View v) {
        if(v == pauseButton){
            System.out.println("STOP");
            vr.execute();
            timerHandler.removeCallbacks(timerRunnable);
            //running = false;
            finish();
        }
    }

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            int time = (int) (System.currentTimeMillis() - startTime);
            int hours = time/3600000;
            time -= hours * 3600000;
            int minuts = time/60000;
            time -= minuts * 60000;
            int seconds = time/1000;
            time -= seconds * 1000;
            int millis = time/1;
            time_text.setText(hours +":"+  String.format("%02d", minuts)+":"+  String.format("%02d", seconds) +";"+String.format("%02d", millis).substring(0,2)    );
            timerHandler.postDelayed(this, 50);
        }
    };
}
