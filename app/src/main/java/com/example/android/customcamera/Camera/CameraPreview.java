package com.example.android.customcamera.Camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.android.customcamera.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sohail on 11/23/15.
 */
public class CameraPreview implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private int displayWidth, displayHeight;
    private Context context;

    @SuppressWarnings("deprecation")
    private Camera camera;

    @SuppressWarnings("deprecation")
    private Camera.Parameters cameraParams;

    private Camera.Size previewSize;

    public CameraPreview(Context context, Camera camera) {
        this.context = context;
        this.camera = camera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;

        if (camera != null)
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (holder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop non-existent preview
        }

        // set preview size and make any resize, rotate or reformatting changes here
        cameraParams = camera.getParameters();

        if (previewSize != null) cameraParams.setPreviewSize(previewSize.width, previewSize.height);

        cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        camera.setParameters(cameraParams);

        camera.setDisplayOrientation(90);

        // start preview with new settings
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Empty. Takes care of releasing the camera preview in your activity.
        releaseCamera();
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public void setDisplayHeight(int displayHeight) {
        this.displayHeight = displayHeight;
    }

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
     * Called from PreviewSurfaceView to set touch focus.
     */
    public void onTouchFocus(Rect touchFocusRect) {

        List<Camera.Area> focusList = new ArrayList<>();
        Camera.Area focusArea = new Camera.Area(touchFocusRect, 1000);
        focusList.add(focusArea);

        Camera.Parameters cameraParams = camera.getParameters();
        cameraParams.setFocusAreas(focusList);
        cameraParams.setMeteringAreas(focusList);
        camera.setParameters(cameraParams);

        camera.autoFocus(autoFocusCallback);
    }

    /**
     * Autofocus Callback
     */
    @SuppressWarnings("deprecation")
    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) camera.cancelAutoFocus();
        }
    };

    /**
     * get instance of camera
     */
    public Camera getCamera() {
        return camera != null ? camera : null;
    }

    /**
     * set preview size from {@link PreviewSurfaceView#onMeasure(int, int)}
     */
    public void setPreviewSize(Camera.Size previewSize) {
        this.previewSize = previewSize;
    }
}


