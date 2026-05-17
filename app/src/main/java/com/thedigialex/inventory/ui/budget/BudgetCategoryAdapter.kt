package com.thedigialex.inventory.ui.budget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thedigialex.inventory.database.entity.BudgetCategory
import com.thedigialex.inventory.databinding.ItemBudgetCategoryBinding

class BudgetCategoryAdapter(
    private val onClick: (BudgetCategory) -> Unit,
    private val onDelete: (BudgetCategory) -> Unit
) : ListAdapter<BudgetCategory, BudgetCategoryAdapter.ViewHolder>(DIFF) {

    var selectedId: Int = -1

    inner class ViewHolder(val binding: ItemBudgetCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemBudgetCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cat = getItem(position)
        holder.binding.apply {
            tvName.text = cat.name
            tvType.text = cat.type.replaceFirstChar { it.uppercase() }
            tvType.setTextColor(
                if (cat.type == "income") root.context.getColor(android.R.color.holo_green_dark)
                else root.context.getColor(android.R.color.holo_red_dark)
            )
            root.isSelected = cat.id == selectedId
            root.strokeWidth = if (cat.id == selectedId) 3 else 0
            root.setOnClickListener { onClick(cat) }
            btnDelete.setOnClickListener { onDelete(cat) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<BudgetCategory>() {
            override fun areItemsTheSame(a: BudgetCategory, b: BudgetCategory) = a.id == b.id
            override fun areContentsTheSame(a: BudgetCategory, b: BudgetCategory) = a == b
        }
    }
}
