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
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import app.ddf.danskdatahistoriskforening.App;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.helper.LocalMediaStorage;

public class ItemDescriptionFragment extends Fragment implements ItemUpdater, View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnFocusChangeListener {
    EditText itemDescription;

    //views
    ImageButton recButton;
    ImageButton playButton;
    ImageButton prevButton;
    ImageButton nextButton;
    SeekBar seekBar;
    MediaPlayer mPlayer;
    Handler mHandler;
    TextView durText;
    TextView posText;
    TextView audioText;
    ArrayList<Uri> audioUris;

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

        mHandler=  new Handler();
        if(savedInstanceState == null){
            setAudioPlayer(); // sets mPlayer
        }


        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        setAudioPlayer(); // resets mPlayer
    }

    @Override
    public void updateItem(Item item) {
        item.setItemDescription(itemDescription.getText().toString());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        killAudioPlayer(); // stop mPlayer and mHandler
    }

    @Override
    public void onPause() {
        super.onPause();
        killAudioPlayer(); // stop mPlayer and mHandler
        updateItem(((ItemActivity) getActivity()).getItem());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mPlayer != null && fromUser) {
            mPlayer.seekTo(progress);
            setAudioText(posText, mPlayer.getCurrentPosition());
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
        } else if (v == playButton) {
            if (mPlayer != null) {
                if (mPlayer.isPlaying()) {
                    playButton.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                    mPlayer.pause();
                    mHandler.removeCallbacks(timerRunnable);
                } else {
                    playButton.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                    mPlayer.start();
                    mHandler.postDelayed(timerRunnable, 0);
                }
            } else
                Toast.makeText(getActivity(), "Audio file not found - start recording!", Toast.LENGTH_LONG).show();
        }
    }

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (!mPlayer.isPlaying()) {
                setAudioText(posText, mPlayer.getDuration());
                killAudioPlayer();
                seekBar.setProgress(mPlayer.getDuration());
                return;
            }
            setAudioText(posText, mPlayer.getCurrentPosition());
            seekBar.setProgress(mPlayer.getCurrentPosition());
            mHandler.postDelayed(this, 500);
        }
    };

    private void setAudioText(TextView vt, int millis) {
        String posString = RecordingActivity.millisToPlayback(millis);
        vt.setText(posString.substring(0, posString.length() - 3));
    }

    private void killAudioPlayer() {
        playButton.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        mHandler.removeCallbacks(timerRunnable);
        if (mPlayer == null)
            return;
        if (mPlayer.isPlaying())
            mPlayer.stop();

    }
    private void setAudioPlayer() {
        MediaPlayer oldPlayer = mPlayer;
        String filePath = LocalMediaStorage.getOutputMediaFileUri(2).getPath();
        File mainFile = new File(filePath);
        if (!mainFile.exists()) {
            durText.setText("");
            posText.setText("");
            audioText.setText("No audio file existing.");
            seekBar.setProgress(0);
            seekBar.setEnabled(false);
            playButton.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
            if (mPlayer!=null) {
                mPlayer.release();
                mPlayer = null;
            }
            return;
        }
        audioUris = new ArrayList<>();
        audioUris.add(LocalMediaStorage.getOutputMediaFileUri(2));
        seekBar.setEnabled(true);
        audioText.setText("");
        mPlayer = MediaPlayer.create(getActivity(), Uri.parse(filePath));
        seekBar.setMax(mPlayer.getDuration());
        setAudioText(durText, mPlayer.getDuration());
        if (oldPlayer != null) {
            mPlayer.seekTo(oldPlayer.getCurrentPosition());
            setAudioText(posText, mPlayer.getCurrentPosition());
            oldPlayer.release();
        } else
            posText.setText("0:00:00");
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus)
            App.hideKeyboard(getActivity(), v);
    }
}
