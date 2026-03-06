package com.notepad.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> notes;
    private NotesStorage storage;
    private TextView tvEmpty;
    private View rootLayout;
    private androidx.appcompat.widget.Toolbar toolbar;

    public static final String EXTRA_NOTE_ID = "note_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = new NotesStorage(this);
        notes = storage.loadNotes();

        rootLayout   = findViewById(R.id.rootLayout);
        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty      = findViewById(R.id.tvEmpty);
        toolbar      = findViewById(R.id.toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        setSupportActionBar(toolbar);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new NoteAdapter(notes,
            (note, pos) -> openNote(note.getId()),
            (note, pos) -> showNoteOptions(note, pos)
        );
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> createNewNote());

        // Theme toggle button
        findViewById(R.id.btnToggleTheme).setOnClickListener(v -> toggleTheme());

        applyTheme();
        updateEmptyState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notes = storage.loadNotes();
        adapter.updateNotes(notes);
        applyTheme();
        updateEmptyState();
    }

    private void applyTheme() {
        boolean dark = ThemeManager.isDarkTheme(this);
        rootLayout.setBackgroundColor(ThemeManager.getListBgColor(dark));
        toolbar.setBackgroundColor(ThemeManager.getToolbarColor(dark));
        tvEmpty.setTextColor(ThemeManager.getHintColor(dark));

        ImageButton btn = findViewById(R.id.btnToggleTheme);
        btn.setImageResource(dark
            ? android.R.drawable.ic_menu_day
            : android.R.drawable.ic_menu_week);
    }

    private void toggleTheme() {
        boolean dark = ThemeManager.isDarkTheme(this);
        ThemeManager.setDarkTheme(this, !dark);
        applyTheme();
        adapter.setDarkTheme(!dark);
        adapter.notifyDataSetChanged();
    }

    private void createNewNote() {
        EditText input = new EditText(this);
        input.setHint("Название заметки");
        input.setPadding(48, 32, 48, 16);

        new AlertDialog.Builder(this)
            .setTitle("✏️ Новая заметка")
            .setView(input)
            .setPositiveButton("Создать", (d, w) -> {
                String title = input.getText().toString().trim();
                if (title.isEmpty()) title = "Заметка " + (notes.size() + 1);
                Note newNote = new Note(UUID.randomUUID().toString(), title);
                storage.saveNote(newNote, notes);
                openNote(newNote.getId());
            })
            .setNegativeButton("Отмена", null)
            .show();
    }

    private void openNote(String noteId) {
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        startActivity(intent);
    }

    private void showNoteOptions(Note note, int position) {
        String[] options = {"🎨 Изменить цвет карточки", "🗑️ Удалить заметку"};
        new AlertDialog.Builder(this)
            .setTitle(note.getTitle())
            .setItems(options, (d, which) -> {
                if (which == 0) showCardColorPicker(note);
                else confirmDelete(note);
            })
            .show();
    }

    private void showCardColorPicker(Note note) {
        boolean dark = ThemeManager.isDarkTheme(this);
        int[] colors = dark ? ThemeManager.CARD_COLORS_DARK : ThemeManager.CARD_COLORS_LIGHT;

        int dp = (int) getResources().getDisplayMetrics().density;
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);

        AlertDialog[] dlgRef = new AlertDialog[1];
        for (int color : colors) {
            View cell = new View(this);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 56 * dp; lp.height = 56 * dp;
            lp.setMargins(6 * dp, 6 * dp, 6 * dp, 6 * dp);
            cell.setLayoutParams(lp);
            GradientDrawableHelper.setBorderDrawable(cell, color);
            cell.setOnClickListener(v -> {
                note.setCardColor(color);
                storage.saveNote(note, notes);
                adapter.updateNotes(notes);
                if (dlgRef[0] != null) dlgRef[0].dismiss();
            });
            grid.addView(cell);
        }

        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setPadding(16 * dp, 16 * dp, 16 * dp, 16 * dp);
        wrapper.setBackgroundColor(dark ? 0xFF2A2A2A : 0xFFFFFFFF);
        wrapper.addView(grid);

        dlgRef[0] = new AlertDialog.Builder(this)
            .setTitle("🎨 Цвет карточки")
            .setView(wrapper)
            .setNegativeButton("Отмена", null)
            .create();
        dlgRef[0].show();
    }

    private void confirmDelete(Note note) {
        new AlertDialog.Builder(this)
            .setTitle("Удалить заметку?")
            .setMessage("\"" + note.getTitle() + "\" будет удалена.")
            .setPositiveButton("Удалить", (d, w) -> {
                storage.deleteNote(note.getId(), notes);
                adapter.updateNotes(notes);
                updateEmptyState();
            })
            .setNegativeButton("Отмена", null).show();
    }

    private void updateEmptyState() {
        boolean empty = notes.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
