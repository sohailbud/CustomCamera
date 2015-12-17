package com.example.android.customcamera.Fragments;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.android.customcamera.Adapter.GalleryRecyclerViewAdapter;
import com.example.android.customcamera.R;

import java.util.ArrayList;
import java.util.List;


public class GalleryImagePickerFragment extends Fragment {

    public GalleryImagePickerFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery_image_picker, container, false);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.photosGridItemContainer);

        ViewTreeObserver viewTreeObserver = recyclerView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float width = recyclerView.getWidth() / 3;
                Log.i("WIDTH", Float.toString(width));
            }
        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a grid layout manager
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        RecyclerView.Adapter adapter = new GalleryRecyclerViewAdapter(
                getActivity(), getFullImages(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }


    public List<String> getFullImages(Context context) {

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String order = MediaStore.Images.Media.DATE_TAKEN + " ASC";

        CursorLoader imagesCursorLoader = new CursorLoader(
                context, uri, null, null, null, order);
        Cursor imageCursor = imagesCursorLoader.loadInBackground();

        List<String> imagesData = new ArrayList<>();
        while (imageCursor.moveToNext()) {
            int dataIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            String data = imageCursor.getString(dataIndex);
            imagesData.add(data);
        }
        imageCursor.close();

        return imagesData;

    }

    public List<String> getThumbnails(Context context) {

        Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        CursorLoader thumbnailsCursorLoader = new CursorLoader(context, uri, null, null, null, null);

        Cursor cursor = thumbnailsCursorLoader.loadInBackground();

        List<String> thumbnailsData = new ArrayList<>();
        while (cursor.moveToNext()) {
            int dataIndex = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
            String data = cursor.getString(dataIndex);
            thumbnailsData.add(data);
        }
        cursor.close();

        return thumbnailsData;
    }
}