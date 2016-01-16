package app.ddf.danskdatahistoriskforening.item;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.helper.LocalMediaStorage;

public class ItemFragment extends Fragment implements View.OnClickListener, ItemUpdater {
    ImageButton cameraButton;
    EditText itemTitle;
    LinearLayout imageContainer;
    ImageButton audioButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ddfstate", "onCreateView");
        View layout = inflater.inflate(R.layout.fragment_item, container, false);
        cameraButton = (ImageButton) layout.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);
        itemTitle = (EditText) layout.findViewById(R.id.itemTitle);
        Item item = ((ItemActivity) getActivity()).getItem();
        itemTitle.setText(item.getItemHeadline());
        //((HorizontalScrollView) layout.findViewById(R.id.horizontalScrollView)).setFillViewport(true);
        imageContainer = (LinearLayout) layout.findViewById(R.id.imageContainer);
        return layout;
    }

    // shit like this maybe
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        Log.d("ddfstate", "onPause");
        super.onPause();
        updateItem(((ItemActivity) getActivity()).getItem());
        //if fragment is destroyed imageViews need to be added to a new container
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
        imageContainer.removeAllViews();
        for(int i=0; i<imageUris.size(); i++){
            Pair p = (Pair) imageUris.get(i);
            imageContainer.addView((View) p.first);
        //    ((View) p.first).setOnClickListener((View.OnClickListener) getActivity());
        //    ((View) p.first).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == cameraButton){
            //http://developer.android.com/guide/topics/media/camera.html#intents
            Uri fileUri = LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_IMAGE);
            if(fileUri != null) {
                ((ItemActivity)getActivity()).setTempUri(fileUri);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                getActivity().startActivityForResult(intent, ItemActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            } else
                Toast.makeText(getActivity(), "Der opstod en fejl ved oprettelse af billedet, sørg for at SD kortet er tilgængeligt og prøv igen.", Toast.LENGTH_LONG).show();
        }
    }
}