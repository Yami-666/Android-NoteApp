package com.example.noteapp.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.noteapp.R
import com.example.noteapp.databinding.ItemRvNoteBinding
import com.example.noteapp.model.Note
import com.example.noteapp.ui.NoteFragmentDirections
import kotlinx.android.synthetic.main.item_rv_note.view.*
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class NotesAdapter(private val context: Context?) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {
    private var noteList = emptyList<Note>()
    private var newNoteList = noteList
    private var timer = Timer()

    class NoteViewHolder(val binding: ItemRvNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(ItemRvNoteBinding.inflate(LayoutInflater.from(parent.context),
                parent, false))
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = noteList[position]

        if (currentNote.imagePath != null && currentNote.imagePath != "") {
            // Загрузка изображений в списке в фоновом потоке
            // Чтобы при прокрутке не было провисания UI
            val imagePath: Uri = Uri.fromFile(File(currentNote.imagePath))

            val requestOptions = RequestOptions().placeholder(R.drawable.background_note)

            if (context != null) {
                Glide.with(context)
                        .load(imagePath)
                        .apply(requestOptions)
                        .into(holder.binding.imageNote)
            }
        }

        val layoutNote = holder.binding.layoutItemNote
        val gradientDrawable = layoutNote.background as GradientDrawable
        if (currentNote.color != null) {
            gradientDrawable.setColor(Color.parseColor(currentNote.color))
        } else {
            gradientDrawable.setColor(Color.parseColor("#333333"))
        }

        with(holder) {
            binding.textTitle.text = currentNote.title
            itemView.textSubtitle.text = currentNote.subtitle
            itemView.textDateTime.text = currentNote.dateTime
        }

        // Update Note
        layoutNote.setOnClickListener {
            val action = NoteFragmentDirections.actionNotesFragmentToAddNoteFragment(currentNote)
            holder.itemView.findNavController().navigate(action)
        }

        // Delete Note
        layoutNote.setOnLongClickListener {
            Toast.makeText(context, "Long click", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int = noteList.size

    fun setData(notes: List<Note>) {
        this.noteList = notes
        newNoteList = noteList
        notifyDataSetChanged()
    }

    // TODO: 31.01.2021 Remake in the future with coroutines  
    // Simple implementation with Handler and TimeTask
    fun searchNote(searchKeyword: String) {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (searchKeyword.trim().isNotEmpty()) {
                    val tmp: MutableList<Note> = emptyList<Note>().toMutableList()
                    for (note in noteList) {
                        if (note.title.toLowerCase(Locale.ROOT).contains(searchKeyword.toLowerCase(Locale.ROOT))
                                || note.subtitle.toLowerCase(Locale.ROOT).contains(searchKeyword.toLowerCase(Locale.ROOT))
                                || note.text.toLowerCase(Locale.ROOT).contains(searchKeyword.toLowerCase(Locale.ROOT))) {
                            tmp += note
                        }
                    }
                    noteList = tmp
                }
                Handler(Looper.getMainLooper()).post {
                    notifyDataSetChanged()
                }
            }
        }, 500)
    }

    fun cancelTimer() {
        timer.cancel()
    }
}