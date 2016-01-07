package app.ddf.danskdatahistoriskforening;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageviewerFragment extends Fragment {
    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_imageviewer, container, false);

        ImageView image = (ImageView) layout.findViewById(R.id.imageviewfragment_imageview);
        BitmapFactory.Options options = new BitmapFactory.Options();

        //this may be too big memorywise, but the page adapter should destroy old fragments as needed
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(), options);
        image.setBackgroundColor(Color.BLACK);
        image.setImageBitmap(bitmap);

        return layout;
    }

    public void setImageUri(Uri uri){
        imageUri = uri;
    }
}
