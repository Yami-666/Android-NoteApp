package com.example.noteapp.ui.add

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentAddNoteBinding
import com.example.noteapp.model.Note
import com.example.noteapp.viewModel.NoteViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.layout_sheet_more.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddNoteFragment : Fragment() {

    private lateinit var mNoteViewModel: NoteViewModel
    private val args by navArgs<AddNoteFragmentArgs>()

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1
        private const val REQUEST_CODE_SELECT_IMAGE = 2
    }

    private lateinit var subtitleIndicator: View

    // Цвет заметки (указан по-умолчанию)
    private var selectedNoteColor: String = "#333333"

    // Выбранная пользователем изображение
    private var selectedImagePath: String = ""

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
        // Инициализирует диалоговое окно
        initMoreDialog()
        // Устанавливает цвет индикатора у subtitle
        setIndicatorColor()

        if (args.noteArgs != null) {
            fillNoteFields()
            findColor(selectedNoteColor)
        }

        // Создание/Обновление note
        binding.imageCreateNote.setOnClickListener {
            if (args.noteArgs != null) {
                val note = createNote()
                mNoteViewModel.updateNote(note)
                Toast.makeText(requireContext(), "Note is successfully update!", Toast.LENGTH_SHORT)
                        .show()
            } else {
                val note = createNote()
                mNoteViewModel.addNote(note)
                Toast.makeText(requireContext(), "Note is successfully created!", Toast.LENGTH_SHORT)
                        .show()
            }
            findNavController().navigate(R.id.action_addNoteFragment_to_notesFragment)

        }

        return view
    }

    private fun findColor(color: String) {
        when (color) {
            "#333333" -> binding.root.layoutSheetMore.imageColorDefault.performClick()
            "#007100" -> binding.root.layoutSheetMore.imageColorGreen.performClick()
            "#3A52FC" -> binding.root.layoutSheetMore.imageColorBlue.performClick()
            "#c40019" -> binding.root.layoutSheetMore.imageColorRed.performClick()
            "#FFBB2F" -> binding.root.layoutSheetMore.imageColorYellow.performClick()
            else -> binding.root.layoutSheetMore.imageColorDefault.performClick()
        }
    }

    private fun createNote(): Note {
        return Note(
                id = args.noteArgs?.id ?: 0,
                title = this.binding.inputNoteTitle.text.toString().trim(),
                subtitle = this.binding.inputNoteSubtitle.text.toString().trim(),
                dateTime = this.binding.textDateTime.text.toString().trim(),
                text = this.binding.inputNoteText.text.toString().trim(),
                color = this.selectedNoteColor,
                imagePath = this.selectedImagePath,
                webLink = null,
                video = null,
                audio = null
        )
    }

    private fun fillNoteFields() {
        binding.inputNoteTitle.setText(args.noteArgs?.title)
        binding.inputNoteSubtitle.setText(args.noteArgs?.subtitle)
        binding.inputNoteText.setText(args.noteArgs?.text)
        if (args.noteArgs?.color != null) {
            selectedNoteColor = args.noteArgs?.color!!
        }
        if (args.noteArgs?.imagePath != "" && args.noteArgs?.imagePath != null) {
            selectedImagePath = args.noteArgs?.imagePath!!
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = BitmapFactory
                        .decodeFile(args.noteArgs?.imagePath)

                withContext(Dispatchers.Main) {
                    binding.imageNote.setImageBitmap(bitmap)
                    binding.imageNote.visibility = View.VISIBLE
                }
            }
        }
    }

//    // Проверка введённых данных
//    private fun inputValid(): Boolean {
//        if (binding.inputNoteTitle.text.toString().trim().isEmpty()) {
//            Toast.makeText(requireContext(), "Empty field", Toast.LENGTH_SHORT).show()
//            return false
//        }
//        if (binding.inputNoteSubtitle.text.toString().trim().isEmpty()) {
//            Toast.makeText(requireContext(), "Empty field", Toast.LENGTH_SHORT).show()
//            return false
//        }
//        if (binding.inputNoteText.text.toString().trim().isEmpty()) {
//            Toast.makeText(requireContext(), "Empty field", Toast.LENGTH_SHORT).show()
//            return false
//        }
//        return true
//    }


    // Инициализация нижний панели
    private fun initMoreDialog() {
        val layoutDialog = binding.root.layoutSheetMore
        val bottomSheetBehavior = BottomSheetBehavior.from(layoutDialog)

        layoutDialog.textSheetDialog.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        layoutDialog.imageColorDefault.setOnClickListener {
            selectedNoteColor = "#333333"
            resetImageResource()
            layoutDialog.imageColorDefault.setImageResource(R.drawable.ic_check)
            setIndicatorColor()
        }
        layoutDialog.imageColorGreen.setOnClickListener {
            selectedNoteColor = "#007100"
            resetImageResource()
            layoutDialog.imageColorGreen.setImageResource(R.drawable.ic_check)
            setIndicatorColor()
        }
        layoutDialog.imageColorBlue.setOnClickListener {
            selectedNoteColor = "#3A52FC"
            resetImageResource()
            layoutDialog.imageColorBlue.setImageResource(R.drawable.ic_check)
            setIndicatorColor()
        }
        layoutDialog.imageColorRed.setOnClickListener {
            selectedNoteColor = "#c40019"
            resetImageResource()
            layoutDialog.imageColorRed.setImageResource(R.drawable.ic_check)
            setIndicatorColor()
        }
        layoutDialog.imageColorYellow.setOnClickListener {
            selectedNoteColor = "#FFBB2F"
            resetImageResource()
            layoutDialog.imageColorYellow.setImageResource(R.drawable.ic_check)
            setIndicatorColor()
        }
        layoutDialog.layoutAddImage.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission
                            .READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_CODE_STORAGE_PERMISSION)
            } else {
                selectImage()
            }
        }
    }

    // Сбрасывает все отметки у значков выбора цвета
    private fun resetImageResource() {
        with(binding.root.layoutSheetMore) {
            imageColorBlue.setImageResource(0)
            imageColorDefault.setImageResource(0)
            imageColorGreen.setImageResource(0)
            imageColorRed.setImageResource(0)
            imageColorYellow.setImageResource(0)
        }
    }

    // Метод выбора изображения пользователем
    @SuppressLint("QueryPermissionsNeeded")
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (activity?.packageManager?.let { intent.resolveActivity(it) } != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }
    }

    private fun setIndicatorColor() {
        val gradientDrawable = subtitleIndicator.background as GradientDrawable
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor))
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data!!
            try {
                val inputStream = activity?.contentResolver?.openInputStream(selectedImageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.imageNote.setImageBitmap(bitmap)
                binding.imageNote.visibility = View.VISIBLE

                selectedImagePath = getPathFromUri(selectedImageUri)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }

        }
    }


    // Возвращает путь картинки через uri
    private fun getPathFromUri(contentUri: Uri): String {
        lateinit var filePath: String

        val cursor = activity?.contentResolver?.query(
                contentUri, null, null, null, null
        )

        if (cursor == null) {
            filePath = contentUri.path.toString()
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }
}