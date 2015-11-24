package com.example.android.customcamera.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.android.customcamera.Camera.CameraPreview;
import com.example.android.customcamera.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private ViewTreeObserver viewTreeObserver;

    private Camera camera;
    private CameraPreview cameraPreview;

    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        // create instance of camera
        if (deviceHasCameraHardware(getActivity())) camera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity
        cameraPreview = new CameraPreview(getActivity(), camera);
        final FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);

        ImageView captureButton = (ImageView) view.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(captureButtonOnClickListener);

        viewTreeObserver = preview.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Log.i("WIDTH", String.valueOf(preview.getWidth()));
                    Log.i("HEIGHT", String.valueOf(preview.getHeight()));
                }
            });
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        releaseCamera();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
//                Toast.makeText(this, "Image saved to: \n" + data.getData(), Toast.LENGTH_LONG).show();

            } else if(resultCode == getActivity().RESULT_CANCELED) {
                // User canceled image capture

            } else {

            }
        }
    }

    /**
     * Create a file Uri for saving an image or video
     * @param type
     * @return
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     * @param type
     * @return
     */
    public File getOutputMediaFile(int type) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), this.getString(R.string.app_name));
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }

            // Create a media file name
            String timeCreated = SimpleDateFormat.getDateTimeInstance().format(new Date());
            File mediaFile;
            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_" + timeCreated + ".jpg");

                Log.i("PATH", mediaFile.getPath());

            } else if (type == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "VID_" + timeCreated + ".mp4");

            } else return null;

            return mediaFile;

        } else return null;
    }

    /**
     * Check if phone has a camera
     * @param context
     * @return
     */
    private boolean deviceHasCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Get an instance of camera object
     */
    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    private Camera.PictureCallback picture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) return;

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            }
        }
    };

    private View.OnClickListener captureButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            camera.takePicture(null, null, picture);
        }
    };

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }


}