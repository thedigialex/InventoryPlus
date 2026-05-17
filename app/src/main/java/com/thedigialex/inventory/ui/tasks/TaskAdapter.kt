package com.thedigialex.inventory.ui.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thedigialex.inventory.database.entity.Task
import com.thedigialex.inventory.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onToggle: (Task) -> Unit,
    private val onEdit: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = getItem(position)
        holder.binding.apply {
            tvTitle.text = task.title
            tvDescription.text = task.description
            tvDueDate.text = task.dueDate?.let { DATE_FMT.format(Date(it)) } ?: "No due date"
            cbCompleted.setOnCheckedChangeListener(null)
            cbCompleted.isChecked = task.isCompleted
            cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && task.repeatIntervalDays > 0) {
                    cbCompleted.setOnCheckedChangeListener(null)
                    cbCompleted.isChecked = false
                    val nextDue = (task.dueDate ?: System.currentTimeMillis()) +
                            task.repeatIntervalDays * 24L * 60 * 60 * 1000
                    onToggle(task.copy(isCompleted = false, dueDate = nextDue))
                } else {
                    onToggle(task.copy(isCompleted = isChecked))
                }
            }
            tvPriority.text = when (task.priority) { 1 -> "Medium"; 2 -> "High"; else -> "Low" }
            if (task.repeatIntervalDays > 0) {
                tvRepeat.text = "  •  ↻ ${Task.repeatLabel(task.repeatIntervalDays)}"
                tvRepeat.visibility = View.VISIBLE
            } else {
                tvRepeat.visibility = View.GONE
            }
            btnEdit.setOnClickListener { onEdit(task) }
            btnDelete.setOnClickListener { onDelete(task) }
        }
    }

    companion object {
        private val DATE_FMT = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        val DIFF = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(a: Task, b: Task) = a.id == b.id
            override fun areContentsTheSame(a: Task, b: Task) = a == b
        }
    }
}
