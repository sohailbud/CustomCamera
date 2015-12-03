package com.example.android.customcamera.Camera;

import android.content.Context;
import android.graphics.Camera;
import android.view.MotionEvent;
import android.view.SurfaceView;

/**
 * Created by Sohail on 12/3/15.
 */
public class PreviewSurfaceView extends SurfaceView {

    private CameraPreview cameraPreview;

    public PreviewSurfaceView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setListener(CameraPreview cameraPreview) {
        this.cameraPreview = cameraPreview;
    }
}
