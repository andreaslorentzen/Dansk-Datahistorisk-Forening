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
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.domain.Logic;
import app.ddf.danskdatahistoriskforening.domain.UserSelection;
import app.ddf.danskdatahistoriskforening.helper.BitmapEncoder;
import app.ddf.danskdatahistoriskforening.image.ImageviewerSimpleActivity;


public class ShowItemFragment extends Fragment implements View.OnClickListener, UserSelection.OnSelectItemListener {
    //TODO calculate acceptable thumbnail dimensions based on screensize or available space

    private TextView itemheadlineView;
    private TextView itemdescriptionView;
    private TextView receivedView;
    private TextView datingFromView;
    private TextView datingToView;
    private TextView donatorView;
    private TextView producerView;

    private LinearLayout contentWrapper;
    private ProgressBar progressBar;

    private LinearLayout imageContainer;
    private ArrayList<Pair<ImageView, Uri>> imageUris;

    public ShowItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_show_item, container, false);

        itemheadlineView = (TextView) layout.findViewById(R.id.itemheadline);
        itemdescriptionView = (TextView) layout.findViewById(R.id.itemdescription);
        receivedView = (TextView) layout.findViewById(R.id.received);
        datingFromView = (TextView) layout.findViewById(R.id.datingFrom);
        datingToView = (TextView) layout.findViewById(R.id.datingTo);
        donatorView = (TextView) layout.findViewById(R.id.donator);
        producerView = (TextView) layout.findViewById(R.id.producer);
        imageContainer = (LinearLayout) layout.findViewById(R.id.imageContainer);

        contentWrapper = (LinearLayout) layout.findViewById(R.id.item_details_wrapper);
        progressBar = (ProgressBar) layout.findViewById(R.id.item_details_progressbar);
        //    ((MainActivity) getActivity()).updateSearchVisibility();

        Logic.instance.userSelection.selectItemListeners.add(this);
        OnSelectItem();

        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logic.instance.userSelection.selectItemListeners.remove(this);
    }

    @Override
    public void OnSelectItem() {
        Item item = Logic.instance.userSelection.getSelectedItem();
        if (item == null) {
            ((MainActivity) getActivity()).disableEdit();
            progressBar.setVisibility(View.VISIBLE);
            contentWrapper.setVisibility(View.GONE);
            return;
        }

        ((MainActivity) getActivity()).enableEdit();

        // felterne udfyld felterne
        itemheadlineView.setText(item.getItemHeadline());
        // TODO handle lyd
        itemdescriptionView.setText(item.getItemDescription());
        receivedView.setText(((item.getItemRecievedAsString() == null) ? null : item.getItemRecievedAsString()));
        datingFromView.setText(((item.getItemDatingFromAsString() == null) ? null : item.getItemDatingFromAsString()));
        datingToView.setText(((item.getItemDatingToAsString() == null) ? null : item.getItemDatingToAsString()));

        donatorView.setText(item.getDonator());
        producerView.setText(item.getProducer());

        progressBar.setVisibility(View.GONE);
        contentWrapper.setVisibility(View.VISIBLE);

        //create picture thumbnails
        ArrayList<Uri> uris = item.getPictures();

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
                uriImagePair.first.setOnClickListener(ShowItemFragment.this);
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
