package com.thedigialex.inventory.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.thedigialex.inventory.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var dayAdapter: CalendarDayAdapter
    private lateinit var summaryAdapter: BudgetSummaryAdapter

    private val monthYearFmt = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val monthFmt = SimpleDateFormat("MMMM", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dayAdapter = CalendarDayAdapter { dayNumber -> showDaySummary(dayNumber) }
        binding.rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.rvCalendar.adapter = dayAdapter
        binding.rvCalendar.isNestedScrollingEnabled = false

        summaryAdapter = BudgetSummaryAdapter()
        binding.rvBudgetSummary.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBudgetSummary.adapter = summaryAdapter
        binding.rvBudgetSummary.isNestedScrollingEnabled = false

        viewModel.currentMonth.observe(viewLifecycleOwner) { cal ->
            binding.tvMonth.text = monthYearFmt.format(cal.time)
            rebuildCalendar()
        }
        viewModel.tasks.observe(viewLifecycleOwner) { rebuildCalendar() }
        viewModel.budgetEntries.observe(viewLifecycleOwner) { rebuildCalendar() }
        viewModel.budgetByCategory.observe(viewLifecycleOwner) { summaryAdapter.submitList(it) }

        binding.btnPrev.setOnClickListener { viewModel.previousMonth() }
        binding.btnNext.setOnClickListener { viewModel.nextMonth() }
    }

    private fun rebuildCalendar() {
        val cal = viewModel.currentMonth.value ?: return
        val tasks = viewModel.tasks.value ?: emptyList()
        val budget = viewModel.budgetEntries.value ?: emptyList()
        val today = Calendar.getInstance()
        val tempCal = Calendar.getInstance()

        fun dayOfMonth(millis: Long): Int {
            tempCal.timeInMillis = millis
            return tempCal.get(Calendar.DAY_OF_MONTH)
        }

        val taskDays = tasks.mapNotNull { it.dueDate?.let { d -> dayOfMonth(d) } }.toSet()
        val budgetDays = budget.map { dayOfMonth(it.date) }.toSet()

        val firstDay = (cal.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
        val startDow = firstDay.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val days = mutableListOf<CalendarDay>()
        repeat(startDow) { days.add(CalendarDay(0)) }
        for (d in 1..daysInMonth) {
            val isToday = today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                    today.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
                    today.get(Calendar.DAY_OF_MONTH) == d
            days.add(CalendarDay(d, d in taskDays, d in budgetDays, isToday))
        }
        dayAdapter.submitList(days)
    }

    private fun showDaySummary(dayNumber: Int) {
        val cal = viewModel.currentMonth.value ?: return
        val tasks = viewModel.tasks.value ?: emptyList()
        val budget = viewModel.budgetEntries.value ?: emptyList()
        val tempCal = Calendar.getInstance()

        fun sameDay(millis: Long): Boolean {
            tempCal.timeInMillis = millis
            return tempCal.get(Calendar.DAY_OF_MONTH) == dayNumber &&
                    tempCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
                    tempCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
        }

        val dayTasks = tasks.filter { it.dueDate?.let { d -> sameDay(d) } ?: false }
        val dayBudget = budget.filter { sameDay(it.date) }

        val sb = StringBuilder()
        if (dayTasks.isNotEmpty()) {
            sb.append("Tasks due:\n")
            dayTasks.forEach { sb.append("  • ${it.title} [${if (it.isCompleted) "done" else "pending"}]\n") }
        }
        if (dayBudget.isNotEmpty()) {
            sb.append("\nBudget entries:\n")
            dayBudget.forEach {
                val sign = if (it.type == "income") "+" else "-"
                sb.append("  • ${it.categoryName}${if (it.subCategoryName.isNotBlank()) " / ${it.subCategoryName}" else ""}: $sign${"%.2f".format(it.amount)}\n")
            }
        }
        if (sb.isEmpty()) sb.append("Nothing scheduled for this day.")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("${monthFmt.format(cal.time)} $dayNumber")
            .setMessage(sb.toString())
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
