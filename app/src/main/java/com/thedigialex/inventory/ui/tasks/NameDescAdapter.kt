package com.thedigialex.inventory.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thedigialex.inventory.database.entity.NamedItem
import com.thedigialex.inventory.databinding.ItemNameDescBinding

class NameDescAdapter<T : NamedItem>(
    private val onClick: (T) -> Unit,
    private val onEdit: (T) -> Unit,
    private val onDelete: (T) -> Unit
) : ListAdapter<T, NameDescAdapter<T>.ViewHolder>(
    object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(a: T, b: T) = a.id == b.id
        override fun areContentsTheSame(a: T, b: T) = a == b
    }
) {
    inner class ViewHolder(val binding: ItemNameDescBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemNameDescBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            tvName.text = item.name
            tvDescription.text = item.description
            root.setOnClickListener { onClick(item) }
            btnEdit.setOnClickListener { onEdit(item) }
            btnDelete.setOnClickListener { onDelete(item) }
        }
    }
}
