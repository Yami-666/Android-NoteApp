package com.example.noteapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.model.Note

class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {
    private var noteList = emptyList<Note>()

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv_note, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = noteList[position]
        with(holder) {
            itemView.findViewById<TextView>(R.id.textTitle).text = currentNote.title
            itemView.findViewById<TextView>(R.id.textSubtitle).text = currentNote.subtitle
            itemView.findViewById<TextView>(R.id.textDateTime).text = currentNote.dateTime
        }
    }

    override fun getItemCount(): Int = noteList.size

    fun setData(notes: List<Note>) {
        this.noteList = notes
        notifyDataSetChanged()
    }
}