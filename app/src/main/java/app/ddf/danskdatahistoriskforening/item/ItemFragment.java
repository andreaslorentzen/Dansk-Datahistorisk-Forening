package app.ddf.danskdatahistoriskforening.item;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;

import app.ddf.danskdatahistoriskforening.helper.BitmapEncoder;
import app.ddf.danskdatahistoriskforening.image.ImageviewerActivity;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.helper.LocalMediaStorage;
import app.ddf.danskdatahistoriskforening.R;

public class ItemFragment extends Fragment implements View.OnClickListener{
    private final int MAX_THUMBNAIL_WIDTH = 150;
    private final int MAX_THUMBNAIL_HEIGHT = 250;

    ImageButton cameraButton;
    ImageButton micButton;
    EditText itemTitle;
    LinearLayout imageContatiner;
    ArrayList<Pair<ImageView,Uri>> imageUris;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_item, container, false);
        cameraButton = (ImageButton) layout.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);
        micButton =  (ImageButton) layout.findViewById(R.id.micButton);
        micButton.setOnClickListener(this);
        itemTitle = (EditText) layout.findViewById(R.id.itemTitle);

        Item item = ((ItemActivity) getActivity()).getItem();
        itemTitle.setText(item.getItemHeadline());
        //TODO indsæt billeder, lyd

        //((HorizontalScrollView) layout.findViewById(R.id.horizontalScrollView)).setFillViewport(true);
        imageContatiner = (LinearLayout) layout.findViewById(R.id.imageContainer);
        imageUris = new ArrayList<>();

        return layout;
    }

    // shit like this maybe
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        if(v == cameraButton){
            //http://developer.android.com/guide/topics/media/camera.html#intents
            Uri fileUri = LocalMediaStorage.getOutputMediaFileUri(LocalMediaStorage.MEDIA_TYPE_IMAGE);
            if(fileUri != null) {
                imageUris.add(new Pair<>(new ImageView(getActivity()), fileUri));

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                getActivity().startActivityForResult(intent, ItemActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
            else{
                Toast.makeText(getActivity(), "Der opstod en fejl ved oprettelse af billedet, sørg for at SD kortet er tilgængeligt og prøv igen.", Toast.LENGTH_LONG).show();
            }
        }
        if(v == micButton){
            Intent i = new Intent(getActivity(), RecordingActivity.class);
            startActivity(i);
        }
        else { //image tapped
                int index = -1;
                ArrayList<Uri> uris = new ArrayList<>();
                for(int i = 0; i<imageUris.size(); i++){
                    if(imageUris.get(i).first == v){
                        //the correct imageView was found
                        index = i;
                    }

                    uris.add(imageUris.get(i).second);
                }

                if(index < 0){
                    //none of the imageViews matched
                    Log.d("ddf", "no imageView matched");
                    return;
                }

                Intent intent = new Intent(getActivity(), ImageviewerActivity.class);
                intent.putExtra("imageURIs", uris);
                intent.putExtra("index", index);
                getActivity().startActivityForResult(intent, ItemActivity.IMAGEVIEWER_REQUEST_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ItemActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                ImageView image = imageUris.get(imageUris.size()-1).first;

                LinearLayout.LayoutParams sizeParameters = new LinearLayout.LayoutParams(MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
                image.setLayoutParams(sizeParameters);

/*

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap thumbnail = BitmapFactory.decodeFile(imageUris.get(imageUris.size() - 1).second.getPath(), options);
                image.setImageBitmap(thumbnail);
*/


                BitmapEncoder.decodeFile(image, imageUris.get(imageUris.size() - 1).second, MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
                image.setOnClickListener(this);

                //image.setImageURI(imageUris.get(imageUris.size()-1));
                imageContatiner.addView(image);

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
                imageContatiner.removeAllViews();

                ArrayList temp = new ArrayList<Pair<ImageView, Uri>>(imageUris);
                for(int i = 0; i<imageUris.size(); i++){
                    Pair listItem = imageUris.get(i);

                    if(!remainingURIs.contains(listItem.second)){
                        //image has been removed
                        temp.remove(listItem);
                    }
                    else{
                        //image still exists
                        imageContatiner.addView((ImageView)listItem.first);
                    }
                }

                imageUris = temp;
            }
        }
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
