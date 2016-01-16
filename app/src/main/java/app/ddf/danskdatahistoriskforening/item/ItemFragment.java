package app.ddf.danskdatahistoriskforening.item;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import app.ddf.danskdatahistoriskforening.App;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.helper.LocalMediaStorage;

public class ItemFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, ItemUpdater, TextView.OnEditorActionListener {

    ImageButton cameraButton;
    ImageButton micButton;
    EditText itemTitle;
    LinearLayout imageContainer;
    ImageButton audioButton;
    SeekBar seekBar;
    MediaPlayer mPlayer;
    Handler mHandler;
    TextView durText;
    TextView posText;
    TextView audioText;

    ArrayList<Uri> audioUris;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_item, container, false);
        cameraButton = (ImageButton) layout.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);
        micButton =  (ImageButton) layout.findViewById(R.id.micButton);
        micButton.setOnClickListener(this);
        itemTitle = (EditText) layout.findViewById(R.id.itemTitle);
        itemTitle.setOnEditorActionListener(this);

        Item item = ((ItemActivity) getActivity()).getItem();
        itemTitle.setText(item.getItemHeadline());

        //TODO indsæt lyd

        //((HorizontalScrollView) layout.findViewById(R.id.horizontalScrollView)).setFillViewport(true);
        imageContainer = (LinearLayout) layout.findViewById(R.id.imageContainer);

        // AUDIO
        posText = (TextView) layout.findViewById(R.id.posText);
        durText = (TextView) layout.findViewById(R.id.durText);
        audioText = (TextView) layout.findViewById(R.id.audioText);
        audioButton = (ImageButton) layout.findViewById(R.id.audioButton);
        seekBar = (SeekBar) layout.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        audioButton.setOnClickListener(this);
        mHandler=  new Handler();
        if(savedInstanceState == null){
            setAudioPlayer(); // sets mPlayer
        }
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        ArrayList imageUris = ((ItemActivity)getActivity()).getImageUris();

        imageContainer.removeAllViews();

        for(int i=0; i<imageUris.size(); i++){
            Pair p = (Pair) imageUris.get(i);

            imageContainer.addView((View) p.first);
            //    ((View) p.first).setOnClickListener((View.OnClickListener) getActivity());
            //    ((View) p.first).setOnClickListener(this);
        }

        setAudioPlayer(); // resets mPlayer

    }

    @Override
    public void onPause() {
        super.onPause();

        killAudioPlayer(); // stop mPlayer and mHandler

        updateItem(((ItemActivity) getActivity()).getItem());
    }

    // shit like this maybe
    @Override
    public void onDetach() {
        super.onDetach();
        killAudioPlayer(); // stop mPlayer and mHandler
    }

    @Override
    public void updateItem(Item item){
        item.setItemHeadline(itemTitle.getText().toString());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!isVisibleToUser)
            App.hideKeyboard(getActivity(), itemTitle);
    }

    @Override
    public void onClick(View v) {
        if(v == cameraButton){
            //http://developer.android.com/guide/topics/media/camera.html#intents
            Uri fileUri = LocalMediaStorage.getOutputMediaFileUri(LocalMediaStorage.MEDIA_TYPE_IMAGE);
            if(fileUri != null) {

                ((ItemActivity)getActivity()).setTempUri(fileUri);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                getActivity().startActivityForResult(intent, ItemActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
            else{
                Toast.makeText(getActivity(), "Der opstod en fejl ved oprettelse af billedet, sørg for at SD kortet er tilgængeligt og prøv igen.", Toast.LENGTH_LONG).show();
            }
        } else if(v == micButton){
            ((ItemActivity) getActivity()).startRecording();

        } else if(v == audioButton) {
            if (mPlayer != null) {
                if (mPlayer.isPlaying()) {
                    audioButton.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                    mPlayer.pause();
                    mHandler.removeCallbacks(timerRunnable);
                } else {
                    audioButton.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                    mPlayer.start();
                    mHandler.postDelayed(timerRunnable, 0);
                }
            } else
                Toast.makeText(getActivity(), "Audio file not found - start recording!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        v.clearFocus();
        App.hideKeyboard(getActivity(), v);
        return false;
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
        audioButton.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
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
            audioButton.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
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

}
