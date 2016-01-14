package app.ddf.danskdatahistoriskforening.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.helper.BitmapEncoder;
import app.ddf.danskdatahistoriskforening.image.ImageviewerSimpleActivity;


public class ItemShowFragment extends Fragment implements View.OnClickListener, Model.OnCurrentItemChangeListener {
    //TODO calculate acceptable thumbnail dimensions based on screensize or available space
    private final int MAX_THUMBNAIL_WIDTH = 150;
    private final int MAX_THUMBNAIL_HEIGHT = 250;

    private TextView itemheadlineView;
    private TextView itemdescriptionView;
    private TextView receivedView;
    private TextView datingFromView;
    private TextView datingToView;
    private TextView donatorView;
    private TextView producerView;
    private TextView postNummerView;

    private LinearLayout contentWrapper;
    private ProgressBar progressBar;

    private LinearLayout imageContainer;
    private ArrayList<Pair<ImageView, Uri>> imageUris;

    private boolean isLoaded = false;
    private String loadedURI;

    public ItemShowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageUris = new ArrayList<>();

        if (savedInstanceState != null) {
            isLoaded = savedInstanceState.getBoolean("isLoaded");
            loadedURI = savedInstanceState.getString("loadedURI");
        }

        ((MainActivity) getActivity()).disableEdit();
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

        contentWrapper = (LinearLayout) layout.findViewById(R.id.item_details_wrapper);
        progressBar = (ProgressBar) layout.findViewById(R.id.item_details_progressbar);
        //    ((MainActivity) getActivity()).updateSearchVisibility();

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("isLoaded", isLoaded);
        outState.putString("loadedURI", loadedURI);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Model.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        }, filter);

        Model.getInstance().setOnCurrentItemChangeListener(this);

        Item currentItem = Model.getInstance().getCurrentItem();
        onCurrentItemChange(currentItem);

        Log.d("ddfstate", "isLoaded: " + isLoaded);
        Log.d("ddfstate", "loadedURI: " + loadedURI);

        //avoid downloading details if possible

    }

    @Override
    public void onPause() {
        super.onPause();
        Model.getInstance().setOnCurrentItemChangeListener(null);
    }

    @Override
    public void onCurrentItemChange(Item currentItem) {
        if (currentItem == null) {
            ((MainActivity) getActivity()).disableEdit();
            //   fetchCurrentItem();
        }
        else {
            ((MainActivity) getActivity()).enableEdit();
        }
        updateViews();

    }

    private void updateViews() {
        Item currentItem = Model.getInstance().getCurrentItem();
        if(currentItem == null) {
            progressBar.setVisibility(View.VISIBLE);
            contentWrapper.setVisibility(View.GONE);
            return;
        }

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

        progressBar.setVisibility(View.GONE);
        contentWrapper.setVisibility(View.VISIBLE);

        //create picture thumbnails
        ArrayList<Uri> uris = currentItem.getPictures();
        Object context = getActivity();
        Log.d("ddfstate", "Activity: " + getActivity());
        Log.d("ddfstate", uris + "");

        imageContainer.removeAllViews();
        LinearLayout.LayoutParams sizeParameters = new LinearLayout.LayoutParams(MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);

        if(context != null){
            ((MainActivity) getActivity()).enableEdit();
            if (uris != null) {//activity may have been destroyed while downloading
                for (int i = 0; i < uris.size(); i++) {
                    Pair<ImageView, Uri> uriImagePair = new Pair<>(new ImageView(getActivity()), uris.get(i));
                    uriImagePair.first.setLayoutParams(sizeParameters);

                    imageContainer.addView(uriImagePair.first);
                    imageUris.add(uriImagePair);

                    BitmapEncoder.loadBitmapFromURI(uriImagePair.first, uriImagePair.second, MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
                    uriImagePair.first.setOnClickListener(ItemShowFragment.this);
                }
            }
        }


    }



    @Override
    public void onClick(View v) {
        if (v instanceof ImageView) {
            int index = -1;
            ArrayList<Uri> uris = new ArrayList<>();
            for (int i = 0; i < imageUris.size(); i++) {
                if (v == imageUris.get(i).first) {
                    index = i;
                }

                uris.add(imageUris.get(i).second);
            }

            if (index < 0) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Fragment destroyed");
    }

}
