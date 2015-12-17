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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Sohail on 12/10/15.
 */
public class ImageLoader {

    private Map<ImageView, String> imageViews = Collections.synchronizedMap(
            new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;

    private ImageCache imageCache;

    private Context context;

    String method;


    public ImageLoader(Context context) {
        executorService = Executors.newFixedThreadPool(5);
        this.context = context;
        imageCache = new ImageCache();
    }

    public void displayImage(String path, ImageView imageView) {
        imageViews.put(imageView, path);
        Bitmap bitmap = imageCache.getBitmapFromMemCache(path);
        if (bitmap != null) {
            Log.i("FROM", "FROM MEMORY");

            imageView.setImageBitmap(bitmap);
        } else {
            queuePhoto(path, imageView);
            imageView.setImageResource(R.drawable.empty_photo);
        }

    }

    private void queuePhoto(String path, ImageView imageView) {
        PhotoToLoad photoToLoad = new PhotoToLoad(path, imageView);
        executorService.submit(new PhotosLoader(photoToLoad));
    }

    private boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);

        return tag == null || !tag.equals(photoToLoad.path);
    }

    private Bitmap getBitmap(String path) {

        Bitmap bitmap = imageCache.getBitmapFromDiskCache(path, context);

        if (bitmap != null) {
            Log.i("FROM", "FROM DISK");
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 360, 360);
            return bitmap;
        }

        Bitmap bitmap2 = getBitmapFromLocalPath(path, 1);

        bitmap2 = ThumbnailUtils.extractThumbnail(bitmap2, 360, 360);
        imageCache.addBitmapToDiskCache(bitmap2, context, path);
        Log.i("FROM", "FROM NULL");

        return bitmap2;
    }

    /**
     * @param path
     * @param sampleSize 1 = 100%, 2 = 50%(1/2), 4 = 25%(1/4), ...
     * @return
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
     *
     */
    private class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        private PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
            photoToLoad.imageView.setTag(method);
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
            else photoToLoad.imageView.setImageResource(R.drawable.empty_photo);

        }
    }


}