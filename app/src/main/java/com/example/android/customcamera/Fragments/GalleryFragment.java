package com.example.android.customcamera.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


        GalleryImagePickerFragment galleryImagePickerFragment = new GalleryImagePickerFragment();
        galleryImagePickerFragment.setArguments(getArguments());

        getChildFragmentManager().beginTransaction().add(
                R.id.gallery_image_picker_container,
                galleryImagePickerFragment).commit();


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }
}