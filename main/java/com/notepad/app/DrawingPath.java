package com.notepad.app;

import java.util.ArrayList;
import java.util.List;

public class DrawingPath {
    private List<float[]> points;
    private int color;
    private float strokeWidth;
    private boolean isEraser;
    private int paintAlpha = 255;

    public DrawingPath() {
        this.points = new ArrayList<>();
        this.color = 0xFF000000;
        this.strokeWidth = 8f;
        this.isEraser = false;
    }

    public DrawingPath(int color, float strokeWidth, boolean isEraser) {
        this.points = new ArrayList<>();
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.isEraser = isEraser;
    }

    public void addPoint(float x, float y) { points.add(new float[]{x, y}); }

    public List<float[]> getPoints() { return points; }
    public void setPoints(List<float[]> points) { this.points = points; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    public float getStrokeWidth() { return strokeWidth; }
    public void setStrokeWidth(float strokeWidth) { this.strokeWidth = strokeWidth; }
    public boolean isEraser() { return isEraser; }
    public void setEraser(boolean eraser) { isEraser = eraser; }
    public int getPaintAlpha() { return paintAlpha; }
    public void setPaintAlpha(int alpha) { this.paintAlpha = alpha; }
}
