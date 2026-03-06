package com.notepad.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class DrawingScrollView extends ScrollView {

    private boolean drawingMode = false;
    private DrawingView drawingView;

    public DrawingScrollView(Context ctx)                     { super(ctx); }
    public DrawingScrollView(Context ctx, AttributeSet attrs) { super(ctx, attrs); }

    public void setDrawingMode(boolean drawing, DrawingView dv) {
        this.drawingMode = drawing;
        this.drawingView = dv;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // In drawing mode: don't intercept — let events reach DrawingView directly
        if (drawingMode) return false;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (drawingMode && drawingView != null) {
            // Pass event to DrawingView.
            // NOTE: coordinates here are relative to this ScrollView.
            // DrawingView is a child of FrameLayout which is a child of this ScrollView.
            // The FrameLayout's top = 0, DrawingView's top = 0.
            // The ScrollView's canvas is already translated by -scrollY when drawing children,
            // but touch events are NOT translated — they come in as screen coords relative to view.
            // We need to add scrollY so that y=0 means top of document, not top of visible area.
            MotionEvent docEv = MotionEvent.obtain(ev);
            docEv.offsetLocation(0, getScrollY());
            boolean result = drawingView.onTouchEvent(docEv);
            docEv.recycle();
            return result;
        }
        return super.onTouchEvent(ev);
    }
}
