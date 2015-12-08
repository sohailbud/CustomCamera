package com.example.android.customcamera.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.customcamera.R;

import java.io.File;
import java.util.List;

/**
 * Created by Sohail on 12/5/15.
 */
public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<String> data;
    private float pictureWidth;

    public GalleryRecyclerViewAdapter(Context context, List<String> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_gallery_image_picker_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView imageView = holder.gridImage;
        new LoadSetImageTask().execute(new Object[]{imageView, data.get(position)});
//        holder.gridImage.setImageURI(Uri.fromFile(new File(data.get(position))));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView gridImage;

        public ViewHolder(View itemView) {
            super(itemView);

            gridImage = (ImageView) itemView.findViewById(R.id.gridImage);
        }
    }

    private class LoadSetImageTask extends AsyncTask<Object[], Void, Object[] > {

        @Override
        protected Object[] doInBackground(Object[]... params) {
            ImageView imageView = (ImageView) params[0][0];
            String path = (String) params[0][1];

            Bitmap bitmap = getBitmapFromLocalPath(path, 1);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 360, 360);

            return new Object[] {imageView, bitmap};
        }

        @Override
        protected void onPostExecute(Object[] results) {
            super.onPostExecute(results);

            ImageView imageView = (ImageView) results[0];
            Bitmap bitmap = (Bitmap) results[1];

            imageView.setImageBitmap(bitmap);
        }

        /**
         *
         * @param path
         * @param sampleSize 1 = 100%, 2 = 50%(1/2), 4 = 25%(1/4), ...
         * @return
         */
        public Bitmap getBitmapFromLocalPath(String path, int sampleSize) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = sampleSize;
                return BitmapFactory.decodeFile(path, options);
            } catch(Exception e) {
                //  Logger.e(e.toString());
            }

            return null;
        }
    }
}
