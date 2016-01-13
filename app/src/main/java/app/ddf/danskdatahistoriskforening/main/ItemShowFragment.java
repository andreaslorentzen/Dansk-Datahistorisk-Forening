package app.ddf.danskdatahistoriskforening.main;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.helper.BitmapEncoder;
import app.ddf.danskdatahistoriskforening.image.ImageviewerSimpleActivity;


public class ItemShowFragment extends Fragment implements View.OnClickListener{
    //TODO calculate acceptable thumbnail dimensions based on screensize or available space
    private final int MAX_THUMBNAIL_WIDTH = 150;
    private final int MAX_THUMBNAIL_HEIGHT = 250;

    private String detailsURI;

    private TextView itemheadlineView;
    private TextView itemdescriptionView;
    private TextView receivedView;
    private TextView datingFromView;
    private TextView datingToView;
    private TextView donatorView;
    private TextView producerView;
    private TextView postNummerView;

    private LinearLayout imageContainer;
    private ArrayList<Pair<ImageView, Uri>> imageUris;

    private boolean shouldReloadFromAPI = true;

    public ItemShowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageUris = new ArrayList<>();

        if(savedInstanceState != null){
            shouldReloadFromAPI = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_item_show, container, false);

        itemheadlineView = (TextView) layout.findViewById(R.id.itemheadline);
        itemdescriptionView = (TextView) layout.findViewById(R.id.itemdescription);
        receivedView = (TextView) layout.findViewById(R.id.received);
        datingFromView = (TextView) layout.findViewById(R.id.datingFrom);
        datingToView = (TextView) layout.findViewById(R.id.datingTo);
        donatorView = (TextView) layout.findViewById(R.id.donator);
        producerView = (TextView) layout.findViewById(R.id.producer);
        postNummerView = (TextView) layout.findViewById(R.id.postNummer);
        imageContainer = (LinearLayout) layout.findViewById(R.id.imageContainer);

    //    ((MainActivity) getActivity()).updateSearchVisibility();

        //setDetailsURI(Model.getInstance().getCurrentDetailsURI());

        return layout;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(shouldReloadFromAPI) {
            setDetailsURI(Model.getInstance().getCurrentDetailsURI());
        }
        else{
            updateViews(Model.getInstance().getCurrentItem());
        }
    }

    private void updateViews(Item currentItem){
        // felterne udfyld felterne
        itemheadlineView.setText(currentItem.getItemHeadline());
        // TODO handle lyd
        itemdescriptionView.setText(currentItem.getItemDescription());
        receivedView.setText(((currentItem.getItemRecievedAsString() == null) ? null : currentItem.getItemRecievedAsString()));
        datingFromView.setText(((currentItem.getItemDatingFromAsString() == null) ? null : currentItem.getItemDatingFromAsString()));
        datingToView.setText(((currentItem.getItemDatingToAsString() == null) ? null : currentItem.getItemDatingToAsString()));

        donatorView.setText(currentItem.getDonator());
        producerView.setText(currentItem.getProducer());
        postNummerView.setText(currentItem.getPostalCode());

        //create picture thumbnails
        ArrayList<Uri> uris = currentItem.getPictures();
        Object context = getActivity();
        Log.d("ddfstate", "Activity: " + getActivity());
        Log.d("ddfstate", uris + "");

        if(uris != null && context != null){//activity may have been destroyed while downloading
            for(int i = 0; i<uris.size(); i++){


                Pair<ImageView, Uri> uriImagePair = new Pair(new ImageView(getActivity()), uris.get(i));
                LinearLayout.LayoutParams sizeParameters = new LinearLayout.LayoutParams(MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
                uriImagePair.first.setLayoutParams(sizeParameters);

                imageContainer.addView(uriImagePair.first);
                imageUris.add(uriImagePair);

                BitmapEncoder.loadBitmapFromURI(uriImagePair.first, uriImagePair.second, MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
                uriImagePair.first.setOnClickListener(ItemShowFragment.this);
            }
        }
    }

    public void setDetailsURI(String detailsURI){
        this.detailsURI = detailsURI;

        new AsyncTask<String, Void, Item>() {
            @Override
            protected Item doInBackground(String ... params) {
                Log.d("ddfstate", "start of download details " + params[0]);
                return Model.getDAO().getDetailsFromBackEnd(params[0]);
            }

            @Override
            protected void onPostExecute(Item data) {

                if (data != null){
                    Model.getInstance().setCurrentItem(data);
                    Item currentItem = data;
                    Log.d("itemdetails", data.toJSON().toString());

                    updateViews(currentItem);

                    /*// felterne udfyld felterne
                    itemheadlineView.setText(currentItem.getItemHeadline());
                    // TODO handle lyd
                    itemdescriptionView.setText(currentItem.getItemDescription());
                    receivedView.setText(((currentItem.getItemRecievedAsString() == null) ? null : currentItem.getItemRecievedAsString()));
                    datingFromView.setText(((currentItem.getItemDatingFromAsString() == null) ? null : currentItem.getItemDatingFromAsString()));
                    datingToView.setText(((currentItem.getItemDatingToAsString() == null) ? null : currentItem.getItemDatingToAsString()));

                    donatorView.setText(currentItem.getDonator());
                    producerView.setText(currentItem.getProducer());
                    postNummerView.setText(currentItem.getPostalCode());

                    //create picture thumbnails
                    ArrayList<Uri> uris = currentItem.getPictures();
                    Object context = getActivity();
                    Log.d("ddfstate", "Activity: " + getActivity());
                    Log.d("ddfstate", uris + "");

                    if(uris != null && context != null){//activity may have been destroyed while downloading
                        for(int i = 0; i<uris.size(); i++){


                            Pair<ImageView, Uri> uriImagePair = new Pair(new ImageView(getActivity()), uris.get(i));
                            LinearLayout.LayoutParams sizeParameters = new LinearLayout.LayoutParams(MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
                            uriImagePair.first.setLayoutParams(sizeParameters);

                            imageContainer.addView(uriImagePair.first);
                            imageUris.add(uriImagePair);

                            BitmapEncoder.loadBitmapFromURI(uriImagePair.first, uriImagePair.second, MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
                            uriImagePair.first.setOnClickListener(ItemShowFragment.this);
                        }
                    }*/


                } else {
                    Log.d("itemdetails", "else");
                    //TODO ERROR HANDLING FOR data = null
                }
            }
        }.execute(detailsURI);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public String getDetailsURI(){
        return this.detailsURI;
    }

    @Override
    public void onClick(View v) {
        if(v instanceof ImageView){
            int index = -1;
            ArrayList<Uri> uris = new ArrayList<>();
            for(int i=0; i<imageUris.size(); i++){
                if(v == imageUris.get(i).first){
                    index = i;
                }

                uris.add(imageUris.get(i).second);
            }

            if(index < 0){
                //none of the imageViews matched
                Log.d("ddf", "no imageView matched");
                return;
            }

            Intent intent = new Intent(getActivity(), ImageviewerSimpleActivity.class);
            intent.putExtra("imageURIs", uris);
            intent.putExtra("index", index);
            getActivity().startActivity(intent);
        }
    }
}
