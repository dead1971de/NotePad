package com.notepad.app;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotesStorage {
    private static final String PREFS_NAME = "notepad_prefs";
    private static final String KEY_NOTES = "notes_list";
    private final SharedPreferences prefs;
    private final Gson gson;

    public NotesStorage(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveNotes(List<Note> notes) {
        String json = gson.toJson(notes);
        prefs.edit().putString(KEY_NOTES, json).apply();
    }

    public List<Note> loadNotes() {
        String json = prefs.getString(KEY_NOTES, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Note>>(){}.getType();
        List<Note> notes = gson.fromJson(json, type);
        return notes != null ? notes : new ArrayList<>();
    }

    public void saveNote(Note note, List<Note> allNotes) {
        boolean found = false;
        for (int i = 0; i < allNotes.size(); i++) {
            if (allNotes.get(i).getId().equals(note.getId())) {
                allNotes.set(i, note);
                found = true;
                break;
            }
        }
        if (!found) {
            allNotes.add(note);
        }
        saveNotes(allNotes);
    }

    public void deleteNote(String noteId, List<Note> allNotes) {
        allNotes.removeIf(n -> n.getId().equals(noteId));
        saveNotes(allNotes);
    }
}
