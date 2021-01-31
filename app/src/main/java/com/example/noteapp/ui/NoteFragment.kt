package com.example.noteapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.R
import com.example.noteapp.adapters.NotesAdapter
import com.example.noteapp.databinding.FragmentNotesBinding
import com.example.noteapp.viewModel.NoteViewModel

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

        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        recycleView.layoutManager = manager

        mNoteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        mNoteViewModel.getAllNote.observe(viewLifecycleOwner, {
            adapter.setData(it)
        })

        binding.inputSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.cancelTimer()
            }

            override fun afterTextChanged(s: Editable?) {
                if(s?.isEmpty()!!) {
                    mNoteViewModel.getAllNote.value?.let { adapter.setData(it) }
                } else {
                    adapter.searchNote(s.toString())
                }
            }

        })

        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}