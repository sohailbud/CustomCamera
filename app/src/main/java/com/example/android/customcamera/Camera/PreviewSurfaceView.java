package com.example.android.customcamera.Camera;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.List;

/**
 * Created by Sohail on 12/3/15.
 */
public class PreviewSurfaceView extends SurfaceView {

    private CameraPreview cameraPreview;

    @SuppressWarnings("deprecation")
    private List<android.hardware.Camera.Size> supportedPreviewSizes;

    @SuppressWarnings("deprecation")
    private Camera.Size previewSize;

    private boolean listenerSet = false;
    private boolean focusViewSet = false;
    private FocusView focusView;

    public PreviewSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

//        final int width = MeasureSpec.getSize(widthMeasureSpec);
//        final int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);

        Camera camera = cameraPreview.getCamera();
        if (camera != null)
            supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();

        if (supportedPreviewSizes != null)
            previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);

        cameraPreview.setPreviewSize(previewSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!listenerSet) return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Rect[] focusRect = calculateFocusArea(event);

            cameraPreview.onTouchFocus(focusRect[1]);
            if (focusViewSet) {
                focusView.onTouch(true, focusRect[0]);
                focusView.invalidate();

                // Remove the square after sometime
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        focusView.onTouch(false, new Rect(0, 0, 0, 0));
                        focusView.invalidate();
                    }
                }, 1000);
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
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

    /**
     * set CameraPreview instance for touch focus.
     */
    public void setListener(CameraPreview cameraPreview) {
        this.cameraPreview = cameraPreview;
        listenerSet = true;
    }

    /**
     * set DrawingView instance for touch focus indication.
     */
    public void setDrawingFocusView(FocusView focusView) {
        this.focusView = focusView;
        focusViewSet = true;
    }

    /**
     * /Convert from View's width and height to +/- 1000
     */
    private Rect[] calculateFocusArea(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        Rect focusRect = new Rect(
                (int) (x - 100),
                (int) (y - 100),
                (int) (x + 100),
                (int) (y + 100));

        Rect targetFocusRect = new Rect(
                focusRect.left * 2000 / cameraPreview.getDisplayWidth() - 1000,
                focusRect.top * 2000 / cameraPreview.getDisplayHeight() - 1000,
                focusRect.right * 2000 / cameraPreview.getDisplayWidth() - 1000,
                focusRect.bottom * 2000 / cameraPreview.getDisplayHeight() - 1000);

        return new Rect[]{focusRect, targetFocusRect};
    }
}