package app.ddf.danskdatahistoriskforening.item;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.R;

public class ItemDescriptionFragment extends Fragment implements ItemUpdater, View.OnClickListener, SeekBar.OnSeekBarChangeListener{
    EditText itemDescription;

    //views
    ImageButton recButton;
    ImageButton playButton;
    ImageButton prevButton;
    ImageButton nextButton;

    TextView durText;
    TextView posText;
    TextView audioText;

    SeekBar seekBar;

    ArrayList<MediaPlayer> aps;

    Handler apHandler;
    ArrayList<Uri> audioUris;
    private MediaPlayer currentAP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_item_description, container, false);

        itemDescription = (EditText) layout.findViewById(R.id.itemDescription);

        Item item = ((ItemActivity) getActivity()).getItem();
        setItemDescription(item.getItemDescription());

        // AUDIO
        posText = (TextView) layout.findViewById(R.id.posText);
        durText = (TextView) layout.findViewById(R.id.durText);
        audioText = (TextView) layout.findViewById(R.id.audioText);

        recButton = (ImageButton) layout.findViewById(R.id.recButton);
        playButton = (ImageButton) layout.findViewById(R.id.playButton);
        nextButton = (ImageButton) layout.findViewById(R.id.nextButton);
        prevButton = (ImageButton) layout.findViewById(R.id.prevButton);

        seekBar = (SeekBar) layout.findViewById(R.id.seekBar);

        recButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(this);

        apHandler = new Handler();
        if(savedInstanceState == null){
            resetAudioPlayer(); // sets mPlayer
        }

        return layout;
    }

    public String getItemDescription(){
        if(itemDescription == null){
            return "";
        }
        return itemDescription.getText().toString();
    }

    public void setItemDescription(String description) {
        this.itemDescription.setText(description);
    }

    @Override
    public void updateItem(Item item) {
        item.setItemDescription(itemDescription.getText().toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        resetAudioPlayer();
        updateItem(((ItemActivity) getActivity()).getItem());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        forcestopAudioPlayer();
        destroyAudioPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        forcestopAudioPlayer();
        destroyAudioPlayer();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            seekTo(progress);
            posText.setText(millisToPlayback(getAPSCurrentPosition()));
        }
    }



    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onClick(View v) {
        if (v == recButton) {
            Intent i = new Intent(getActivity(), RecordingActivity.class);
            startActivity(i);
        } else if(v == playButton){
            if (currentAP != null) { // enabled first when a recording has been made
                if (currentAP.isPlaying()) {
                    pauseAudioPlayer();
                } else {
                    startAudioPlayer();
                }
            }
        }
    }

    Runnable apRunnable = new Runnable() {
        @Override
        public void run() {
            if (!currentAP.isPlaying()) {
                seekBar.setProgress(0);
                posText.setText("0:00.00");
                forcestopAudioPlayer();
                return;
            }
            seekBar.setProgress(getAPSCurrentPosition());
            posText.setText(millisToPlayback(getAPSCurrentPosition()));
            apHandler.postDelayed(this, 250);
        }
    };

    private void destroyAudioPlayer() {
        if (currentAP == null)
            return;
        currentAP.stop();
        currentAP.release();
        currentAP = null;
    }

    private void forcestopAudioPlayer() {
        apHandler.removeCallbacks(apRunnable);
        playButton.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        audioText.setText("Paused");
        if (currentAP == null)
            return;
        if (currentAP.isPlaying())
            currentAP.stop();
    }

    private void pauseAudioPlayer() {
        currentAP.pause();
        audioText.setText("Paused");
        apHandler.removeCallbacks(apRunnable);
        playButton.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);

    }
    private void startAudioPlayer() {
        // disable buttons
        playButton.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        currentAP.start();
        apRunnable.run();
        audioText.setText("Playing");
    }

    private void seekTo(int progress) {
        for (MediaPlayer mr : aps) {
            if (mr.getDuration() <= progress) {
                mr.seekTo(progress);
                currentAP = mr;
                return;
            } else {
                progress-=mr.getDuration();
            }



        }
    }

    private int getAPSCurrentPosition() {
        int totalDuration = 0;
        for (MediaPlayer ap: aps) {
            totalDuration += ap.getDuration();
            if (ap == currentAP) {
                totalDuration += currentAP.getCurrentPosition();
                return totalDuration;
            }
        }
        return totalDuration;
    }

    private int getAPSDuration() {
        int duration = 0;
        for (MediaPlayer ap: aps) {
            duration += ap.getDuration();
        }
        return duration;
    }

    private void resetAudioPlayer() {
        if (aps != null) {
            for (MediaPlayer mp : aps) {
                mp.release();
                mp = null;
            }
            aps.clear();
        }
        seekBar.setProgress(0);
        posText.setText("0:00.00");
        durText.setText("0:00.00");
        if (audioUris != null)
            for (Uri uri : audioUris) {
                File audioFile = new File(uri.getPath());
                if (audioFile.exists()) {
                    aps.add(MediaPlayer.create(getActivity(), Uri.parse(uri.getPath())));
                }
            }
        if (aps.isEmpty())
            audioText.setText("No audio files");
        else {
            currentAP = aps.get(0);
            audioText.setText(aps.size() + " audio files");
        }
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
    private void setAudioText(TextView vt, int millis) {
        String posString = RecordingActivity.millisToPlayback(millis);
        vt.setText(posString.substring(0, posString.length() - 3));
    }
    */
}
