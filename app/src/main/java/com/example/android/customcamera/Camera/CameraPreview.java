package com.example.android.customcamera.Camera;

import android.content.Context;
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
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private int displayWidth;
    private int displayHeight;
    private Canvas canvas;


    Camera.Size previewSize;
    List<Camera.Size> supportedPreviewSizes;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.camera = camera;

        supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();

        // Install a SurfaceHolder.Callback so we get notified
        // when underlying surface gets created and destroyed
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        canvas = holder.lockCanvas();

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
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        camera.setParameters(parameters);

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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (supportedPreviewSizes != null) {
            previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    public void setDisplayHeight(int displayHeight) {
        this.displayHeight = displayHeight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        float touchMajor = event.getTouchMajor();
        float touchMinor = event.getTouchMinor();

        Rect rect = calculateFocusArea(x, y, touchMajor, touchMinor);
        focusOnTouch(event, rect);

        return true;
    }

    /**
     * /Convert from View's width and height to +/- 1000
     */
    private Rect calculateFocusArea(float x, float y, float touchMajor, float touchMinor) {
        Rect focusRect = new Rect(
                (int)(x - touchMajor/2),
                (int)(y - touchMinor/2),
                (int)(x + touchMajor/2),
                (int)(y + touchMinor/2));

        Rect targetFocusRect = new Rect(
                focusRect.left * 2000 / displayWidth - 1000,
                focusRect.top * 2000 / displayHeight - 1000,
                focusRect.right * 2000 / displayWidth - 1000,
                focusRect.bottom * 2000 / displayHeight - 1000);

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawRect(targetFocusRect, paint);
        surfaceHolder.unlockCanvasAndPost(canvas);

        return targetFocusRect;
    }

    private void focusOnTouch(MotionEvent event, Rect rect) {

        if (camera != null) {
            final List<Camera.Area> focusList = new ArrayList<>();
            Camera.Area focusArea = new Camera.Area(rect, 1000);
            focusList.add(focusArea);

            Camera.Parameters cameraParameters = camera.getParameters();
            cameraParameters.setFocusAreas(focusList);
            cameraParameters.setMeteringAreas(focusList);
            camera.setParameters(cameraParameters);
        }



    }






}
