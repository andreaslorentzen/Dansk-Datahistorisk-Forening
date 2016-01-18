package app.ddf.danskdatahistoriskforening.item;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.ddf.danskdatahistoriskforening.App;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.domain.Logic;
import app.ddf.danskdatahistoriskforening.helper.LocalMediaStorage;

public class ItemFragment extends Fragment implements View.OnClickListener, ItemUpdater, TextView.OnEditorActionListener {

    ImageButton cameraButton;
    EditText itemTitle;
    LinearLayout imageContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_item, container, false);
        cameraButton = (ImageButton) layout.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);
        itemTitle = (EditText) layout.findViewById(R.id.itemTitle);
        itemTitle.setOnEditorActionListener(this);

        Item item = Logic.instance.editItem;
        itemTitle.setText(item.getItemHeadline());
        //((HorizontalScrollView) layout.findViewById(R.id.horizontalScrollView)).setFillViewport(true);
        imageContainer = (LinearLayout) layout.findViewById(R.id.imageContainer);
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        imageContainer.removeAllViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        updateItem(Logic.instance.editItem);
        //if fragment is destroyed imageViews need to be added to a new container
    }

    @Override
    public void updateItem(Item item){
        item.setItemHeadline(itemTitle.getText().toString());
    }

    @Override
    public void onResume() {
        super.onResume();

        HashMap<Uri, ImageView> imageViews = ((ItemActivity)getActivity()).imageViews;

        imageContainer.removeAllViews();

        Item item = Logic.instance.editItem;

        ArrayList<Uri> uris = item.getPictures();
        if (uris != null)
            for (Uri uri : uris) {
                imageContainer.addView(imageViews.get(uri));
            }

        uris = item.getAddedPictures();
        if (uris != null)
            for (Uri uri : uris) {
                imageContainer.addView(imageViews.get(uri));
            }


/*
        for(int i=0; i<imageViews.size(); i++){
            //    ((View) p.first).setOnClickListener((View.OnClickListener) getActivity());
            //    ((View) p.first).setOnClickListener(this);
        }
*/
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        v.clearFocus();
        App.hideKeyboard(getActivity(), v);
        return false;
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!isVisibleToUser)
            App.hideKeyboard(getActivity(), itemTitle);
    }
}