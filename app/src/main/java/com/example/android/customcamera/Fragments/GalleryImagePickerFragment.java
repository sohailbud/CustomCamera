package com.example.android.customcamera.Fragments;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.android.customcamera.adapter.GalleryRecyclerViewAdapter;
import com.example.android.customcamera.R;

import java.util.ArrayList;
import java.util.List;


public class GalleryImagePickerFragment extends Fragment {

    private static int tabLayoutHeight;

    public GalleryImagePickerFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery_image_picker, container, false);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.photosGridItemContainer);

        final TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        ViewTreeObserver tabLayoutViewTreeObserver = tabLayout.getViewTreeObserver();
        tabLayoutViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tabLayoutHeight = tabLayout.getHeight();
                recyclerView.setPadding(0, 0, 0, tabLayoutHeight);

            }
        });

        final GalleryRecyclerViewAdapter galleryRecyclerViewAdapter = new GalleryRecyclerViewAdapter(
                getActivity(), getFullImages(getActivity()));


        ViewTreeObserver recyclerViewViewTreeObserver = recyclerView.getViewTreeObserver();
        recyclerViewViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                galleryRecyclerViewAdapter.setImageSize(recyclerView.getWidth() / 3);
            }
        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a grid layout manager
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        // specify an adapter
        recyclerView.setAdapter(galleryRecyclerViewAdapter);

        return view;
    }

    /**
     * gets images from phone and stores the paths in a list
     */
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




}