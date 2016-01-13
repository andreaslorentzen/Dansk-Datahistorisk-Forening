package app.ddf.danskdatahistoriskforening.item;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import app.ddf.danskdatahistoriskforening.helper.BitmapEncoder;
import app.ddf.danskdatahistoriskforening.image.ImageviewerDeleteActivity;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.helper.LocalMediaStorage;
import app.ddf.danskdatahistoriskforening.R;

public class ItemFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, ItemUpdater {
    //TODO calculate acceptable thumbnail dimensions based on screensize or available space
    private final int MAX_THUMBNAIL_WIDTH = 150;
    private final int MAX_THUMBNAIL_HEIGHT = 250;

    //onActivityResult state variables
    boolean isCreated = false;
    boolean hasResult = false;
    int requestCode;
    int resultCode;
    Intent data;

    //views
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
    public void onCreate(Bundle savedInstanceState) {
        Log.d("ddfstate", "onCreate");

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ddfstate", "onCreateView");

        View layout = inflater.inflate(R.layout.fragment_item, container, false);
        cameraButton = (ImageButton) layout.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);
        micButton =  (ImageButton) layout.findViewById(R.id.micButton);
        micButton.setOnClickListener(this);
        itemTitle = (EditText) layout.findViewById(R.id.itemTitle);

        Item item = ((ItemActivity) getActivity()).getItem();
        Log.d("ddfstate", item + "");
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

        //delayed onActivityResult
        /*isCreated = true;

        if(hasResult) {
            Log.d("ddfstate", "has result");
            onActivityResult(requestCode, resultCode, data);
            hasResult = false;
            data = null;
        }*/

        return layout;
    }

    // shit like this maybe
    @Override
    public void onDetach() {
        super.onDetach();
        killAudioPlayer(); // stop mPlayer and mHandler
    }

    @Override
    public void onPause() {
        Log.d("ddfstate", "onPause");

        super.onPause();

        killAudioPlayer(); // stop mPlayer and mHandler

        updateItem(((ItemActivity) getActivity()).getItem());

        imageContainer.removeAllViews();

    }

    @Override
    public void updateItem(Item item){
        item.setItemHeadline(itemTitle.getText().toString());
    }

    @Override
    public void onResume() {
        super.onResume();

        ArrayList imageUris = ((ItemActivity)getActivity()).getImageUris();

        for(int i=0; i<imageUris.size(); i++){
            Pair p = (Pair) imageUris.get(i);

            imageContainer.addView((View) p.first);
            ((View) p.first).setOnClickListener(this);
        }

        setAudioPlayer(); // resets mPlayer
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
            Intent i = new Intent(getActivity(), RecordingActivity.class);
            startActivity(i);
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
        } else { //image tapped
                int index = -1;
                ArrayList imageUris = ((ItemActivity)getActivity()).getImageUris();
                ArrayList<Uri> uris = new ArrayList<>();
                for(int i = 0; i<imageUris.size(); i++){
                    Pair p = (Pair) imageUris.get(i);
                    if(p.first == v){
                        //the correct imageView was found
                        index = i;
                    }

                    uris.add((Uri) p.second);
                }

                if(index < 0){
                    //none of the imageViews matched
                    Log.d("ddf", "no imageView matched");
                    return;
                }

                Intent intent = new Intent(getActivity(), ImageviewerDeleteActivity.class);
                intent.putExtra("imageURIs", uris);
                intent.putExtra("index", index);
                getActivity().startActivityForResult(intent, ItemActivity.IMAGEVIEWER_REQUEST_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //OnActivityResult is called before onViewCreated
        //delay update if necessary
        Log.d("ddfstate", "is created: " + isCreated);

        /*if(!isCreated){
            hasResult = true;
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.data = data;

            return;
        }*/

        ArrayList imageUris = ((ItemActivity)getActivity()).getImageUris();

        if (requestCode == ItemActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("ddfstate", imageUris + "");

                // Image captured and saved to fileUri specified in the Intent
                int lastIndex = imageUris.size()-1;
                Pair p = (Pair) imageUris.get(lastIndex);
                ImageView image = (ImageView) p.first;

                LinearLayout.LayoutParams sizeParameters = new LinearLayout.LayoutParams(MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
                image.setLayoutParams(sizeParameters);

                BitmapEncoder.loadBitmapFromURI(image, (Uri) p.second, MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
                image.setOnClickListener(this);

                imageContainer.addView(image);

                if(((ItemActivity)getActivity()).getItem() != null) {
                    ((ItemActivity) getActivity()).getItem().addToAddedPictures((Uri) p.second);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
                //clean up
                imageUris.remove(imageUris.size() - 1);
            } else {
                // Image capture failed, advise user
                imageUris.remove(imageUris.size()-1);
                Toast.makeText(getActivity(), "Der opstod en fejl under brug af kameraet" , Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode == ItemActivity.IMAGEVIEWER_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                ArrayList<Uri> remainingURIs = data.getParcelableArrayListExtra("remainingURIs");

                //no change
                if(remainingURIs.size() == imageUris.size()){
                    return;
                }

                //update image list and reconstruct imageContainer
                imageContainer.removeAllViews();

                if(((ItemActivity)getActivity()).getItem() != null){
                    ((ItemActivity)getActivity()).getItem().setPicturesChanged(true);
                }
                ArrayList temp = new ArrayList<Pair<ImageView, Uri>>(imageUris);
                for(int i = 0; i<imageUris.size(); i++){
                    Pair listItem = (Pair) imageUris.get(i);

                    if(!remainingURIs.contains(listItem.second)){
                        //image has been removed
                        if(((ItemActivity) getActivity()).getItem() != null){
                            Item currentItem = ((ItemActivity) getActivity()).getItem();
                            if(currentItem.getAddedPictures() !=  null){
                                if(!currentItem.getAddedPictures().contains(listItem.second)){
                                    currentItem.addDeletedPicture((Uri) listItem.second);
                                } else{
                                    currentItem.removeFromAddedPicture((Uri) listItem.second);
                                }
                            }
                        }
                        temp.remove(listItem);
                    }
                    else{
                        //image still exists
                        imageContainer.addView((ImageView) listItem.first);
                    }
                }

                ((ItemActivity) getActivity()).setImageUris(temp);
            }
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
        audioUris = new ArrayList<Uri>();
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

    public String getItemTitle(){
        if(itemTitle == null){
            return "";
        }
        return itemTitle.getText().toString();
    }

    public void setItemTitle(String title) {
        this.itemTitle.setText(title);
    }
}
