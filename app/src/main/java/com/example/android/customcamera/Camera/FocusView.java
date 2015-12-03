package com.example.android.customcamera.Camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Sohail on 12/2/15.
 */
public class FocusView extends View {

    private Rect touchAreaRect;
    private Paint paint;
    private boolean isTouched = false;

    public FocusView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isTouched) canvas.drawRect(touchAreaRect.left, touchAreaRect.top,
                touchAreaRect.right, touchAreaRect.bottom, paint);
    }

    public void onTouch(Boolean isTouched, Rect touchAreaRect) {
        this.isTouched = isTouched;
        this.touchAreaRect = touchAreaRect;
    }
}
