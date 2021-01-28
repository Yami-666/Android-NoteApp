package com.example.noteapp.ui.add

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentAddNoteBinding
import com.example.noteapp.databinding.LayoutSheetMoreBinding
import com.example.noteapp.model.Note
import com.example.noteapp.viewModel.NoteViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.android.synthetic.main.layout_sheet_more.*
import java.text.SimpleDateFormat
import java.util.*

class AddNoteFragment : Fragment() {

    private lateinit var mNoteViewModel: NoteViewModel

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var subtitleIndicator: View

    // Цвет заментки указан по-умолчанию
    var selectedNoteColor: String = "#333333"

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

        // Указание цвета индикатора у подзагаловка
        subtitleIndicator = binding.indicator

        // Указываем формат даты и времени
        binding.textDateTime.text =
                SimpleDateFormat("EEEE, dd, MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(Date())

        binding.imageCreateNote.setOnClickListener {
            createNote()
        }

        initMoreDialog(view)
        setIndicatorColor()

        return view
    }

    // Создание новой заметки
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
                color = selectedNoteColor,
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

    // Проверка введённых данных
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


    // Инициализация нижний панели
    private fun initMoreDialog(view: View) {
        val layoutMoreDialog = view.findViewById<LinearLayout>(R.id.layoutSheetMore)
        val bottomSheetBehavior = BottomSheetBehavior.from(layoutMoreDialog)
        layoutMoreDialog.findViewById<TextView>(R.id.textSheetDialog).setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        val imageColorYellow = layoutMoreDialog.findViewById<ImageView>(R.id.imageColorYellow)
        val imageColorDefault = layoutMoreDialog.findViewById<ImageView>(R.id.imageColorDefault)
        val imageColorRed = layoutMoreDialog.findViewById<ImageView>(R.id.imageColorRed)
        val imageColorBlue = layoutMoreDialog.findViewById<ImageView>(R.id.imageColorBlue)
        val imageColorGreen = layoutMoreDialog.findViewById<ImageView>(R.id.imageColorGreen)

        layoutMoreDialog.findViewById<ImageView>(R.id.imageColorDefault).setOnClickListener {
            selectedNoteColor = "#333333"
            resetImageResource()
            imageColorDefault.setImageResource(R.drawable.ic_check)
            setIndicatorColor()
        }

        layoutMoreDialog.findViewById<ImageView>(R.id.imageColorGreen).setOnClickListener {
            selectedNoteColor = "#007100"
            resetImageResource()
            imageColorGreen.setImageResource(R.drawable.ic_check)
            setIndicatorColor()
        }

        layoutMoreDialog.findViewById<ImageView>(R.id.imageColorBlue).setOnClickListener {
            selectedNoteColor = "#3A52FC"
            resetImageResource()
            imageColorBlue.setImageResource(R.drawable.ic_check)
            setIndicatorColor()
        }

        layoutMoreDialog.findViewById<ImageView>(R.id.imageColorRed).setOnClickListener {
            selectedNoteColor = "#c40019"
            resetImageResource()
            imageColorRed.setImageResource(R.drawable.ic_check)
            setIndicatorColor()
        }

        layoutMoreDialog.findViewById<ImageView>(R.id.imageColorYellow).setOnClickListener {
            selectedNoteColor = "#FFBB2F"
            resetImageResource()
            imageColorYellow.setImageResource(R.drawable.ic_check)
            setIndicatorColor()
        }
    }

    private fun resetImageResource() {
        imageColorBlue.setImageResource(0)
        imageColorDefault.setImageResource(0)
        imageColorGreen.setImageResource(0)
        imageColorRed.setImageResource(0)
        imageColorYellow.setImageResource(0)
    }

    private fun setIndicatorColor() {
        val gradientDrawable = subtitleIndicator.background as GradientDrawable
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor))
    }
}
