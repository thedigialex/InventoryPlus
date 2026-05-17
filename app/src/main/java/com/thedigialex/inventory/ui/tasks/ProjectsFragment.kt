package com.thedigialex.inventory.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.thedigialex.inventory.R
import com.thedigialex.inventory.database.entity.Project
import com.thedigialex.inventory.databinding.FragmentProjectsBinding

class ProjectsFragment : Fragment() {
    private var _binding: FragmentProjectsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var adapter: NameDescAdapter<Project>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = NameDescAdapter(
            onClick = { project ->
                viewModel.selectProject(project)
                findNavController().navigate(R.id.action_projects_to_features)
            },
            onEdit = { showProjectDialog(it) },
            onDelete = { viewModel.deleteProject(it) }
        )
        binding.rvProjects.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProjects.adapter = adapter

        viewModel.projects.observe(viewLifecycleOwner) { adapter.submitList(it) }
        binding.fab.setOnClickListener { showProjectDialog(null) }
    }

    private fun showProjectDialog(existing: Project?) {
        val dialogView = layoutInflater.inflate(R.layout.fragment_add_item, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)
        existing?.let { etName.setText(it.name); etDescription.setText(it.description) }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (existing == null) "New Project" else "Edit Project")
            .setView(dialogView)
            .setPositiveButton(if (existing == null) "Add" else "Save") { _, _ ->
                val name = etName.text.toString().ifBlank { return@setPositiveButton }
                val desc = etDescription.text.toString()
                if (existing == null) viewModel.insertProject(Project(name = name, description = desc))
                else viewModel.updateProject(existing.copy(name = name, description = desc))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
