package com.example.android.customcamera.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.customcamera.R;
import com.example.android.customcamera.gallery.ImageLoader;

import java.util.List;

/**
 * Created by Sohail on 12/5/15.
 */
public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<String> data;
    private int imageSize;

    private ImageLoader imageLoader;

    public GalleryRecyclerViewAdapter(Context context, List<String> data) {
        inflater = LayoutInflater.from(context);
        this.imageLoader = new ImageLoader(context, imageSize);
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_gallery_image_picker_item, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        imageLoader.displayImage(data.get(position), holder.gridImage);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView gridImage;

        public ViewHolder(View itemView) {
            super(itemView);

            gridImage = (ImageView) itemView.findViewById(R.id.gridImage);
            gridImage.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), String.valueOf(v.getTag()), Toast.LENGTH_SHORT).show();
        }
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }
}

