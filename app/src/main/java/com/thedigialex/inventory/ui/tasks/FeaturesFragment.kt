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
import com.thedigialex.inventory.database.entity.Feature
import com.thedigialex.inventory.databinding.FragmentFeaturesBinding

class FeaturesFragment : Fragment() {
    private var _binding: FragmentFeaturesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var adapter: NameDescAdapter<Feature>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFeaturesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = NameDescAdapter(
            onClick = { feature ->
                viewModel.selectFeature(feature)
                findNavController().navigate(R.id.action_features_to_tasks)
            },
            onEdit = { showFeatureDialog(it) },
            onDelete = { viewModel.deleteFeature(it) }
        )
        binding.rvFeatures.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFeatures.adapter = adapter

        viewModel.features.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.selectedProject.observe(viewLifecycleOwner) { binding.tvProjectTitle.text = it.name }
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.fab.setOnClickListener { showFeatureDialog(null) }
    }

    private fun showFeatureDialog(existing: Feature?) {
        val projectId = viewModel.selectedProject.value?.id ?: return
        val dialogView = layoutInflater.inflate(R.layout.fragment_add_item, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)
        existing?.let { etName.setText(it.name); etDescription.setText(it.description) }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (existing == null) "New Feature" else "Edit Feature")
            .setView(dialogView)
            .setPositiveButton(if (existing == null) "Add" else "Save") { _, _ ->
                val name = etName.text.toString().ifBlank { return@setPositiveButton }
                val desc = etDescription.text.toString()
                if (existing == null) viewModel.insertFeature(Feature(projectId = projectId, name = name, description = desc))
                else viewModel.updateFeature(existing.copy(name = name, description = desc))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
