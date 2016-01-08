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
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    //set ImageView content from URI
    public static void decodeFile(ImageView image, Uri uri, int width, int height){
        //decode full scale dimensions of image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri.getPath(), options);

        Log.d("bitmap", "actual width: " + options.outWidth + " actual height: " + options.outHeight);

        //calculate samplesize
        options.inSampleSize = calculateInSampleSize(options, width, height);

        Log.d("bitmap", "samplesize: " + options.inSampleSize);

        //decode from file and insert into ImageView
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), options);
        image.setImageBitmap(bitmap);

        Log.d("bitmap", "bitmap width: " + options.outWidth + " bitmap height: " + options.outHeight);
    }
}
