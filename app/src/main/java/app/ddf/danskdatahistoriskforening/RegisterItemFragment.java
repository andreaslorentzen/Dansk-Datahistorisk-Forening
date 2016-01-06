package app.ddf.danskdatahistoriskforening;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RegisterItemFragment extends Fragment implements View.OnClickListener{

    ImageButton cameraButton;
    ImageButton micButton;
    //ImageView imageView;
    RadioGaga rg;
    EditText itemTitle;
    LinearLayout imageContatiner;
    ArrayList<Pair<ImageView,Uri>> imageUris;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_register_item, container, false);
        cameraButton = (ImageButton) layout.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);
        micButton =  (ImageButton) layout.findViewById(R.id.micButton);
        micButton.setOnClickListener(this);
        //imageView = (ImageView) layout.findViewById(R.id.imageView);
        itemTitle = (EditText) layout.findViewById(R.id.itemTitle);
        rg = new RadioGaga();

        //((HorizontalScrollView) layout.findViewById(R.id.horizontalScrollView)).setFillViewport(true);
        imageContatiner = (LinearLayout) layout.findViewById(R.id.imageContainer);
        imageUris = new ArrayList<>();

        return layout;
    }

    // shit like this maybe
    @Override
    public void onDetach() {
        super.onDetach();
        rg.requestStop();
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
                getActivity().startActivityForResult(intent, RegisterActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
            else{
                Toast.makeText(getActivity(), "Der opstod en fejl ved oprettelse af billedet, sørg for at SD kortet er tilgængeligt og prøv igen.", Toast.LENGTH_LONG).show();
            }
        }
        else if(v == micButton){
            //Intent i = new Intent(getActivity(), RecordingActivity.class);
            //startActivity(i);
        }
        else { //image tapped
            try {
                ImageView image = (ImageView) v;

                int index = -1;
                for(int i = 0; i<imageUris.size(); i++){
                    if(imageUris.get(i).first == image){
                        //the correct imageView was found
                        index = i;
                        break;
                    }
                }

                if(index < 0){
                    //none of the imageViews matched
                    Log.d("ddf", "no imageView matched");
                    return;
                }

                Toast.makeText(getActivity(), "" + index, Toast.LENGTH_LONG).show();
            }
            catch (ClassCastException e){
                //View was not an image
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RegisterActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent

                ImageView image = imageUris.get(imageUris.size()-1).first;
                LinearLayout.LayoutParams sizeParameters = new LinearLayout.LayoutParams(150, 250);
                image.setLayoutParams(sizeParameters);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap thumbnail = BitmapFactory.decodeFile(imageUris.get(imageUris.size() - 1).second.getPath(), options);
                image.setImageBitmap(thumbnail);
                image.setOnClickListener(this);

                //image.setImageURI(imageUris.get(imageUris.size()-1));
                imageContatiner.addView(image);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // Image capture failed, advise user
                Toast.makeText(getActivity(), "Failed" , Toast.LENGTH_LONG).show();
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


    //needed for generating unique ids in android versions less than 17
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in {@link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}
