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
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.android.customcamera.Camera.CameraPreview;
import com.example.android.customcamera.Camera.FocusView;
import com.example.android.customcamera.Camera.PreviewSurfaceView;
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

    private PreviewSurfaceView previewSurfaceView;
    private CameraPreview cameraPreview;
    private FocusView focusView;
    private Camera camera;

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

        previewSurfaceView = (PreviewSurfaceView) view.findViewById(R.id.preview_surface_view);
        SurfaceHolder surfaceHolder = previewSurfaceView.getHolder();

        // Create our Preview view
        cameraPreview = new CameraPreview(getActivity(), camera);

        // Install a SurfaceHolder.Callback so we get notified
        // when underlying surface gets created and destroyed
        surfaceHolder.addCallback(cameraPreview);
//        surfaceHolder.setType();

        previewSurfaceView.setListener(cameraPreview);
        focusView = (FocusView) view.findViewById(R.id.drawing_focus_view);
        previewSurfaceView.setDrawingFocusView(focusView);

        ImageView captureButton = (ImageView) view.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(captureButtonOnClickListener);

        viewTreeObserver = previewSurfaceView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    cameraPreview.setDisplayWidth(previewSurfaceView.getWidth());
                    cameraPreview.setDisplayHeight(previewSurfaceView.getHeight());
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

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // User canceled image capture

            } else {

            }
        }
    }

    /**
     * Create a file Uri for saving an image or video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
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

    /**
     * Releases camera
     */
    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    /**
     * Get an instance of camera object
     */
    @SuppressWarnings("deprecation")
    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * Check if phone has a camera
     */
    private boolean deviceHasCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }



}