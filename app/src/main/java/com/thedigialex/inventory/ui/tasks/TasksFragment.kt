package com.thedigialex.inventory.ui.tasks

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.thedigialex.inventory.R
import com.thedigialex.inventory.database.entity.Task
import com.thedigialex.inventory.databinding.FragmentTasksBinding
import java.text.SimpleDateFormat
import java.util.*

class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var adapter: TaskAdapter
    private var allTasks: List<Task> = emptyList()
    private var showCompleted = false

    private val dateFmt = SimpleDateFormat("yyyy-M-d", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = TaskAdapter(
            onToggle = { task -> viewModel.updateTask(task) },
            onEdit = { showTaskDialog(it) },
            onDelete = { viewModel.deleteTask(it) }
        )
        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTasks.adapter = adapter

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Active"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Completed"))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                showCompleted = tab.position == 1
                applyFilter()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        viewModel.tasks.observe(viewLifecycleOwner) {
            allTasks = it
            applyFilter()
        }
        viewModel.selectedFeature.observe(viewLifecycleOwner) { binding.tvFeatureTitle.text = it.name }
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.fab.setOnClickListener { showTaskDialog(null) }
    }

    private fun applyFilter() {
        adapter.submitList(allTasks.filter { it.isCompleted == showCompleted })
    }

    private fun showTaskDialog(existing: Task?) {
        val featureId = viewModel.selectedFeature.value?.id ?: return
        var selectedDueDate: Long? = existing?.dueDate

        val dialogView = layoutInflater.inflate(R.layout.fragment_add_task, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)
        val btnDate = dialogView.findViewById<Button>(R.id.btnPickDate)
        val spinnerPriority = dialogView.findViewById<Spinner>(R.id.spinnerPriority)
        val spinnerRepeat = dialogView.findViewById<Spinner>(R.id.spinnerRepeat)

        ArrayAdapter.createFromResource(requireContext(), R.array.priority_levels, R.layout.item_spinner)
            .also { it.setDropDownViewResource(R.layout.item_spinner_dropdown); spinnerPriority.adapter = it }
        ArrayAdapter.createFromResource(requireContext(), R.array.repeat_intervals, R.layout.item_spinner)
            .also { it.setDropDownViewResource(R.layout.item_spinner_dropdown); spinnerRepeat.adapter = it }

        existing?.let {
            etTitle.setText(it.title)
            etDescription.setText(it.description)
            spinnerPriority.setSelection(it.priority)
            spinnerRepeat.setSelection(Task.repeatToSpinnerIndex(it.repeatIntervalDays))
            it.dueDate?.let { d -> btnDate.text = dateFmt.format(Date(d)) }
        }

        btnDate.setOnClickListener {
            val cal = Calendar.getInstance().also { c -> selectedDueDate?.let { d -> c.timeInMillis = d } }
            DatePickerDialog(requireContext(), { _, y, m, d ->
                selectedDueDate = Calendar.getInstance().apply { set(y, m, d) }.timeInMillis
                btnDate.text = "$y-${m + 1}-$d"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (existing == null) "New Task" else "Edit Task")
            .setView(dialogView)
            .setPositiveButton(if (existing == null) "Add" else "Save") { _, _ ->
                val title = etTitle.text.toString().ifBlank { return@setPositiveButton }
                val repeatDays = Task.repeatFromSpinnerIndex(spinnerRepeat.selectedItemPosition)
                if (existing == null) {
                    viewModel.insertTask(Task(
                        featureId = featureId,
                        title = title,
                        description = etDescription.text.toString(),
                        dueDate = selectedDueDate,
                        priority = spinnerPriority.selectedItemPosition,
                        repeatIntervalDays = repeatDays
                    ))
                } else {
                    viewModel.updateTask(existing.copy(
                        title = title,
                        description = etDescription.text.toString(),
                        priority = spinnerPriority.selectedItemPosition,
                        repeatIntervalDays = repeatDays,
                        dueDate = selectedDueDate
                    ))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
