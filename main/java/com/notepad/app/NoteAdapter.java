package com.notepad.app;

import android.graphics.Color;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.*;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private final OnNoteClickListener clickListener;
    private final OnNoteLongClickListener longClickListener;
    private boolean darkTheme = false;

    public interface OnNoteClickListener    { void onNoteClick(Note note, int position); }
    public interface OnNoteLongClickListener { void onNoteLongClick(Note note, int position); }

    public NoteAdapter(List<Note> notes, OnNoteClickListener c, OnNoteLongClickListener lc) {
        this.notes = notes; this.clickListener = c; this.longClickListener = lc;
    }

    public void setDarkTheme(boolean dark) { this.darkTheme = dark; }

    @NonNull @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder h, int pos) { h.bind(notes.get(pos), pos); }

    @Override public int getItemCount() { return notes.size(); }

    public void updateNotes(List<Note> n) { this.notes = n; notifyDataSetChanged(); }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle, tvPreview, tvDate;
        View accentBar;

        NoteViewHolder(View v) {
            super(v);
            cardView  = v.findViewById(R.id.cardView);
            tvTitle   = v.findViewById(R.id.tvNoteTitle);
            tvPreview = v.findViewById(R.id.tvNotePreview);
            tvDate    = v.findViewById(R.id.tvNoteDate);
            accentBar = v.findViewById(R.id.accentBar);
        }

        void bind(Note note, int pos) {
            String title = note.getTitle().isEmpty() ? "Без названия" : note.getTitle();
            tvTitle.setText(title);

            String preview = note.getTextContent();
            if (preview == null || preview.isEmpty())
                preview = (note.getDrawingPaths() != null && !note.getDrawingPaths().isEmpty())
                    ? "✏️ Есть рисунок" : "Пустая заметка";
            tvPreview.setText(preview.length() > 70 ? preview.substring(0, 70) + "…" : preview);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm", new Locale("ru"));
            tvDate.setText(sdf.format(new Date(note.getUpdatedAt())));

            // Card background
            int[] palette = darkTheme ? ThemeManager.CARD_COLORS_DARK : ThemeManager.CARD_COLORS_LIGHT;
            int cardColor = (note.getCardColor() != 0) ? note.getCardColor() : palette[pos % palette.length];
            cardView.setCardBackgroundColor(cardColor);

            // Title color on card:
            // If user set a custom title color (stored as bright/light colour for the dark topbar),
            // we use it directly on the card too — it shows the chosen colour.
            // If no custom colour was set, default to dark text (always readable on coloured card).
            int titleColorOnCard;
            int savedTitleColor = note.getTitleColor();
            if (savedTitleColor != 0 && savedTitleColor != Color.WHITE) {
                // Use the chosen colour — user wanted this colour for their title
                titleColorOnCard = savedTitleColor;
            } else {
                // Default: dark text, readable on any light card
                titleColorOnCard = 0xFF1A1A1A;
            }
            tvTitle.setTextColor(titleColorOnCard);

            // Accent bar and secondary text always dark for readability on light cards
            tvPreview.setTextColor(0xFF444444);
            tvDate.setTextColor(0xFF888888);
            if (accentBar != null) accentBar.setBackgroundColor(0xFF1A1A2E);

            itemView.setOnClickListener(v -> clickListener.onNoteClick(note, getAdapterPosition()));
            itemView.setOnLongClickListener(v -> { longClickListener.onNoteLongClick(note, getAdapterPosition()); return true; });
        }
    }
}
