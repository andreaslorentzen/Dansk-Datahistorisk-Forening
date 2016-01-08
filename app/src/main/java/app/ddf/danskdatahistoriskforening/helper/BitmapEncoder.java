package app.ddf.danskdatahistoriskforening.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class BitmapEncoder {

    //http://developer.android.com/training/displaying-bitmaps/load-bitmap.html#load-bitmap
    //calculate desired scaling of bitmap
    private static int calculateInSampleSize(){

        return 4;
    }

    //set ImageView content from URI
    public static void decodeFile(ImageView image, Uri uri, int width, int height){
        //decode full scale dimensions of image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri.getPath(), options);

        Log.d("bitmap", "width: " + width + " height: " + height);

        //calculate samplesize
        options.inSampleSize = calculateInSampleSize();

        //decode from file and insert into ImageView
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), options);
        image.setImageBitmap(bitmap);
    }
}
