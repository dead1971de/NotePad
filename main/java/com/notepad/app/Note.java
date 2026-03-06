package com.notepad.app;

import java.util.ArrayList;
import java.util.List;

public class Note {
    private String id;
    private String title;
    private String textContent;
    private List<DrawingPath> drawingPaths;
    private List<TextHighlight> highlights;
    private long createdAt;
    private long updatedAt;
    private int cardColor;
    private int titleColor; // 0 = default black

    public Note(String id, String title) {
        this.id = id;
        this.title = title;
        this.textContent = "";
        this.drawingPaths = new ArrayList<>();
        this.highlights = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.cardColor = 0;
        this.titleColor = 0;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTextContent() { return textContent; }
    public void setTextContent(String t) { this.textContent = t; }
    public List<DrawingPath> getDrawingPaths() { return drawingPaths; }
    public void setDrawingPaths(List<DrawingPath> p) { this.drawingPaths = p; }
    public List<TextHighlight> getHighlights() { return highlights; }
    public void setHighlights(List<TextHighlight> h) { this.highlights = h; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long v) { this.createdAt = v; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long v) { this.updatedAt = v; }
    public int getCardColor() { return cardColor; }
    public void setCardColor(int c) { this.cardColor = c; }
    public int getTitleColor() { return titleColor; }
    public void setTitleColor(int c) { this.titleColor = c; }
}
