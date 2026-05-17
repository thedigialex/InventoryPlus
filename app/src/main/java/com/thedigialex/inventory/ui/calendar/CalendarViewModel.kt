package com.thedigialex.inventory.ui.calendar

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.map
import com.thedigialex.inventory.database.AppDatabase
import com.thedigialex.inventory.database.entity.BudgetEntry
import com.thedigialex.inventory.repository.BudgetRepository
import com.thedigialex.inventory.repository.TaskRepository
import java.util.*

data class CalendarCategoryGroup(
    val categoryName: String,
    val type: String,
    val total: Double,
    val subGroups: List<Pair<String, Double>>
)

class CalendarViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getInstance(app)
    private val budgetRepo = BudgetRepository(db.budgetDao(), db.budgetCategoryDao(), db.budgetSubCategoryDao())
    private val taskRepo = TaskRepository(db.projectDao(), db.featureDao(), db.taskDao())

    private val _currentMonth = MutableLiveData(Calendar.getInstance())
    val currentMonth: LiveData<Calendar> = _currentMonth

    val tasks = _currentMonth.switchMap { cal ->
        taskRepo.getTasksForDateRange(monthStart(cal), monthEnd(cal))
    }

    val budgetEntries: LiveData<List<BudgetEntry>> = _currentMonth.switchMap { cal ->
        budgetRepo.getEntriesForMonth(monthStart(cal), monthEnd(cal))
    }

    val budgetByCategory: LiveData<List<CalendarCategoryGroup>> = budgetEntries.map { entries ->
        entries.groupBy { it.categoryName }
            .map { (name, list) ->
                CalendarCategoryGroup(
                    categoryName = name,
                    type = list.first().type,
                    total = list.sumOf { it.amount },
                    subGroups = list.groupBy { it.subCategoryName.ifBlank { "Other" } }
                        .map { (n, v) -> Pair(n, v.sumOf { it.amount }) }
                        .sortedBy { it.first }
                )
            }
            .sortedWith(compareBy({ it.type }, { it.categoryName }))
    }

    fun previousMonth() {
        val cal = (_currentMonth.value ?: Calendar.getInstance()).clone() as Calendar
        cal.add(Calendar.MONTH, -1)
        _currentMonth.value = cal
    }

    fun nextMonth() {
        val cal = (_currentMonth.value ?: Calendar.getInstance()).clone() as Calendar
        cal.add(Calendar.MONTH, 1)
        _currentMonth.value = cal
    }

    private fun monthStart(cal: Calendar) = (cal.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun monthEnd(cal: Calendar) = (cal.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
    }.timeInMillis
}
