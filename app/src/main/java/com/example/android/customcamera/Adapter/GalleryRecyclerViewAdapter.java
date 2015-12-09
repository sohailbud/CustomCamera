package com.example.android.customcamera.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.customcamera.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sohail on 12/5/15.
 */
public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<String> data;
    private HashMap<Integer, String> tasksLog = new HashMap<>();

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
        LoadSetImageTask loadSetImageTask = new LoadSetImageTask();
        tasksLog.put(imageView.hashCode(), data.get(position));

        loadSetImageTask.execute(new Object[]{imageView, data.get(position)});
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView gridImage;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            gridImage = (ImageView) itemView.findViewById(R.id.gridImage);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), String.valueOf(v.getTag()), Toast.LENGTH_SHORT).show();
        }
    }

    private class LoadSetImageTask extends AsyncTask<Object[], Void, Object[]> {

        @Override
        protected Object[] doInBackground(Object[]... params) {
            ImageView imageView = (ImageView) params[0][0];
            String path = (String) params[0][1];

            Bitmap bitmap = getBitmapFromLocalPath(path, 1);

            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 360, 360);

            return new Object[]{imageView, bitmap, path};
        }

        @Override
        protected void onPostExecute(Object[] results) {
            super.onPostExecute(results);

            ImageView imageView = (ImageView) results[0];
            Bitmap bitmap = (Bitmap) results[1];
            String path = (String) results[2];

//            if (tasksLog.get(imageView.hashCode()).equals(path)) {
                imageView.setImageBitmap(bitmap);
                imageView.setTag(path);
//                tasksLog.remove(imageView.hashCode());
//            } else {
//                Log.i("MATCH", "0");
//            }
        }

        /**
         * @param path
         * @param sampleSize 1 = 100%, 2 = 50%(1/2), 4 = 25%(1/4), ...
         * @return
         */
        public Bitmap getBitmapFromLocalPath(String path, int sampleSize) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = sampleSize;
                return BitmapFactory.decodeFile(path, options);
            } catch (Exception e) {
                //  Logger.e(e.toString());
            }
            return null;
        }
    }
}
