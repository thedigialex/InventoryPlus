package com.thedigialex.inventory.ui.budget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thedigialex.inventory.database.entity.BudgetSubCategory
import com.thedigialex.inventory.databinding.ItemBudgetSubCategoryBinding

class BudgetSubCategoryAdapter(
    private val onEdit: (BudgetSubCategory) -> Unit,
    private val onDelete: (BudgetSubCategory) -> Unit
) : ListAdapter<BudgetSubCategory, BudgetSubCategoryAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemBudgetSubCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemBudgetSubCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sub = getItem(position)
        holder.binding.apply {
            tvName.text = sub.name
            tvBudgeted.text = "Budget: ${"%.2f".format(sub.budgetedAmount)}"
            btnEdit.setOnClickListener { onEdit(sub) }
            btnDelete.setOnClickListener { onDelete(sub) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<BudgetSubCategory>() {
            override fun areItemsTheSame(a: BudgetSubCategory, b: BudgetSubCategory) = a.id == b.id
            override fun areContentsTheSame(a: BudgetSubCategory, b: BudgetSubCategory) = a == b
        }
    }
}
