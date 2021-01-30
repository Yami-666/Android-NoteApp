package com.example.noteapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.R
import com.example.noteapp.adapters.NotesAdapter
import com.example.noteapp.databinding.FragmentNotesBinding
import com.example.noteapp.viewModel.NoteViewModel
import com.google.android.material.circularreveal.CircularRevealHelper

class NoteFragment : Fragment() {

    private lateinit var mNoteViewModel: NoteViewModel

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)
        _binding = FragmentNotesBinding.bind(view)

        binding.addNoteButton.setOnClickListener {
            findNavController().navigate(R.id.action_notesFragment_to_addNoteFragment)
        }

        val recycleView = binding.notesRecycleView
        val adapter = NotesAdapter(context)
        recycleView.adapter = adapter
        recycleView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        mNoteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        mNoteViewModel.getAllNote.observe(viewLifecycleOwner, {
            adapter.setData(it)
        })


        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}