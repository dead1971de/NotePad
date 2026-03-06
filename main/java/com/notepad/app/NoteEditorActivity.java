package com.notepad.app;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class NoteEditorActivity extends AppCompatActivity {

    private EditText etTitle, etContent;
    private DrawingView drawingView;
    private DrawingScrollView mainScroll;
    private LinearLayout toolbarText, toolbarDraw, rootLayout;
    private View topBar;
    private ImageButton btnToggleMode, btnEraser, btnUndo, btnClearDraw;
    private View drawColorIndicator, highlightColorIndicator, titleColorDot;
    private SeekBar seekBarAlpha, seekBarThickness;

    private NotesStorage storage;
    private List<Note> allNotes;
    private Note currentNote;
    private boolean isDrawingMode = false;
    private boolean isDark;

    private int selectedDrawColor;
    private int selectedHighlightColor = 0xCCFFFF00;
    private int selectedTitleColor     = Color.WHITE;
    private int highlightType          = 0;

    private static final int[] DRAW_COLORS = {
        0xFF000000, 0xFFFFFFFF, 0xFFD32F2F, 0xFFFF7043,
        0xFFFDD835, 0xFF43A047, 0xFF1565C0, 0xFF6A1B9A
    };
    private static final int[] HIGHLIGHT_COLORS = {
        0xCCFFFF00, 0xCCFFCC00, 0xCCFF9100, 0xCCFF4081,
        0xCCEA80FC, 0xCC80D8FF, 0xCC69F0AE, 0xCCCCFF90
    };
    private static final int[] TITLE_COLORS = {
        0xFFFFFFFF, 0xFFFFEB3B, 0xFFFF8A65, 0xFFFF80AB,
        0xFFEA80FC, 0xFF80D8FF, 0xFF69F0AE, 0xFFCCFF90,
        0xFFFFCC02, 0xFFFF5252, 0xFF40C4FF, 0xFFB9F6CA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        isDark = ThemeManager.isDarkTheme(this);
        selectedDrawColor = ThemeManager.getDefaultPenColor(isDark);

        storage  = new NotesStorage(this);
        allNotes = storage.loadNotes();
        String noteId = getIntent().getStringExtra(MainActivity.EXTRA_NOTE_ID);
        currentNote = findNote(noteId);

        initViews();
        applyTheme();
        loadNoteData();
        setupListeners();
    }

    private Note findNote(String id) {
        if (id == null) return null;
        for (Note n : allNotes) if (n.getId().equals(id)) return n;
        return null;
    }

    private void initViews() {
        rootLayout              = findViewById(R.id.rootLayout);
        topBar                  = findViewById(R.id.topBar);
        etTitle                 = findViewById(R.id.etTitle);
        titleColorDot           = findViewById(R.id.titleColorDot);
        etContent               = findViewById(R.id.etContent);
        mainScroll              = findViewById(R.id.mainScroll);
        drawingView             = findViewById(R.id.drawingView);
        toolbarText             = findViewById(R.id.toolbarText);
        toolbarDraw             = findViewById(R.id.toolbarDraw);
        btnToggleMode           = findViewById(R.id.btnToggleMode);
        btnEraser               = findViewById(R.id.btnEraser);
        btnUndo                 = findViewById(R.id.btnUndo);
        btnClearDraw            = findViewById(R.id.btnClearDraw);
        drawColorIndicator      = findViewById(R.id.drawColorIndicator);
        highlightColorIndicator = findViewById(R.id.highlightColorIndicator);
        seekBarAlpha            = findViewById(R.id.seekBarAlpha);
        seekBarThickness        = findViewById(R.id.seekBarThickness);
    }

    private void applyTheme() {
        rootLayout.setBackgroundColor(ThemeManager.getBgColor(isDark));
        topBar.setBackgroundColor(ThemeManager.getToolbarColor(isDark));
        toolbarText.setBackgroundColor(ThemeManager.getSubToolbarColor(isDark));
        toolbarDraw.setBackgroundColor(ThemeManager.getDrawSubToolbar(isDark));
        etContent.setTextColor(ThemeManager.getTextColor(isDark));
        etContent.setHintTextColor(ThemeManager.getHintColor(isDark));
        drawColorIndicator.setBackgroundColor(selectedDrawColor);
    }

    private void loadNoteData() {
        if (currentNote == null) return;
        etTitle.setText(currentNote.getTitle());
        int saved = currentNote.getTitleColor();
        if (saved != 0) {
            selectedTitleColor = saved;
            etTitle.setTextColor(saved);
            titleColorDot.setBackgroundColor(saved);
        }
        if (currentNote.getTextContent() != null) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(currentNote.getTextContent());
            if (currentNote.getHighlights() != null)
                for (TextHighlight h : currentNote.getHighlights()) applySpan(ssb, h);
            etContent.setText(ssb);
        }
        if (currentNote.getDrawingPaths() != null && !currentNote.getDrawingPaths().isEmpty())
            drawingView.post(() -> drawingView.setPaths(currentNote.getDrawingPaths()));
    }

    private void applySpan(SpannableStringBuilder ssb, TextHighlight h) {
        int len = ssb.length();
        int s = Math.min(h.getStart(), len), e = Math.min(h.getEnd(), len);
        if (s >= e) return;
        if (h.isUnderline())          ssb.setSpan(new UnderlineSpan(), s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        else if (h.isStrikethrough()) ssb.setSpan(new StrikethroughSpan(), s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        else                          ssb.setSpan(new BackgroundColorSpan(h.getColor()), s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setupListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> saveAndFinish());
        btnToggleMode.setOnClickListener(v -> toggleMode());
        titleColorDot.setOnClickListener(v -> showTitleColorPicker());

        btnEraser.setOnClickListener(v -> {
            boolean e = !drawingView.isEraserMode();
            drawingView.setEraserMode(e);
            btnEraser.setAlpha(e ? 1.0f : 0.5f);
        });
        btnUndo.setOnClickListener(v -> drawingView.undoLast());
        btnClearDraw.setOnClickListener(v ->
            new AlertDialog.Builder(this)
                .setMessage("Очистить всё рисование?")
                .setPositiveButton("Да", (d, w) -> drawingView.clearAll())
                .setNegativeButton("Нет", null).show());

        findViewById(R.id.btnColorPicker).setOnClickListener(v -> showColorPicker(false));
        findViewById(R.id.btnHighlightColorPicker).setOnClickListener(v -> showColorPicker(true));
        findViewById(R.id.btnHighlightType).setOnClickListener(v -> showHighlightTypeMenu());
        findViewById(R.id.btnApplyFormat).setOnClickListener(v -> applyFormatToSelection());
        findViewById(R.id.btnClearFormat).setOnClickListener(v -> clearFormatFromSelection());

        seekBarAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean f) { drawingView.setDrawAlpha(Math.max(0.05f, p / 100f)); }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
        seekBarThickness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean f) { drawingView.setStrokeWidth(Math.max(2f, p + 2f)); }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
    }

    private void toggleMode() {
        isDrawingMode = !isDrawingMode;
        drawingView.setDrawingEnabled(isDrawingMode);
        // Tell the scroll view whether to route touches to DrawingView
        mainScroll.setDrawingMode(isDrawingMode, drawingView);

        if (isDrawingMode) {
            toolbarText.setVisibility(View.GONE);
            toolbarDraw.setVisibility(View.VISIBLE);
            etContent.setFocusable(false);
            etContent.setFocusableInTouchMode(false);
            btnToggleMode.setColorFilter(0xFF4FC3F7);
            Toast.makeText(this, "✏️ Режим рисования", Toast.LENGTH_SHORT).show();
        } else {
            toolbarText.setVisibility(View.VISIBLE);
            toolbarDraw.setVisibility(View.GONE);
            etContent.setFocusable(true);
            etContent.setFocusableInTouchMode(true);
            btnToggleMode.clearColorFilter();
            Toast.makeText(this, "📝 Режим текста", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTitleColorPicker() {
        int dp = (int) getResources().getDisplayMetrics().density;
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        AlertDialog[] dlgRef = new AlertDialog[1];
        for (int color : TITLE_COLORS) {
            View cell = new View(this);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 56*dp; lp.height = 56*dp;
            lp.setMargins(6*dp, 6*dp, 6*dp, 6*dp);
            cell.setLayoutParams(lp);
            GradientDrawableHelper.setBorderDrawable(cell, color);
            cell.setOnClickListener(v -> {
                selectedTitleColor = color;
                etTitle.setTextColor(color);
                titleColorDot.setBackgroundColor(color);
                if (dlgRef[0] != null) dlgRef[0].dismiss();
            });
            grid.addView(cell);
        }
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setPadding(16*dp, 16*dp, 16*dp, 16*dp);
        wrapper.setBackgroundColor(0xFF222233);
        wrapper.addView(grid);
        dlgRef[0] = new AlertDialog.Builder(this)
            .setTitle("Цвет названия").setView(wrapper).setNegativeButton("Отмена", null).create();
        dlgRef[0].show();
    }

    private void showHighlightTypeMenu() {
        String[] types = {"🖊️ Маркер", "U  Подчёркивание", "S  Зачёркивание"};
        new AlertDialog.Builder(this)
            .setTitle("Тип форматирования")
            .setItems(types, (d, which) -> {
                highlightType = which;
                String[] labels = {"Маркер", "Подчёркивание", "Зачёркивание"};
                ((Button) findViewById(R.id.btnHighlightType)).setText(labels[which]);
            }).show();
    }

    private void applyFormatToSelection() {
        int start = etContent.getSelectionStart(), end = etContent.getSelectionEnd();
        if (start >= end) { Toast.makeText(this, "Выделите текст", Toast.LENGTH_SHORT).show(); return; }
        Spannable sp = (Spannable) etContent.getText();
        switch (highlightType) {
            case 0: sp.setSpan(new BackgroundColorSpan(selectedHighlightColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); break;
            case 1: sp.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); break;
            case 2: sp.setSpan(new StrikethroughSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); break;
        }
        if (currentNote.getHighlights() == null) currentNote.setHighlights(new ArrayList<>());
        TextHighlight h = new TextHighlight(start, end, selectedHighlightColor);
        h.setUnderline(highlightType == 1); h.setStrikethrough(highlightType == 2);
        currentNote.getHighlights().add(h);
    }

    private void clearFormatFromSelection() {
        Spannable sp = (Spannable) etContent.getText();
        int start = etContent.getSelectionStart(), end = etContent.getSelectionEnd();
        int s = (start == end) ? 0 : start, e = (start == end) ? sp.length() : end;
        for (BackgroundColorSpan x : sp.getSpans(s, e, BackgroundColorSpan.class)) sp.removeSpan(x);
        for (UnderlineSpan x      : sp.getSpans(s, e, UnderlineSpan.class))        sp.removeSpan(x);
        for (StrikethroughSpan x  : sp.getSpans(s, e, StrikethroughSpan.class))    sp.removeSpan(x);
        if (currentNote.getHighlights() != null)
            currentNote.getHighlights().removeIf(hh -> (start == end) || (hh.getStart() >= s && hh.getEnd() <= e));
    }

    private void showColorPicker(boolean isHighlight) {
        int[] colors = isHighlight ? HIGHLIGHT_COLORS : DRAW_COLORS;
        int dp = (int) getResources().getDisplayMetrics().density;
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        AlertDialog[] dlgRef = new AlertDialog[1];
        for (int color : colors) {
            View cell = new View(this);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 60*dp; lp.height = 60*dp;
            lp.setMargins(6*dp, 6*dp, 6*dp, 6*dp);
            cell.setLayoutParams(lp);
            GradientDrawableHelper.setBorderDrawable(cell, (color & 0xFFFFFF) | 0xFF000000);
            cell.setOnClickListener(v -> {
                if (isHighlight) {
                    selectedHighlightColor = color;
                    highlightColorIndicator.setBackgroundColor((color & 0xFFFFFF) | 0xFF000000);
                } else {
                    selectedDrawColor = color;
                    drawingView.setColor(color);
                    drawingView.setEraserMode(false);
                    btnEraser.setAlpha(0.5f);
                    drawColorIndicator.setBackgroundColor(color);
                }
                if (dlgRef[0] != null) dlgRef[0].dismiss();
            });
            grid.addView(cell);
        }
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setPadding(16*dp, 16*dp, 16*dp, 16*dp);
        wrapper.setBackgroundColor(isDark ? 0xFF2A2A2A : 0xFFFFFFFF);
        wrapper.addView(grid);
        dlgRef[0] = new AlertDialog.Builder(this)
            .setTitle(isHighlight ? "Цвет маркера" : "Цвет кисти")
            .setView(wrapper).setNegativeButton("Отмена", null).create();
        dlgRef[0].show();
    }

    private void saveAndFinish() {
        if (currentNote == null) { finish(); return; }
        String title = etTitle.getText().toString().trim();
        currentNote.setTitle(title.isEmpty() ? "Без названия" : title);
        currentNote.setTitleColor(selectedTitleColor);
        currentNote.setTextContent(etContent.getText().toString());
        currentNote.setDrawingPaths(drawingView.getPaths());
        currentNote.setUpdatedAt(System.currentTimeMillis());
        storage.saveNote(currentNote, allNotes);
        finish();
    }

    @Override
    public void onBackPressed() { saveAndFinish(); }
}
