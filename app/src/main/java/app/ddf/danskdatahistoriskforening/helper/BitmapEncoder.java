package app.ddf.danskdatahistoriskforening.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

public class BitmapEncoder {

    public static void loadBitmapFromURI(ImageView image, Uri uri, int width, int height){
        loadBitmapFromURI(image, uri, width, height, null);
    }

    public static void loadBitmapFromURI(ImageView image, Uri uri, int width, int height, ProgressBar progressBar){
        if(cancelPotentialWork(image, uri)) {
            BitmapWorkerTask task = new BitmapWorkerTask(image, uri, width, height);
            AsyncLoadingDrawable drawable = new AsyncLoadingDrawable(task);
            image.setImageDrawable(drawable);

            task.execute();
        }
    }

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
    private static class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private Uri uri = null;
        private int width;
        private int height;

        public Uri getUri(){
            return uri;
        }

        public BitmapWorkerTask(ImageView imageView, Uri uri, int width, int height) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);

            this.uri = uri;
            this.width = width;
            this.height = height;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Void... params){
            //simulate slow loading
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return decodeFile(uri, width, height);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(isCancelled()){
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask task = getTaskFromImage(imageView);

                if (task == this && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    //http://developer.android.com/training/displaying-bitmaps/process-bitmap.html#concurrency
    //http://android-developers.blogspot.dk/2010/07/multithreading-for-performance.html

    //temporary drawable which will associate an ImageView with the last task executed
    private static class AsyncLoadingDrawable extends ColorDrawable{
        private final WeakReference<BitmapWorkerTask> taskWeakReference;

        public AsyncLoadingDrawable(BitmapWorkerTask task){
            super(Color.BLACK);

            this.taskWeakReference = new WeakReference<>(task);
        }

        public BitmapWorkerTask getTask(){
            return taskWeakReference.get();
        }
    }

    //checks if task is already being peformed or if imageview is being reused and cancels old task if necessary
    private static boolean cancelPotentialWork(ImageView image, Uri uri){
        BitmapWorkerTask runningTask = getTaskFromImage(image);

        //imageView is associated with a running task
        if(runningTask != null){
            //running task differs from current task or
            // parameters have not been set (should not happen... but just in case)
            if(runningTask.getUri() == null || runningTask.getUri() != uri){
                runningTask.cancel(true);
            }
            //running task is doing identical work
            else{
                return false;
            }
        }

        return true;
    }

    private static BitmapWorkerTask getTaskFromImage(ImageView image){
        if(image != null) {
            final Drawable drawable = image.getDrawable();
            if(drawable instanceof AsyncLoadingDrawable){
                return ((AsyncLoadingDrawable) drawable).getTask();
            }
        }

        return null;
    }
}
