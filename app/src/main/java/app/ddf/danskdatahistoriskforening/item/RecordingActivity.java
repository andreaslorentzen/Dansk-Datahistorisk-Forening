package app.ddf.danskdatahistoriskforening.item;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.helper.LocalMediaStorage;
import app.ddf.danskdatahistoriskforening.helper.AudioRecorder;

public class RecordingActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    ImageButton cancelButton;
    ImageButton doneButton;
    ImageButton recButton;
    ImageButton trashButton;
    ImageButton playButton;

    TextView recText;
    TextView posText;
    TextView audioText;
    TextView durText;

    SeekBar seekBar;

    AudioRecorder ar;
    Handler arHandler;
    Handler apHandler;

    long startTime;
    private MediaPlayer ap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        cancelButton = (ImageButton) this.findViewById(R.id.cancelButton);
        doneButton = (ImageButton) this.findViewById(R.id.doneButton);
        recButton = (ImageButton) this.findViewById(R.id.recButton);
        trashButton = (ImageButton) this.findViewById(R.id.trashButton);
        playButton = (ImageButton) this.findViewById(R.id.playButton);

        cancelButton.setOnClickListener(this);
        doneButton.setOnClickListener(this);
        recButton.setOnClickListener(this);
        trashButton.setOnClickListener(this);
        playButton.setOnClickListener(this);

        recText = (TextView) this.findViewById(R.id.recText);
        posText = (TextView) this.findViewById(R.id.posText);
        audioText = (TextView) this.findViewById(R.id.audioText);
        durText = (TextView) this.findViewById(R.id.durText);

        seekBar = (SeekBar) this.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        File recordedFile = new File(LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD).getPath());
        if (recordedFile.exists()) {
            recordedFile.delete();
        }
        resetAudioPlayer();

        arHandler = new Handler();
        apHandler = new Handler();
        ar = new AudioRecorder();
    }

    @Override
    public void onClick(View v) {
        if(v == cancelButton){
            File recordedFile = new File(LocalMediaStorage.getOutputMediaFileUri(null, 3).getPath());
            if (recordedFile.exists())
                recordedFile.delete();
            destroyAudioPlayer();
            finish();
        } else  if(v == doneButton){
            destroyAudioPlayer();
            Intent result = new Intent();
            String fileName = "recording_" + System.nanoTime() +".mp4";
            System.out.println(fileName);
            File file = new File(LocalMediaStorage.getOutputMediaFileUri(null, 3).getPath());
            System.out.println(file);
            file.renameTo(new File(LocalMediaStorage.getOutputMediaFileUri(fileName, 2).getPath()));
            System.out.println(file);
            System.out.println(LocalMediaStorage.getOutputMediaFileUri(fileName, 2));
            result.putExtra("recordingUri", LocalMediaStorage.getOutputMediaFileUri(fileName, 2));
            System.out.println(result);
            setResult(Activity.RESULT_OK, result);
            finish();
        } else  if(v == recButton){
            if (ar.isRecording()) {
                try {
                    ar.stopRecording();
                    recButton.setBackgroundResource(R.drawable.ic_fiber_manual_record_black_48dp);
                    arHandler.removeCallbacks(arRunnable);
                    audioText.setText("Pausing");
                    recText.setText(millisToPlayback(getAudioDuration()));
                    durText.setText(millisToPlayback(getAudioDuration()));
                    resetAudioPlayer();

                    // enable buttons
                    setEnableScreen(true);
                    setEnableAP(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    // disable buttons
                    setEnableScreen(false);
                    setEnableAP(false);

                    ar.startRecording();
                    startTime = System.currentTimeMillis();
                    recButton.setBackgroundResource(R.drawable.ic_pause_circle_filled_white_48dp);
                    arHandler.postDelayed(arRunnable, 0);
                    audioText.setText("Recording");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else  if(v == trashButton){
            File recordedFile = new File(LocalMediaStorage.getOutputMediaFileUri(null, 3).getPath());
            if (recordedFile.exists())
                recordedFile.delete();
            recText.setText("0:00.00");
            resetAudioPlayer();
        } else if(v == playButton){
            if (ap != null) { // enabled first when a recording has been made
                if (ap.isPlaying()) {
                    pauseAudioPlayer();
                } else {
                    startAudioPlayer();
                }
            }
        }
    }

    private void setEnableScreen(boolean active) {
        trashButton.setEnabled(active);
        cancelButton.setEnabled(active);
        doneButton.setEnabled(active);
    }

    Runnable arRunnable = new Runnable() {
        @Override
        public void run() {
            if (!ar.isRecording())
                return;
            int time = (int) (System.currentTimeMillis() - startTime) + getAudioDuration();
            recText.setText(millisToPlayback(time));
            durText.setText(millisToPlayback(time));
            arHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        resetAudioPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        forcestopAudioPlayer();
        destroyAudioPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        forcestopAudioPlayer();
        destroyAudioPlayer();
    }

    /**
     *
     * MEDIA PLAYER START
     *
     */

    Runnable apRunnable = new Runnable() {
        @Override
        public void run() {
            if (!ap.isPlaying()) {
                seekBar.setProgress(0);
                posText.setText("0:00.00");
                forcestopAudioPlayer();
                return;
            }
            seekBar.setProgress(ap.getCurrentPosition());
            posText.setText(millisToPlayback(ap.getCurrentPosition()));
            apHandler.postDelayed(this, 250);
        }
    };

    private void destroyAudioPlayer() {
        if (ap == null)
            return;
        ap.stop();
        ap.release();
        ap = null;
    }

    private void forcestopAudioPlayer() {
        apHandler.removeCallbacks(apRunnable);
        playButton.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        setEnableScreen(true);
        recButton.setEnabled(true);
        audioText.setText("Paused");
        if (ap == null)
            return;
        if (ap.isPlaying())
            ap.stop();
    }

    // PAUSE does NOT reset seek/pos
    private void pauseAudioPlayer() {
        ap.pause();
        audioText.setText("Paused");

        apHandler.removeCallbacks(apRunnable);
        playButton.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        setEnableScreen(true);
        recButton.setEnabled(true);
    }

    private void startAudioPlayer() {
        // disable buttons
        setEnableScreen(false);
        recButton.setEnabled(false);

        playButton.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        ap.start();
        apRunnable.run();
        audioText.setText("Playing");
    }

    private void setEnableAP(boolean active) {
        seekBar.setEnabled(active);
        playButton.setEnabled(active);
    }

    private void resetAudioPlayer() {
        MediaPlayer old = ap;
        ap = new MediaPlayer();
        String filePath = LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD).getPath();
        if (new File(filePath).exists()) {
            ap = MediaPlayer.create(this, Uri.parse(filePath));
            seekBar.setMax(ap.getDuration());
            durText.setText(millisToPlayback(ap.getDuration()));
            if (old == null) {
                seekBar.setProgress(0);
                posText.setText("0:00.00");
                return;
            }
            seekBar.setProgress(old.getDuration());
            posText.setText(millisToPlayback(ap.getCurrentPosition()));
            ap.seekTo(old.getDuration());
            old.release();
            return;
        }
        seekBar.setProgress(0);
        posText.setText("0:00.00");
        durText.setText("0:00.00");
        audioText.setText("Go ahead and record! :)");
        setEnableAP(false);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (ap != null && fromUser) {
            ap.seekTo(progress);
            posText.setText(millisToPlayback(ap.getCurrentPosition()));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    /**
     *
     * MEDIA PLAYER STOP
     *
     */

    public int getAudioDuration() {
        Uri uri = LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD);
        File recordFile = new File(uri.getPath());
        if (recordFile.exists()) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(this, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return Integer.parseInt(durationStr);
        }
        return 0;
    }

    public static String millisToPlayback(int time) {
        int hours = time/3600000;
        time -= hours * 3600000;
        int minuts = time/60000;
        time -= minuts * 60000;
        int seconds = time/1000;
        time -= seconds * 1000;

        return hours +":"+  String.format("%02d", minuts)+"."+  String.format("%02d", seconds);
    }

/*
    private void setAlpha(int  a) {
        List<View> mpChildren = getAllChildrenBFS(findViewById(R.id.mpLayout));
        for (View c : mpChildren) {
            Drawable d = c.getBackground();
            if (c!=null )
                c.setAlpha(a);
        }
    }
    private List<View> getAllChildrenBFS(View v) {
        List<View> visited = new ArrayList<View>();
        List<View> unvisited = new ArrayList<View>();
        unvisited.add(v);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            visited.add(child);
            if (!(child instanceof ViewGroup)) continue;
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i=0; i<childCount; i++) unvisited.add(group.getChildAt(i));
        }
        return visited;
    }
    */
}
