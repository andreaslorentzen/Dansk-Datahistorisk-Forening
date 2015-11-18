package app.ddf.danskdatahistoriskforening;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

public class RegisterItemFragment extends Fragment implements View.OnClickListener{
    static private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    ImageButton cameraButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_register_item, container, false);

        cameraButton = (ImageButton) layout.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);

        return layout;
    }

    public void getTitle() {
    }

    public void getImages() {
    }

    public void getRecoording() {
    }

    @Override
    public void onClick(View v) {
        if(v == cameraButton){
            //http://developer.android.com/guide/topics/media/camera.html#intents
            Uri fileUri;

            fileUri = LocalMediaStorage.getOutputMediaFileUri(LocalMediaStorage.MEDIA_TYPE_IMAGE);

            if(fileUri == null) {
                Toast.makeText(getActivity(), "Could not access external storage", Toast.LENGTH_LONG);
                return;
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


}
