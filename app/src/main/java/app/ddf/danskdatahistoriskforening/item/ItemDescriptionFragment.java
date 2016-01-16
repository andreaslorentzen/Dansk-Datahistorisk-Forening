package app.ddf.danskdatahistoriskforening.item;

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
import java.util.List;

import app.ddf.danskdatahistoriskforening.App;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.dal.Item;

public class ItemDescriptionFragment extends Fragment implements ItemUpdater, View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnFocusChangeListener {
    EditText itemDescription;

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

    private MediaPlayer currentAP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_item_description, container, false);
        itemDescription = (EditText) layout.findViewById(R.id.itemDescription);
        itemDescription.setOnFocusChangeListener(this);

        Item item = ((ItemActivity) getActivity()).getItem();
        itemDescription.setText(item.getItemDescription());

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
            resetAudioPlayer(); // sets mPlayer
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        resetAudioPlayer();
        updateItem(((ItemActivity) getActivity()).getItem());
    }

    @Override
    public void updateItem(Item item) {
        item.setItemDescription(itemDescription.getText().toString());
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
        updateItem(((ItemActivity) getActivity()).getItem());
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
            ((ItemActivity) getActivity()).startRecording();
        } else if(v == playButton){
            if (currentAP != null) { // enabled first when a recording has been made
                if (currentAP.isPlaying()) {
                    pauseAudioPlayer();
                } else {
                    startAudioPlayer();
                }
            }
        }else if(v == prevButton){
            if (currentAP != null) { // enabled first when a recording has been made
                    if (!currentAP.isPlaying()) {
                        if (setPrevAP())
                            startAudioPlayer();
                    } else
                        setPrevAP();
            }
        }else if(v == nextButton){
            if (currentAP != null) { // enabled first when a recording has been made
                if (!currentAP.isPlaying()) {
                    if (setNextAP())
                        startAudioPlayer();
                } else
                    setNextAP();
            }
        }
    }

    Runnable apRunnable = new Runnable() {
        @Override
        public void run() {
            if (!currentAP.isPlaying()) {
                boolean hasNext = setNextAP();
                if (hasNext) {
                    currentAP.start();
                } else {
                    seekBar.setProgress(0);
                    posText.setText("0:00.00");
                    forcestopAudioPlayer();
                    return;
                }
            }
            seekBar.setProgress(getAPSCurrentPosition());
            posText.setText(millisToPlayback(getAPSCurrentPosition()));
            apHandler.postDelayed(this, 250);
        }
    };
    
    private boolean setNextAP() {
        for (int i = 0; i < aps.size(); i++) {
            if (aps.get(i) == currentAP && i+1 < aps.size()) {
                if (currentAP.isPlaying())
                    currentAP.pause();
                currentAP = aps.get(i+1);
                currentAP.seekTo(0);
                seekBar.setProgress(getAPSCurrentPosition());
                posText.setText(millisToPlayback(getAPSCurrentPosition()));
                return true;
            }
        }
        return false;
    }

    private boolean setPrevAP() {
        for (int i = 0; i < aps.size(); i++) {
            if (aps.get(i) == currentAP && i-1 > 0) {
                if (currentAP.isPlaying())
                    currentAP.pause();
                currentAP = aps.get(i-1);
                currentAP.seekTo(0);
                seekBar.setProgress(getAPSCurrentPosition());
                posText.setText(millisToPlayback(getAPSCurrentPosition()));
                return true;
            }
        }
        return false;
    }

    private void destroyAudioPlayer() {
        if (currentAP == null)
            return;
        if (currentAP.isPlaying())
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
        currentAP = aps.get(0);
        currentAP.seekTo(0);
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
        for (MediaPlayer ap : aps) {
            if (ap.getDuration() >= progress) {
                currentAP = ap;
                ap.seekTo(progress);
                currentAP = ap;
                return;
            } else {
                progress-=ap.getDuration();
            }
        }
    }

    private int getAPSCurrentPosition() {
        int totalDuration = currentAP.getCurrentPosition();
        for (MediaPlayer ap: aps) {
            if (ap == currentAP) {
                return totalDuration;
            } else
                totalDuration += ap.getDuration();
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
        System.out.println();
        System.out.println("RESET AUDIO PLAYER DESCRIPTION");
        seekBar.setProgress(0);
        posText.setText("0:00.00");
        if (aps != null) {
            for (MediaPlayer mp : aps) {
                mp.release();
                mp = null;
            }
            aps.clear();
        }
        List<Uri> recordings = new ArrayList<Uri>();
        List<Uri> recordingsBE = ((ItemActivity) getActivity()).getItem().getRecordings();
        List<Uri> recordingsFE = ((ItemActivity) getActivity()).getItem().getAddedRecordings();
        if (recordingsBE != null)
            recordings.addAll(recordingsBE);
        if (recordingsFE != null)
            recordings.addAll(recordingsFE);
        if (recordings.isEmpty()) {
            seekBar.setEnabled(false);
            audioText.setText("No audio files");
            durText.setText("0:00.00");
        } else {
            seekBar.setEnabled(true);
            System.out.println(recordings.size());
            aps = new ArrayList<MediaPlayer>();
            for (Uri uri : recordings) {
                System.out.println(uri);
                File audioFile = new File(uri.getPath());
                if (audioFile.exists()) {
                    aps.add(MediaPlayer.create(getActivity(), Uri.parse(uri.getPath())));
                }
            }
            currentAP = aps.get(0);
            seekBar.setMax(getAPSDuration());
            audioText.setText(aps.size() + " audio files");
            durText.setText(millisToPlayback(getAPSDuration()));
        }
    }
    
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus)
            App.hideKeyboard(getActivity(), v);
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
