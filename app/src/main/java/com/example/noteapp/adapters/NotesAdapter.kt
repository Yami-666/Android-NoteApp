package com.example.noteapp.adapters

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
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.model.Note
import kotlinx.coroutines.*

class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {
    private var noteList = emptyList<Note>()

    @Volatile
    private var bitmap: Bitmap? = null

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

        val layoutNote = holder.itemView.findViewById<LinearLayout>(R.id.layoutItemNote)
        val gradientDrawable = layoutNote.background as GradientDrawable
        if (currentNote.color != null) {
            gradientDrawable.setColor(Color.parseColor(currentNote.color))
        } else {
            gradientDrawable.setColor(Color.parseColor("#333333"))
        }

        if (currentNote.imagePath != null) {
            val imageNote = holder.itemView.findViewById<ImageView>(R.id.imageNote)

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