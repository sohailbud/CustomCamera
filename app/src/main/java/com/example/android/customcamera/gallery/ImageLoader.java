package com.example.android.customcamera.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.example.android.customcamera.R;
import com.example.android.customcamera.Util.ImageCache;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Sohail on 12/10/15.
 * Memory cache code borrowed from a stack overflow entry
 * http://stackoverflow.com/questions/541966/lazy-load-of-images-in-listview
 */
public class ImageLoader {

    // stores the imageviews being recycled
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(
            new WeakHashMap<ImageView, String>());

    private ExecutorService executorService;

    private Context context;

    // caching class handles all disk and memory caching
    private ImageCache imageCache;

    // empty bitmap to set while actual bitmap loads
    private Bitmap emptyBitmap;

    public ImageLoader(Context context) {
        executorService = Executors.newFixedThreadPool(5);
        this.context = context;
        imageCache = new ImageCache();

        // get empty bitmap and resize
        emptyBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_photo);
        emptyBitmap = ThumbnailUtils.extractThumbnail(emptyBitmap, 360, 360);
    }

    /**
     * recycles the image view and if available displays the image from memory
     */
    public void displayImage(String path, ImageView imageView) {
        // store the image view to recycle
        imageViews.put(imageView, path);

        // try to get bitmap from memory cache
        Bitmap bitmap = imageCache.getBitmapFromMemCache(path);
        if (bitmap != null) {
            Log.i("FROM", "FROM MEMORY");

            // remove the placeholder bitmap and set the actual bitmap from memory
            imageView.setImageBitmap(null);
            imageView.setImageBitmap(bitmap);

        } else {
            // if bitmap was not in memory cache,
            queuePhoto(path, imageView);

            // set the placeholder image
            imageView.setImageBitmap(emptyBitmap);
        }
    }

    /**
     * simple creates the photoToLoad objects and adds it to queue
     */
    private void queuePhoto(String path, ImageView imageView) {
        PhotoToLoad photoToLoad = new PhotoToLoad(path, imageView);
        executorService.submit(new PhotosLoader(photoToLoad));
    }

    /**
     * checks if imageView is being reused
     */
    private boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);

        return tag == null || !tag.equals(photoToLoad.path);
    }

    /**
     * checks to see if bitmap is in disk, if not then gets the bitmap and resizes it
     */
    private Bitmap getBitmap(String path) {

        // get bitmap from disk, if empty then proceed further
        Bitmap bitmap = imageCache.getBitmapFromDiskCache(path, context);
        if (bitmap != null) {
            Log.i("FROM", "FROM DISK");
            return bitmap;
        }

        // create options and calculate sample size with provided options
        BitmapFactory.Options options = new BitmapFactory.Options();

        // Setting the inJustDecodeBounds property to true while decoding avoids memory allocation
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);
        int sampleSize = calculateInSampleSize(options, 360, 360);

        // get bitmap using the sample size calculated
        Bitmap bitmap2 = getBitmapFromLocalPath(path, sampleSize);

        // resize the bitmap
        bitmap2 = ThumbnailUtils.extractThumbnail(bitmap2, 360, 360);

        // adds bitmap to disk cache
        imageCache.addBitmapToDiskCache(bitmap2, context, path);

        Log.i("FROM", "FROM NULL");

        return bitmap2;
    }

    /**
     * gets bitmap from local path
     * @param sampleSize 1 = 100%, 2 = 50%(1/2), 4 = 25%(1/4), ...
     */
    private Bitmap getBitmapFromLocalPath(String path, int sampleSize) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;
            return BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            //  Logger.e(e.toString());
        }
        return null;
    }

    /**
     * Task for the queue
     * simple class that holds the path to bitmap and imageview
     */
    private class PhotoToLoad {
        private String path;
        private ImageView imageView;

        private PhotoToLoad(String path, ImageView imageView) {
            this.path = path;
            this.imageView = imageView;
        }
    }

    /**
     * gets the photo from diskcache if available or resizes it from original photo
     * calls on bitmapDisplayer to load the picture
     */
    private class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        private PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad)) return;

            Bitmap bitmap = getBitmap(photoToLoad.path);
            imageCache.addBitmapToMemCache(photoToLoad.path, bitmap);

            BitmapDisplayer bitmapDisplayer = new BitmapDisplayer(bitmap, photoToLoad);
            AppCompatActivity activity = (AppCompatActivity) photoToLoad.imageView.getContext();
            activity.runOnUiThread(bitmapDisplayer);
        }
    }

    /**
     * used to display bitmap in the UI thread
     */
    private class BitmapDisplayer implements Runnable {

        private Bitmap bitmap;
        private PhotoToLoad photoToLoad;

        private BitmapDisplayer(Bitmap bitmap, PhotoToLoad photoToLoad) {
            this.bitmap = bitmap;
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad)) return;
            if (bitmap != null) photoToLoad.imageView.setImageBitmap(bitmap);
            else photoToLoad.imageView.setImageBitmap(emptyBitmap);

        }
    }

    /**
     * calculates appropriate sample size of the picture based on current dimensions
     * method from google docs
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


}