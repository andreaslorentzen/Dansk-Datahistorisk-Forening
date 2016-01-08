package app.ddf.danskdatahistoriskforening.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

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
            //only either width or height needs to be required size as picture is not stretched to fill ImageView
            while ((halfHeight / inSampleSize) > reqHeight || (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    //set ImageView content from URI
    private static Bitmap decodeFile(Uri uri, int width, int height){
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

        Log.d("bitmap", "bitmap width: " + options.outWidth + " bitmap height: " + options.outHeight);

        return bitmap;
    }

    //http://developer.android.com/training/displaying-bitmaps/process-bitmap.html
    public static class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private Uri uri;
        private int width;
        private int height;

        public BitmapWorkerTask(ImageView imageView, Uri uri, int width, int height) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);

            this.uri = uri;
            this.width = width;
            this.height = height;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Void... params) {
            return decodeFile(uri, width, height);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}
