package com.example.noteapp.ui.add

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentAddNoteBinding
import com.example.noteapp.databinding.LayoutDeleteDialogBinding
import com.example.noteapp.model.Note
import com.example.noteapp.ui.enum.ColorNoteType
import com.example.noteapp.viewModel.NoteViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_add_note.view.*
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

    private lateinit var mDeleteDialog: AlertDialog
    private lateinit var mUrlDialog: AlertDialog

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1
        private const val REQUEST_CODE_SELECT_IMAGE = 2
    }

    private lateinit var subtitleIndicator: View

    // Selected user color for indicator and Note
    private var selectedNoteColor: String = ColorNoteType.DEFAULT.color

    // Selected user image path
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

        // Subtitle indicator view for change color
        subtitleIndicator = binding.indicator

        // Data & time auto select
        binding.textDateTime.text =
                SimpleDateFormat("EEEE, dd, MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(Date())

        initMoreDialog()
        setIndicatorColor()

        if (args.noteArgs != null) {
            fillNoteFields()
            findColor(selectedNoteColor)
        }

        // Add/Update Note
        binding.imageCreateNote.setOnClickListener {
            if (args.noteArgs != null) {
                val note = createNote()
                mNoteViewModel.updateNote(note)
            } else {
                val note = createNote()
                mNoteViewModel.addNote(note)
            }
            findNavController().navigate(R.id.action_addNoteFragment_to_notesFragment)

        }

        // Delete url
        binding.imageDeleteUrl.setOnClickListener {
            binding.layoutWebURL.visibility = View.GONE
        }

        return view
    }

    // Find color if we want to update note
    private fun findColor(color: String) {
        when (color) {
            ColorNoteType.DEFAULT.color -> binding.root.layoutSheetMore.imageColorDefault.performClick()
            ColorNoteType.GREEN.color -> binding.root.layoutSheetMore.imageColorGreen.performClick()
            ColorNoteType.BLUE.color -> binding.root.layoutSheetMore.imageColorBlue.performClick()
            ColorNoteType.RED.color -> binding.root.layoutSheetMore.imageColorRed.performClick()
            ColorNoteType.YELLOW.color -> binding.root.layoutSheetMore.imageColorYellow.performClick()
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
                webLink = if (binding.layoutWebURL.visibility == View.VISIBLE) binding.textURL
                        .text.toString() else "",
                video = null,
                audio = null
        )
    }

    private fun fillNoteFields() {
        binding.inputNoteTitle.setText(args.noteArgs?.title)
        binding.inputNoteSubtitle.setText(args.noteArgs?.subtitle)
        binding.inputNoteText.setText(args.noteArgs?.text)
        // if the color is...
        if (args.noteArgs?.color != null) {
            selectedNoteColor = args.noteArgs?.color!!
        }
        // If the picture is...
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
        // If the web link is...
        if (args.noteArgs?.webLink != null && args.noteArgs?.webLink != "") {
            binding.textURL.text = args.noteArgs?.webLink!!
            binding.root.layoutWebURL.visibility = View.VISIBLE
        }
    }

    // Init sheet dialog
    private fun initMoreDialog() {
        val layoutDialog = binding.root.layoutSheetMore
        val bottomSheetBehavior = BottomSheetBehavior.from(layoutDialog)

        // Show sheet dialog after click
        layoutDialog.textSheetDialog.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        // Change color note
        layoutDialog.imageColorDefault.setOnClickListener {
            changeNoteColor(ColorNoteType.DEFAULT.color)
            layoutDialog.imageColorDefault.setImageResource(R.drawable.ic_check)
        }
        layoutDialog.imageColorGreen.setOnClickListener {
            changeNoteColor(ColorNoteType.GREEN.color)
            layoutDialog.imageColorGreen.setImageResource(R.drawable.ic_check)
        }
        layoutDialog.imageColorBlue.setOnClickListener {
            changeNoteColor(ColorNoteType.BLUE.color)
            layoutDialog.imageColorBlue.setImageResource(R.drawable.ic_check)
        }
        layoutDialog.imageColorRed.setOnClickListener {
            changeNoteColor(ColorNoteType.RED.color)
            layoutDialog.imageColorRed.setImageResource(R.drawable.ic_check)
        }
        layoutDialog.imageColorYellow.setOnClickListener {
            changeNoteColor(ColorNoteType.YELLOW.color)
            layoutDialog.imageColorYellow.setImageResource(R.drawable.ic_check)
        }

        // Add image
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

        // Add web-url
        layoutDialog.layoutAddWebLink.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            initUrlDialog()
        }

        // Delete note
        if (args.noteArgs != null) {
            layoutDialog.layoutDeleteNote.visibility = View.VISIBLE
            layoutDialog.layoutDeleteNote.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                initDeleteNoteDialog()
            }
        }
    }

    private fun changeNoteColor(color: String) {
        selectedNoteColor = color

        // Resetting all color icons to override one in the future
        with(binding.root.layoutSheetMore) {
            imageColorBlue.setImageResource(0)
            imageColorDefault.setImageResource(0)
            imageColorGreen.setImageResource(0)
            imageColorRed.setImageResource(0)
            imageColorYellow.setImageResource(0)
        }

        setIndicatorColor()
    }

    // Init delete dialog with choice
    fun initDeleteNoteDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(
                R.layout.layout_delete_dialog,
                binding.root.findViewById(R.id.layoutDeleteNoteContainer)
        )
        builder.setView(view)
        mDeleteDialog = builder.create()
        if (mDeleteDialog.window != null) {
            mDeleteDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        val dialogBinding = LayoutDeleteDialogBinding.bind(view)
        dialogBinding.textDeleteNote.setOnClickListener {
            mNoteViewModel.deleteNote(createNote())
            mDeleteDialog.cancel()
            findNavController().navigate(R.id.action_addNoteFragment_to_notesFragment)
        }
        dialogBinding.textCancelDialog.setOnClickListener {
            mDeleteDialog.cancel()
        }
        mDeleteDialog.show()
    }

    private fun initUrlDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(
                R.layout.layout_url_dialog,
                binding.root.findViewById(R.id.layoutUrlNoteContainer)
        )

        builder.setView(view)
        mUrlDialog = builder.create()
        if (mUrlDialog.window != null) {
            mUrlDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        val inputUrl = view.findViewById<EditText>(R.id.inputUrl)
        inputUrl.requestFocus()

        view.findViewById<TextView>(R.id.textAddNote).setOnClickListener {
            if (inputUrl.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Enter URL", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.WEB_URL.matcher(inputUrl.text.toString()).matches()) {
                Toast.makeText(requireContext(), "Enter a valid URL", Toast.LENGTH_SHORT).show()
            } else {
                binding.textURL.text = inputUrl.text.toString()
                binding.layoutWebURL.visibility = View.VISIBLE
                mUrlDialog.dismiss()
            }
        }
        view.findViewById<TextView>(R.id.textCancelDialog).setOnClickListener {
            mUrlDialog.dismiss()
        }
        mUrlDialog.show()
    }

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
