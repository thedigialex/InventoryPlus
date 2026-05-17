package com.thedigialex.inventory.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thedigialex.inventory.databinding.ItemBudgetSummaryBinding

class BudgetSummaryAdapter : ListAdapter<CalendarCategoryGroup, BudgetSummaryAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemBudgetSummaryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemBudgetSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = getItem(position)
        holder.binding.apply {
            tvCategoryName.text = group.categoryName
            val sign = if (group.type == "income") "+" else "-"
            tvTotal.text = "$sign${"%.2f".format(group.total)}"
            tvTotal.setTextColor(
                if (group.type == "income") root.context.getColor(android.R.color.holo_green_dark)
                else root.context.getColor(android.R.color.holo_red_dark)
            )
            tvSubGroups.text = group.subGroups.joinToString("\n") { (name, total) ->
                "  • $name: ${"%.2f".format(total)}"
            }
            tvSubGroups.visibility = if (group.subGroups.isEmpty()) android.view.View.GONE
            else android.view.View.VISIBLE
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<CalendarCategoryGroup>() {
            override fun areItemsTheSame(a: CalendarCategoryGroup, b: CalendarCategoryGroup) =
                a.categoryName == b.categoryName && a.type == b.type
            override fun areContentsTheSame(a: CalendarCategoryGroup, b: CalendarCategoryGroup) = a == b
        }
    }
}
