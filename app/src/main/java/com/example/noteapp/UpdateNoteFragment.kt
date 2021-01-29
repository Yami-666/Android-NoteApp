package com.example.noteapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.noteapp.R
import com.example.noteapp.UpdateNoteFragmentArgs
import com.example.noteapp.databinding.FragmentUpdateNoteBinding
import com.example.noteapp.viewModel.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateNoteFragment : Fragment() {

    private val args by navArgs<UpdateNoteFragmentArgs>()
    private lateinit var mNoteViewModel: NoteViewModel

    private var _binding: FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_note, container, false)
        _binding = FragmentUpdateNoteBinding.bind(view)

        binding.inputNoteTitle.setText(args.noteArgs.title)
        binding.inputNoteSubtitle.setText(args.noteArgs.subtitle)
        binding.inputNoteText.setText(args.noteArgs.text)
        if (args.noteArgs.imagePath != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = BitmapFactory
                        .decodeFile(args.noteArgs.imagePath)
                withContext(Dispatchers.Main) {
                    binding.imageNote.setImageBitmap(bitmap)
                    binding.imageNote.visibility = View.VISIBLE
                }
            }
        }

        binding.imageCreateNote.setOnClickListener {
            updateNote()
        }
        return view
    }

    private fun updateNote() {

    }
}