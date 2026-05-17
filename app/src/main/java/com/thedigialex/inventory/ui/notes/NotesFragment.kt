package com.thedigialex.inventory.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.thedigialex.inventory.R
import com.thedigialex.inventory.databinding.FragmentNotesBinding

class NotesFragment : Fragment() {
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NoteViewModel by viewModels()
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = NoteAdapter(
            onClick = { note ->
                findNavController().navigate(R.id.action_notes_to_detail, bundleOf("noteId" to note.id))
            },
            onDelete = { viewModel.delete(it) }
        )
        binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotes.adapter = adapter

        viewModel.notes.observe(viewLifecycleOwner) { adapter.submitList(it) }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_notes_to_detail, bundleOf("noteId" to -1))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
