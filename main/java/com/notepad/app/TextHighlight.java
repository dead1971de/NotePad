package com.notepad.app;

public class TextHighlight {
    private int start;
    private int end;
    private int color;
    private boolean isUnderline;
    private boolean isStrikethrough;

    public TextHighlight(int start, int end, int color) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.isUnderline = false;
        this.isStrikethrough = false;
    }

    public int getStart() { return start; }
    public void setStart(int start) { this.start = start; }
    public int getEnd() { return end; }
    public void setEnd(int end) { this.end = end; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    public boolean isUnderline() { return isUnderline; }
    public void setUnderline(boolean underline) { isUnderline = underline; }
    public boolean isStrikethrough() { return isStrikethrough; }
    public void setStrikethrough(boolean strikethrough) { isStrikethrough = strikethrough; }
}
