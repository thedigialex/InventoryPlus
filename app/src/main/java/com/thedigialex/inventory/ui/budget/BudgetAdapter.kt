package com.thedigialex.inventory.ui.budget

import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thedigialex.inventory.R
import com.thedigialex.inventory.database.entity.BudgetEntry
import com.thedigialex.inventory.databinding.ItemBudgetBinding
import com.thedigialex.inventory.databinding.ItemBudgetHeaderBinding
import java.text.SimpleDateFormat
import java.util.*

class BudgetAdapter(private val onDelete: (BudgetEntry) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<BudgetListItem> = emptyList()

    inner class HeaderViewHolder(val binding: ItemBudgetHeaderBinding) : RecyclerView.ViewHolder(binding.root)
    inner class EntryViewHolder(val binding: ItemBudgetBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int) = when (items[position]) {
        is BudgetListItem.Header -> TYPE_HEADER
        is BudgetListItem.Entry -> TYPE_ENTRY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == TYPE_HEADER)
            HeaderViewHolder(ItemBudgetHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else
            EntryViewHolder(ItemBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is BudgetListItem.Header -> (holder as HeaderViewHolder).binding.apply {
                tvCategoryName.text = item.categoryName
                bindAmount(tvTotal, item.total, item.type)
            }
            is BudgetListItem.Entry -> (holder as EntryViewHolder).binding.apply {
                val entry = item.entry
                tvCategory.text = entry.subCategoryName.ifBlank { entry.categoryName }
                tvDescription.text = entry.description
                tvDate.text = DATE_FMT.format(Date(entry.date))
                bindAmount(tvAmount, entry.amount, entry.type)
                btnDelete.setOnClickListener { onDelete(entry) }
            }
        }
    }

    private fun bindAmount(view: TextView, amount: Double, type: String) {
        val isIncome = type == "income"
        view.text = "${if (isIncome) "+" else "-"}${"%.2f".format(amount)}"
        view.setTextColor(view.context.getColor(if (isIncome) R.color.successGreen else R.color.errorRed))
    }

    fun submitList(newItems: List<BudgetListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ENTRY = 1
        private val DATE_FMT = SimpleDateFormat("MMM dd", Locale.getDefault())
    }
}
