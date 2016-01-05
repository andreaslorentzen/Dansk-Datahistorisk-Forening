package app.ddf.danskdatahistoriskforening;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class RecordingActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton pauseButton;
    TextView time_text;
    VoiceRecorder vr;

    boolean running;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        pauseButton = (ImageButton) this.findViewById(R.id.voice_pause_button);
        time_text = (TextView) this.findViewById(R.id.time_text);
        System.out.println(pauseButton);
        pauseButton.setOnClickListener(this);
        //vr = new VoiceRecorder();
        //vr.execute();
        running = true;
        timer();
    }

    @Override
    public void onClick(View v) {
        if(v == pauseButton){
            System.out.println("ASASD");
            running = false;
            finish();
        }
    }


    private void timer() {
        final long startTime = System.currentTimeMillis();
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
                System.out.println("onProgressUpdate");
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

                time_text.setText(hours +":"+  String.format("%02d", minuts)+":"+  String.format("%02d", seconds) +":"+String.format("%03d", millis)    );
            }

            @Override
            protected void onPostExecute(Object result) {
                System.out.println("onPostExecute");
            }
        }.execute(100);
    }
}
