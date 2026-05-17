package com.thedigialex.inventory.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thedigialex.inventory.databinding.ItemCalendarDayBinding

data class CalendarDay(
    val dayNumber: Int,
    val hasTask: Boolean = false,
    val hasBudget: Boolean = false,
    val isToday: Boolean = false
)

class CalendarDayAdapter(private val onDayClick: (Int) -> Unit) :
    ListAdapter<CalendarDay, CalendarDayAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemCalendarDayBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = getItem(position)
        holder.binding.apply {
            if (day.dayNumber == 0) {
                tvDay.text = ""
                tvDay.background = null
                dotTask.visibility = View.GONE
                dotBudget.visibility = View.GONE
                root.isClickable = false
            } else {
                tvDay.text = day.dayNumber.toString()
                tvDay.background = if (day.isToday)
                    root.context.getDrawable(com.thedigialex.inventory.R.drawable.bg_today) else null
                tvDay.setTextColor(
                    if (day.isToday) root.context.getColor(android.R.color.white)
                    else root.context.getColor(com.thedigialex.inventory.R.color.textPrimary)
                )
                dotTask.visibility = if (day.hasTask) View.VISIBLE else View.GONE
                dotBudget.visibility = if (day.hasBudget) View.VISIBLE else View.GONE
                root.isClickable = true
                root.setOnClickListener { onDayClick(day.dayNumber) }
            }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<CalendarDay>() {
            override fun areItemsTheSame(a: CalendarDay, b: CalendarDay) = a.dayNumber == b.dayNumber
            override fun areContentsTheSame(a: CalendarDay, b: CalendarDay) = a == b
        }
    }
}
