package app.ddf.danskdatahistoriskforening.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import app.ddf.danskdatahistoriskforening.App;
import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.helper.BitmapEncoder;
import app.ddf.danskdatahistoriskforening.image.ImageviewerSimpleActivity;


public class ItemShowFragment extends Fragment implements View.OnClickListener, Model.OnCurrentItemChangeListener {
    //TODO calculate acceptable thumbnail dimensions based on screensize or available space

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

    public ItemShowFragment() {
        // Required empty public constructor
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
    public void onResume() {
        super.onResume();
        Model.getInstance().setOnCurrentItemChangeListener(this);
        onCurrentItemChange(Model.getInstance().getCurrentItem());
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
            progressBar.setVisibility(View.VISIBLE);
            contentWrapper.setVisibility(View.GONE);
            return;
        }

        ((MainActivity) getActivity()).enableEdit();

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

        imageContainer.removeAllViews();
        imageUris = new ArrayList<>();

        if (uris != null) {//activity may have been destroyed while downloading

            LinearLayout.LayoutParams sizeParameters = new LinearLayout.LayoutParams(App.MAX_THUMBNAIL_WIDTH, App.MAX_THUMBNAIL_HEIGHT);

            for (int i = 0; i < uris.size(); i++) {
                Pair<ImageView, Uri> uriImagePair = new Pair<>(new ImageView(getActivity()), uris.get(i));
                uriImagePair.first.setLayoutParams(sizeParameters);

                imageContainer.addView(uriImagePair.first);
                imageUris.add(uriImagePair);

                BitmapEncoder.loadBitmapFromURI(uriImagePair.first, uriImagePair.second, App.MAX_THUMBNAIL_WIDTH, App.MAX_THUMBNAIL_HEIGHT);
                uriImagePair.first.setOnClickListener(ItemShowFragment.this);
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
