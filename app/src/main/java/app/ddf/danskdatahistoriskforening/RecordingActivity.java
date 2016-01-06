package app.ddf.danskdatahistoriskforening;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class RecordingActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton pauseButton;
    TextView time_text;
    VoiceRecorder vr;
    Handler timerHandler = new Handler();
    boolean running;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        pauseButton = (ImageButton) this.findViewById(R.id.voice_pause_button);
        time_text = (TextView) this.findViewById(R.id.time_text);
        System.out.println(pauseButton);
        pauseButton.setOnClickListener(this);
        running = true;
        timerHandler.postDelayed(timerRunnable, 0);
        //timer();
    }

    @Override
    public void onClick(View v) {
        if(v == pauseButton){
            System.out.println("STOP");
            timerHandler.removeCallbacks(timerRunnable);
            running = false;
            finish();
        }
    }

    Runnable timerRunnable = new Runnable() {
        final long startTime = System.currentTimeMillis();
        @Override
        public void run() {
            int time = (int) (System.currentTimeMillis() - startTime); // elapsed time from nano to millis
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
/*
    private void timer() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... executeParametre) {
                while (running) {
                    publishProgress();
                }
                return "færdig!";  // <5>
            }
            @Override
            protected void onProgressUpdate(Object... progress) {
                // 1000 millis
                // 60 seconds
                // 60 minuts
                int time = (int) (System.currentTimeMillis() - startTime); // elapsed time from nano to millis
                int hours = time/3600000;
                time -= hours * 3600000;
                int minuts = time/60000;
                time -= minuts * 60000;
                int seconds = time/1000;
                time -= seconds * 1000;
                int millis = time/1;
                //onPostExecute((hours +":"+  String.format("%02d", minuts)+":"+  String.format("%02d", seconds) +":"+String.format("%02d", millis).substring(0,2)    ));
                time_text.setText(hours +":"+  String.format("%02d", minuts)+":"+  String.format("%02d", seconds) +";"+String.format("%02d", millis).substring(0,2)    );
            }
        }.execute(100);
    }*/
}
