package com.example.noteapp.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentAddNoteBinding
import com.example.noteapp.model.Note
import com.example.noteapp.viewModel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddNoteFragment : Fragment() {

    private lateinit var mNoteViewModel: NoteViewModel

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_add_note, container, false)
        _binding = FragmentAddNoteBinding.bind(view)

        binding.imageArrowBack.setOnClickListener {
            findNavController().navigate(R.id.action_addNoteFragment_to_notesFragment)
        }

        mNoteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        // Указываем формат даты и времени
        binding.textDateTime.text =
            SimpleDateFormat("EEEE, dd, MMMM yyyy HH:mm a", Locale.getDefault())
                .format(Date())

        binding.imageCreateNote.setOnClickListener {
            createNote()
        }

        return view
    }

    private fun createNote() {
        // Если обязательные поля пусты, то выход из метода
        if (!inputValid()) {
            return
        }

        val note = Note(
            id = 0,
            title = binding.inputNoteTitle.text.toString().trim(),
            subtitle = binding.inputNoteSubtitle.text.toString().trim(),
            dateTime = binding.textDateTime.text.toString().trim(),
            text = binding.inputNoteText.text.toString().trim(),
            color = null,
            imagePath = null,
            webLink = null,
            video = null,
            audio = null
        )

        mNoteViewModel.addNote(note)
        Toast.makeText(requireContext(), "Note is successfully created!", Toast.LENGTH_SHORT)
            .show()
        findNavController().navigate(R.id.action_addNoteFragment_to_notesFragment)
    }

    private fun inputValid(): Boolean {
        if (binding.inputNoteTitle.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Empty field", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.inputNoteSubtitle.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Empty field", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.inputNoteText.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Empty field", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
