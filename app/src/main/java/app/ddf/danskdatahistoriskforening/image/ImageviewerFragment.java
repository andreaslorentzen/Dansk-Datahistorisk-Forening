package app.ddf.danskdatahistoriskforening.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.helper.BitmapEncoder;

public class ImageviewerFragment extends Fragment {
    private Uri imageUri;
    private int position;
    private int total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_imageviewer, container, false);

        ImageView image = (ImageView) layout.findViewById(R.id.imageviewfragment_imageview);
        image.setBackgroundColor(Color.BLACK);

        ProgressBar progressBar = (ProgressBar) layout.findViewById(R.id.imageviewfragment_progress);

        TextView header = (TextView) layout.findViewById(R.id.imageviewfragment_header);
        header.append(position + " af " + total);

        //use screen dimensions as approximation for imageView dimensions
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        BitmapEncoder.loadBitmapFromURI(image, imageUri, metrics.widthPixels, metrics.heightPixels, progressBar);

        return layout;
    }

    public void setImageUri(Uri uri){
        imageUri = uri;
    }

    public void setHeaderData(int position, int total){
        this.position = position;
        this.total = total;
    }
}
