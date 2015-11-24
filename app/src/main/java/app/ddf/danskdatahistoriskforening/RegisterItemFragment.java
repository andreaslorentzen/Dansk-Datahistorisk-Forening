package app.ddf.danskdatahistoriskforening;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class RegisterItemFragment extends Fragment implements View.OnClickListener{

    ImageButton cameraButton;
    ImageButton micButton;
    ImageView imageView;
    RadioGaga rg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_register_item, container, false);
        cameraButton = (ImageButton) layout.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);
        micButton =  (ImageButton) layout.findViewById(R.id.micButton);
        micButton.setOnClickListener(this);
        imageView = (ImageView) layout.findViewById(R.id.imageView);
        rg = new RadioGaga();
        return layout;
    }

    // shit like this maybe
    @Override
    public void onDetach() {
        super.onDetach();
        rg.requestStop();
    }


    Uri fileUri;

    @Override
    public void onClick(View v) {
        if(v == cameraButton){
            //http://developer.android.com/guide/topics/media/camera.html#intents
            fileUri = LocalMediaStorage.getOutputMediaFileUri(LocalMediaStorage.MEDIA_TYPE_IMAGE);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            getActivity().startActivityForResult(intent, RegisterActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
        if(v == micButton){
            rg.execute();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RegisterActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                imageView.setImageURI(fileUri);
             //   Toast.makeText(getActivity(),  Toast.LENGTH_LONG).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // Image capture failed, advise user
                Toast.makeText(getActivity(), "Failed" , Toast.LENGTH_LONG).show();
            }
        }
    }

}
