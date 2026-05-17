package com.thedigialex.inventory.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.thedigialex.inventory.database.entity.Note
import com.thedigialex.inventory.databinding.FragmentNoteDetailBinding

class NoteDetailFragment : Fragment() {
    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NoteViewModel by viewModels()
    private var noteId: Int = -1
    private var existingNote: Note? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        noteId = arguments?.getInt("noteId", -1) ?: -1

        if (noteId != -1) {
            viewModel.notes.observe(viewLifecycleOwner) { notes ->
                if (existingNote == null) {
                    notes.find { it.id == noteId }?.let { note ->
                        existingNote = note
                        binding.etTitle.setText(note.title)
                        binding.etDescription.setText(note.description)
                    }
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().ifBlank { "Untitled" }
            val description = binding.etDescription.text.toString()
            val now = System.currentTimeMillis()
            if (existingNote != null) {
                viewModel.update(existingNote!!.copy(title = title, description = description, updatedDate = now))
            } else {
                viewModel.insert(Note(title = title, description = description, createdDate = now, updatedDate = now))
            }
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
