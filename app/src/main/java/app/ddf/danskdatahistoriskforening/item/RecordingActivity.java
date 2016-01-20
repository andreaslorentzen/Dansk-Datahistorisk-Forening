package app.ddf.danskdatahistoriskforening.item;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
        //resetAudioPlayer();

        arHandler = new Handler();
        apHandler = new Handler();
        ar = new AudioRecorder();
        startRecording();
    }

    long buttonCooldown;

    @Override
    public void onClick(View v) {
        if (v == cancelButton) {
            cancelRecording();
        } else if (v == doneButton) {
            finishRecording();
        } else if (v == recButton) {
            long currentTime = System.nanoTime();
            if (currentTime - buttonCooldown > 200000000) { // hack fix 400 milliseconds cooldown between button clicks
                if (ar.isRecording()) {
                    stopRecording();
                } else {
                    File folder = LocalMediaStorage.getOutputMediaFolder();
                    if (folder == null) {
                        Toast.makeText(this, "Der opstod en fejl ved lydoptagelse, sørg for at SD kortet er tilgængeligt og prøv igen.", Toast.LENGTH_LONG).show();
                    } else {
                        startRecording();
                    }
                }
                buttonCooldown = currentTime;
            }
        } else if (v == trashButton) {
            trashRecording();
        } else if (v == playButton) {
            if (ap != null) { // enabled first when a recording has been made
                if (ap.isPlaying()) {
                    pauseAudioPlayer();
                } else {
                    startAudioPlayer();
                }
            }
        }
    }

    private void finishRecording() {
        destroyAudioPlayer();
        File folder = LocalMediaStorage.getOutputMediaFolder();
        if (folder == null) {
            Toast.makeText(this, "Der opstod en fejl da lydoptagelsen skulle gemmes, intet SD kort blev fundet, optagelsen blev ikke gemt.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Intent result = new Intent();
            String fileName = "recording_" + System.nanoTime() + ".mp4";
            File file = new File(LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD).getPath());
            file.renameTo(new File(LocalMediaStorage.getOutputMediaFileUri(fileName, LocalMediaStorage.MEDIA_TYPE_AUDIO).getPath()));
            result.putExtra("recordingUri", LocalMediaStorage.getOutputMediaFileUri(fileName, LocalMediaStorage.MEDIA_TYPE_AUDIO));
            setResult(Activity.RESULT_OK, result);
            finish();
        }


    }

    private void cancelRecording() {
        File folder = LocalMediaStorage.getOutputMediaFolder();
        if (folder == null) {
            destroyAudioPlayer();
            finish();
        } else {
            File recordedFile = new File(LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD).getPath());
            if (recordedFile.exists()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Annulere optagelse");
                builder.setMessage("Vil slette optagelsen?");
                builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File recordedFile = new File(LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD).getPath());
                        recordedFile.delete();
                        destroyAudioPlayer();
                        finish();
                    }
                });
                builder.setNegativeButton("NEJ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
            } else {
                destroyAudioPlayer();
                finish();
            }
        }
    }


    private void trashRecording() {
        File folder = LocalMediaStorage.getOutputMediaFolder();
        if (folder == null) {
            Toast.makeText(this, "Der opstod en fejl da lydoptagelsen skulle slettes, intet SD kort blev fundet.", Toast.LENGTH_LONG).show();
        } else {
            File recordedFile = new File(LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD).getPath());
            if (recordedFile.exists()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Slet optagelse");
                builder.setMessage("Vil slette optagelsen?");
                builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File recordedFile = new File(LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD).getPath());
                        recordedFile.delete();
                        recText.setText("0:00.00");
                        resetAudioPlayer();
                    }
                });
                builder.setNegativeButton("NEJ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        }


    }

    private void setEnabledTrash(boolean active) {
        File recordedFile = new File(LocalMediaStorage.getOutputMediaFileUri(null, 3).getPath());
        if (recordedFile.exists() && active) {
            trashButton.setEnabled(true);
            trashButton.setAlpha(1.0f);
        } else {
            trashButton.setEnabled(false);
            trashButton.setAlpha(0.35f);
        }
    }

    private void stopRecording() {
        try {
            if (!ar.isRecording())
                return;
            if (!ar.stopRecording())
                return;
            recButton.setImageResource(R.drawable.ic_mic_white_48dp);
            arHandler.removeCallbacks(arRunnable);
            resetAudioPlayer();
            // enable buttons
            setEnableScreen(true);
            setEnableAP(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startRecording() {
        try {
            // disable buttons
            setEnableScreen(false);
            setEnableAP(false);
            ar.startRecording();
            startTime = System.currentTimeMillis();
            recButton.setImageResource(R.drawable.ic_pause);
            arHandler.postDelayed(arRunnable, 0);
            audioText.setText("Recording");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setEnableScreen(boolean active) {
        setEnabledTrash(active);
        cancelButton.setEnabled(active);
        doneButton.setEnabled(active);
        if (active) {
            cancelButton.setAlpha(1f);
            doneButton.setAlpha(1f);
        } else {
            cancelButton.setAlpha(0.35f);
            doneButton.setAlpha(0.35f);
        }
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
        stopRecording();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (ar.isRecording())
            stopRecording();
        else if (ap != null && ap.isPlaying())
            forcestopAudioPlayer();
        else
            cancelRecording();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        forcestopAudioPlayer();
        destroyAudioPlayer();
        stopRecording();
    }

    @Override
    public void onStop() {
        super.onStop();
        forcestopAudioPlayer();
        destroyAudioPlayer();
        stopRecording();
    }


    /**
     * MEDIA PLAYER START
     */

    Runnable apRunnable = new Runnable() {
        @Override
        public void run() {
            if (!ap.isPlaying()) {
                seekBar.setProgress(0);
                posText.setText("0:00.00");
                recButton.setAlpha(1.0f);
                recText.setAlpha(1.0f);
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
        setEnableRec(true);
        audioText.setText("Paused");
        if (ap == null)
            return;
        if (ap.isPlaying())
            ap.pause();
    }


    private void setEnableRec(boolean active) {
        recButton.setEnabled(active);
        if (active) {
            recButton.setAlpha(1.0f);
            recText.setAlpha(1.0f);
        } else {
            recButton.setAlpha(0.35f);
            recText.setAlpha(0.35f);
        }
    }

    // PAUSE does NOT reset seek/pos
    private void pauseAudioPlayer() {
        ap.pause();
        audioText.setText("Paused");

        apHandler.removeCallbacks(apRunnable);
        playButton.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        setEnableScreen(true);
        setEnableRec(true);
    }

    private void startAudioPlayer() {
        // disable buttons
        setEnableScreen(false);
        setEnableRec(false);

        playButton.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        ap.start();
        apRunnable.run();
        audioText.setText("Playing");
    }

    private void setEnableAP(boolean active) {
        seekBar.setEnabled(active);
        playButton.setEnabled(active);
        if (active) {
            playButton.setAlpha(1f);
        } else {
            playButton.setAlpha(0.35f);
        }
    }

    private void resetAudioPlayer() {

        MediaPlayer old = ap;

        String filePath = LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD).getPath();
        if (new File(filePath).exists()) {
            audioText.setText("Paused");
            ap = new MediaPlayer();
            setEnabledTrash(true);
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
        setEnabledTrash(false);
        seekBar.setProgress(0);
        posText.setText("0:00.00");
        durText.setText("0:00.00");
        if (ar.isRecording())
            audioText.setText("Recording");
        else
            audioText.setText("Nothing recorded");
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
     * MEDIA PLAYER STOP
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
        int hours = time / 3600000;
        time -= hours * 3600000;
        int minuts = time / 60000;
        time -= minuts * 60000;
        int seconds = time / 1000;

        return hours + ":" + String.format("%02d", minuts) + "." + String.format("%02d", seconds);
    }
}
