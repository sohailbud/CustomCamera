package com.example.android.customcamera.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.example.android.customcamera.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        GalleryImagePickerFragment galleryImagePickerFragment = new GalleryImagePickerFragment();
        fragmentTransaction.add(R.id.gallery_image_picker_container, galleryImagePickerFragment);
        fragmentTransaction.commit();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }
}