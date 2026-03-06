package com.notepad.app;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.ScrollView;
import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private final List<DrawingPath> paths = new ArrayList<>();
    private DrawingPath currentPath;
    private boolean drawingEnabled = false;

    private int   currentColor  = Color.BLACK;
    private float currentStroke = 8f;
    private float currentAlpha  = 1.0f;
    private boolean eraserMode  = false;

    private final Paint paint = new Paint();

    public DrawingView(Context ctx)                     { super(ctx);       init(); }
    public DrawingView(Context ctx, AttributeSet attrs) { super(ctx, attrs); init(); }

    private void init() {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    /** Returns current scroll offset of parent ScrollView */
    private int getParentScrollY() {
        ViewParent p = getParent();
        while (p != null) {
            if (p instanceof ScrollView) return ((ScrollView) p).getScrollY();
            p = p.getParent();
        }
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (DrawingPath dp : paths)   renderPath(canvas, dp);
        if (currentPath != null)       renderPath(canvas, currentPath);
    }

    private void renderPath(Canvas canvas, DrawingPath dp) {
        List<float[]> pts = dp.getPoints();
        if (pts.size() < 2) return;
        Paint p = new Paint(paint);
        if (dp.isEraser()) {
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            p.setStrokeWidth(dp.getStrokeWidth());
        } else {
            p.setColor(dp.getColor());
            p.setStrokeWidth(dp.getStrokeWidth());
            p.setAlpha(dp.getPaintAlpha());
        }
        Path path = new Path();
        path.moveTo(pts.get(0)[0], pts.get(0)[1]);
        for (int i = 1; i < pts.size() - 1; i++) {
            float mx = (pts.get(i)[0] + pts.get(i+1)[0]) / 2f;
            float my = (pts.get(i)[1] + pts.get(i+1)[1]) / 2f;
            path.quadTo(pts.get(i)[0], pts.get(i)[1], mx, my);
        }
        float[] last = pts.get(pts.size()-1);
        path.lineTo(last[0], last[1]);
        canvas.drawPath(path, p);
        p.setXfermode(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!drawingEnabled) return false;

        // x,y already in DrawingView's own coordinate space
        // DrawingView is a sibling of EditText inside FrameLayout inside ScrollView
        // so getTop() of DrawingView == 0, and the FrameLayout scrolls with ScrollView
        // → coordinates are already document-relative. No offset needed.
        float x = ev.getX();
        float y = ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new DrawingPath(
                    currentColor,
                    eraserMode ? currentStroke * 5f : currentStroke,
                    eraserMode);
                currentPath.setPaintAlpha((int)(currentAlpha * 255));
                currentPath.addPoint(x, y);
                currentPath.addPoint(x + 0.1f, y + 0.1f);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (currentPath != null) { currentPath.addPoint(x, y); invalidate(); }
                return true;
            case MotionEvent.ACTION_UP:
                if (currentPath != null) {
                    currentPath.addPoint(x, y);
                    paths.add(currentPath);
                    currentPath = null;
                    invalidate();
                }
                return true;
        }
        return false;
    }

    public void setDrawingEnabled(boolean e) { drawingEnabled = e; }
    public void setColor(int c)              { currentColor = c; eraserMode = false; }
    public void setStrokeWidth(float w)      { currentStroke = w; }
    public void setDrawAlpha(float a)        { currentAlpha = Math.max(0.05f, Math.min(1f, a)); }
    public void setEraserMode(boolean e)     { eraserMode = e; }
    public boolean isEraserMode()            { return eraserMode; }
    public void clearAll()                   { paths.clear(); currentPath = null; invalidate(); }
    public void undoLast()                   { if (!paths.isEmpty()) { paths.remove(paths.size()-1); invalidate(); } }
    public List<DrawingPath> getPaths()      { return new ArrayList<>(paths); }
    public void setPaths(List<DrawingPath> p){ paths.clear(); if (p != null) paths.addAll(p); invalidate(); }
}
