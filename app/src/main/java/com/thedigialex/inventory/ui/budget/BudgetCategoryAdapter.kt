package com.thedigialex.inventory.ui.budget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thedigialex.inventory.databinding.ItemBudgetCategoryBinding

class BudgetCategoryAdapter(
    private val onClick: (CategoryWithTotal) -> Unit,
    private val onEdit: (CategoryWithTotal) -> Unit,
    private val onDelete: (CategoryWithTotal) -> Unit
) : ListAdapter<CategoryWithTotal, BudgetCategoryAdapter.ViewHolder>(DIFF) {

    var selectedId: Int = -1

    inner class ViewHolder(val binding: ItemBudgetCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemBudgetCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val cat = item.category
        holder.binding.apply {
            tvName.text = cat.name
            tvType.text = cat.type.replaceFirstChar { it.uppercase() }
            tvType.setTextColor(
                if (cat.type == "income") root.context.getColor(android.R.color.holo_green_dark)
                else root.context.getColor(android.R.color.holo_red_dark)
            )
            tvTotal.text = "Budgeted: ${"%.2f".format(item.total)}"
            root.isSelected = cat.id == selectedId
            root.strokeWidth = if (cat.id == selectedId) 3 else 0
            root.setOnClickListener { onClick(item) }
            btnEdit.setOnClickListener { onEdit(item) }
            btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<CategoryWithTotal>() {
            override fun areItemsTheSame(a: CategoryWithTotal, b: CategoryWithTotal) = a.category.id == b.category.id
            override fun areContentsTheSame(a: CategoryWithTotal, b: CategoryWithTotal) = a == b
        }
    }
}
