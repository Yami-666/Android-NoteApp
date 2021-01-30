package com.example.noteapp.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentNotesBinding
import com.example.noteapp.databinding.ItemRvNoteBinding
import com.example.noteapp.model.Note
import com.example.noteapp.ui.NoteFragmentDirections
import kotlinx.android.synthetic.main.item_rv_note.view.*
import kotlinx.coroutines.*

class NotesAdapter(private val context: Context?) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {
    private var noteList = emptyList<Note>()

    class NoteViewHolder(val binding: ItemRvNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(ItemRvNoteBinding.inflate(LayoutInflater.from(parent.context),
                parent, false))
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = noteList[position]

        with(holder) {
            binding.textTitle.text = currentNote.title
            itemView.textSubtitle.text = currentNote.subtitle
            itemView.textDateTime.text = currentNote.dateTime
        }

        val layoutNote = holder.binding.layoutItemNote
        val gradientDrawable = layoutNote.background as GradientDrawable
        if (currentNote.color != null) {
            gradientDrawable.setColor(Color.parseColor(currentNote.color))
        } else {
            gradientDrawable.setColor(Color.parseColor("#333333"))
        }

        if (currentNote.imagePath != null) {
            val imageNote = holder.binding.imageNote
            // Загрузка изображений в списке в фоновом потоке
            // Чтобы при прокрутке не было провисания UI
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = getBitmap(currentNote.imagePath)

                withContext(Dispatchers.Main) {
                    imageNote.setImageBitmap(bitmap)
                    imageNote.visibility = View.VISIBLE
                }
            }
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

    private fun getBitmap(image: String?): Bitmap? {
        return BitmapFactory
                .decodeFile(image)
    }

    override fun getItemCount(): Int = noteList.size

    fun setData(notes: List<Note>) {
        this.noteList = notes
        notifyDataSetChanged()
    }
}